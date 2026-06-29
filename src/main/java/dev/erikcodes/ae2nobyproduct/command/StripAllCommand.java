package dev.erikcodes.ae2nobyproduct.command;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.core.ByproductStripper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * {@code /ae2nobyproduct strip-all}: an operator command that strips byproducts from every Pattern
 * Provider in the AE2 network of the block the player is looking at (block and cable-part forms alike).
 *
 * <p>It is destructive and network-wide, so it is two-step: the first run <em>previews</em> how many
 * patterns would change (no mutation) and arms the player against the targeted grid; a second run on
 * that same grid within {@link #CONFIRM_TICKS} applies it. Aiming at a different network, or letting
 * the window lapse, just re-previews. Loader-agnostic: registered via Architectury and using only AE2
 * grid API that is identical across the supported AE2 versions.
 */
public final class StripAllCommand {
    private StripAllCommand() {}

    /** How far the player can be from the targeted network block. */
    private static final double REACH = 8.0;
    /** Confirmation window: 30 seconds at 20 ticks/second. */
    private static final long CONFIRM_TICKS = 600L;

    // The grid is held weakly and the map is pruned on expiry, so a one-off preview never pins an AE2
    // grid past the confirmation window (ARMED is static and lives for the server's whole run).
    private record Armed(long expiresAtTick, WeakReference<IGrid> grid) {}

    private static final Map<UUID, Armed> ARMED = new HashMap<>();

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> register(dispatcher));
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(CommonMod.MOD_ID)
            .then(Commands.literal("strip-all")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> run(ctx.getSource()))));
    }

    private static int run(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.translatable("command.ae2nobyproduct.strip_all.not_a_player"));
            return 0;
        }
        ServerLevel level = player.serverLevel();

        IGrid grid = targetedGrid(player, level);
        if (grid == null) {
            source.sendFailure(Component.translatable("command.ae2nobyproduct.strip_all.no_network"));
            return 0;
        }

        List<PatternProviderLogicHost> providers = patternProviders(grid);
        long now = level.getGameTime();
        // Drop expired arming entries first, so nothing holds a grid past its 30s window.
        ARMED.values().removeIf(a -> now > a.expiresAtTick());
        Armed armed = ARMED.get(player.getUUID());
        // armed is non-expired (just pruned); confirm only if it is still the same, live grid.
        boolean confirmed = armed != null && armed.grid().get() == grid;

        if (confirmed) {
            ARMED.remove(player.getUUID());
            int[] tally = sweep(providers, level, true);
            int patterns = tally[0];
            int touched = tally[1];
            source.sendSuccess(
                () -> Component.translatable("command.ae2nobyproduct.strip_all.done", patterns, touched), true);
            return patterns;
        }

        // First run (or expired / different network): preview only, no mutation.
        int[] tally = sweep(providers, level, false);
        int patterns = tally[0];
        int touched = tally[1];
        if (patterns == 0) {
            ARMED.remove(player.getUUID());
            source.sendSuccess(
                () -> Component.translatable("command.ae2nobyproduct.strip_all.nothing"), false);
            return 0;
        }
        ARMED.put(player.getUUID(), new Armed(now + CONFIRM_TICKS, new WeakReference<>(grid)));
        source.sendSuccess(
            () -> Component.translatable("command.ae2nobyproduct.strip_all.preview", patterns, touched), false);
        return patterns;
    }

    /** Run the stripper over every provider. Returns {patternCount, providerCount}. */
    private static int[] sweep(List<PatternProviderLogicHost> providers, ServerLevel level, boolean apply) {
        int patterns = 0;
        int touched = 0;
        for (PatternProviderLogicHost host : providers) {
            int n = ByproductStripper.strip(host.getLogic().getPatternInv(), level, apply);
            if (n > 0) {
                patterns += n;
                touched++;
                if (apply) {
                    host.getLogic().saveChanges();
                }
            }
        }
        return new int[] { patterns, touched };
    }

    /** The AE2 grid of the block the player is looking at, or {@code null} if it is not on a network. */
    private static IGrid targetedGrid(ServerPlayer player, ServerLevel level) {
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 end = eye.add(player.getViewVector(1.0f).scale(REACH));
        BlockHitResult hit = level.clip(
            new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        IInWorldGridNodeHost host = GridHelper.getNodeHost(level, hit.getBlockPos());
        if (host == null) {
            return null;
        }
        // Block machines expose their node with a null side; cables/parts expose it per face. Try both.
        IGridNode node = host.getGridNode(null);
        if (node == null) {
            for (Direction side : Direction.values()) {
                node = host.getGridNode(side);
                if (node != null) {
                    break;
                }
            }
        }
        return node != null ? node.getGrid() : null;
    }

    /** Every Pattern Provider (block + cable-part form) active in the grid. */
    private static List<PatternProviderLogicHost> patternProviders(IGrid grid) {
        List<PatternProviderLogicHost> out = new ArrayList<>();
        for (Class<?> machineClass : grid.getMachineClasses()) {
            if (PatternProviderLogicHost.class.isAssignableFrom(machineClass)) {
                for (Object machine : grid.getActiveMachines(machineClass)) {
                    if (machine instanceof PatternProviderLogicHost host) {
                        out.add(host);
                    }
                }
            }
        }
        return out;
    }
}

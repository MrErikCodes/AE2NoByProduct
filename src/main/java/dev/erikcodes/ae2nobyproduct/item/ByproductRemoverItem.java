package dev.erikcodes.ae2nobyproduct.item;

import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.core.ByproductStripper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Reusable tool that strips byproducts from every processing pattern stored in a single Pattern
 * Provider. Shift + right-click a Pattern Provider, either the full block or the flat cable-mounted
 * panel ({@code ae2:cable_pattern_provider}): each PROCESSING pattern with more than one output is
 * re-encoded keeping only its first (primary) output. Server-side only. The network-wide equivalent is
 * the {@code /ae2nobyproduct strip-all} command; both share {@link ByproductStripper}.
 *
 * <p>Config is read from the shared {@link ByproductConfig}, so it behaves identically on every loader.
 */
public class ByproductRemoverItem extends Item {

    public ByproductRemoverItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        // All logic is server-side. Tell the client the interaction succeeded so the
        // arm-swing/animation plays without running gameplay logic twice.
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        ByproductConfig cfg = ByproductConfig.get();
        if (!cfg.enableFeature()) {
            notify(player, "message.ae2nobyproduct.disabled");
            return InteractionResult.CONSUME;
        }

        PatternProviderLogicHost provider = resolveProvider(level, context);
        if (provider == null) {
            return InteractionResult.PASS;
        }

        int cleaned = ByproductStripper.strip(provider.getLogic().getPatternInv(), level, true);

        if (cleaned > 0) {
            // Persist the edited inventory. saveChanges() covers both forms: a Pattern Provider block
            // entity flags itself setChanged(), a cable part marks its cable-bus host for save.
            provider.saveChanges();
            notify(player, "message.ae2nobyproduct.removed", cleaned);
            if (cfg.consumeOnUse()) {
                context.getItemInHand().shrink(1);
            }
        } else {
            notify(player, "message.ae2nobyproduct.none");
        }

        return InteractionResult.CONSUME;
    }

    /**
     * Resolves the Pattern Provider the player clicked, supporting both forms AE2 ships:
     * the full block (a {@link PatternProviderLogicHost} block entity, including add-on block
     * providers) and the flat cable-mounted panel ({@code ae2:cable_pattern_provider}), which is an
     * {@code IPart} on a cable-bus host rather than a block entity. Returns null if the clicked block
     * is neither.
     */
    private static PatternProviderLogicHost resolveProvider(Level level, UseOnContext context) {
        BlockEntity be = level.getBlockEntity(context.getClickedPos());
        if (be instanceof PatternProviderLogicHost provider) {
            return provider;
        }
        if (be instanceof IPartHost partHost) {
            // Map the precise world hit location to the part on that face. selectPartWorld converts to
            // host-local coordinates internally; SelectedPart.part is null for facade/empty hits.
            SelectedPart selected = partHost.selectPartWorld(context.getClickLocation());
            if (selected.part instanceof PatternProviderLogicHost provider) {
                return provider;
            }
        }
        return null;
    }

    /** Sends a chat message to the player, unless messages are disabled in the config. */
    private static void notify(Player player, String key, Object... args) {
        if (ByproductConfig.get().showMessages()) {
            player.displayClientMessage(Component.translatable(key, args), false);
        }
    }
}

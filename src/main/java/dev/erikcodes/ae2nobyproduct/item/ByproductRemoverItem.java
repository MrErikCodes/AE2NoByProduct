package dev.erikcodes.ae2nobyproduct.item;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.crafting.pattern.AEProcessingPattern;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Reusable tool that strips byproducts from every processing pattern stored in a
 * Pattern Provider. Right-click a Pattern Provider block: each PROCESSING pattern
 * with more than one output is re-encoded keeping only its first (primary) output.
 * Crafting / smithing / stonecutting patterns are left untouched. Server-side only.
 *
 * <p>The AE2 ({@code appeng.*}) symbols it uses keep stable names across loaders, and Minecraft
 * references are remapped per platform by the Architectury build. Config is read from the shared
 * {@link ByproductConfig}, so it behaves identically on every loader.
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

        BlockEntity be = level.getBlockEntity(context.getClickedPos());
        if (!(be instanceof PatternProviderBlockEntity provider)) {
            return InteractionResult.PASS;
        }

        int cleaned = stripByproducts(provider, level);

        if (cleaned > 0) {
            // Persist the edited inventory and notify the provider's grid logic.
            provider.getLogic().saveChanges();
            provider.setChanged();
            notify(player, "message.ae2nobyproduct.removed", cleaned);
            if (cfg.consumeOnUse()) {
                context.getItemInHand().shrink(1);
            }
        } else {
            notify(player, "message.ae2nobyproduct.none");
        }

        return InteractionResult.CONSUME;
    }

    /** Sends a chat message to the player, unless messages are disabled in the config. */
    private static void notify(Player player, String key, Object... args) {
        if (ByproductConfig.get().showMessages()) {
            player.displayClientMessage(Component.translatable(key, args), false);
        }
    }

    /**
     * Re-encodes every processing pattern with more than one output, keeping only the
     * first output. Returns the number of patterns that were changed.
     */
    private static int stripByproducts(PatternProviderBlockEntity provider, Level level) {
        InternalInventory inv = provider.getLogic().getPatternInv();
        int cleaned = 0;

        for (int slot = 0; slot < inv.size(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            IPatternDetails details = PatternDetailsHelper.decodePattern(stack, level);
            if (!(details instanceof AEProcessingPattern processing)) {
                // Only processing patterns are touched; crafting/smithing/stonecutting are skipped.
                continue;
            }

            //? if >=1.21 {
            /*java.util.List<GenericStack> sparseOutputs = processing.getSparseOutputs();
            *///?} else {
            GenericStack[] sparseOutputs = processing.getSparseOutputs();
            //?}
            GenericStack firstOutput = null;
            int outputCount = 0;
            for (GenericStack out : sparseOutputs) {
                if (out != null) {
                    outputCount++;
                    if (firstOutput == null) {
                        firstOutput = out;
                    }
                }
            }

            // Nothing to do unless there are byproducts to drop.
            if (outputCount <= 1 || firstOutput == null) {
                continue;
            }

            //? if >=1.21 {
            /*java.util.List<GenericStack> sparseInputs = processing.getSparseInputs();
            ItemStack reencoded = PatternDetailsHelper.encodeProcessingPattern(sparseInputs, java.util.List.of(firstOutput));
            *///?} else {
            GenericStack[] sparseInputs = processing.getSparseInputs();
            ItemStack reencoded = PatternDetailsHelper.encodeProcessingPattern(
                sparseInputs, new GenericStack[] { firstOutput });
            //?}
            // Never overwrite a valid pattern with a failed/empty encode; leave it untouched.
            if (reencoded == null || reencoded.isEmpty()) {
                continue;
            }
            inv.setItemDirect(slot, reencoded);
            cleaned++;
        }

        return cleaned;
    }
}

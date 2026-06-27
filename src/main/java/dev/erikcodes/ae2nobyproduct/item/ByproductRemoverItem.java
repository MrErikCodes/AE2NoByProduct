package dev.erikcodes.ae2nobyproduct.item;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.crafting.pattern.AEProcessingPattern;
import dev.erikcodes.ae2nobyproduct.config.Config;
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

        if (!Config.enableFeature()) {
            player.displayClientMessage(Component.translatable("message.ae2nobyproduct.disabled"), false);
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
            player.displayClientMessage(
                Component.translatable("message.ae2nobyproduct.removed", cleaned), false);
            if (Config.consumeOnUse()) {
                context.getItemInHand().shrink(1);
            }
        } else {
            player.displayClientMessage(Component.translatable("message.ae2nobyproduct.none"), false);
        }

        return InteractionResult.CONSUME;
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

            GenericStack[] sparseOutputs = processing.getSparseOutputs();
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

            GenericStack[] sparseInputs = processing.getSparseInputs();
            ItemStack reencoded = PatternDetailsHelper.encodeProcessingPattern(
                sparseInputs, new GenericStack[] { firstOutput });
            inv.setItemDirect(slot, reencoded);
            cleaned++;
        }

        return cleaned;
    }
}

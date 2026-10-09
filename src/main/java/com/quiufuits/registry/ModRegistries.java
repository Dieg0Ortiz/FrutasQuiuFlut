package com.quiufuits.registry;

import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.items.FruitItem;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Callbacks globales: semilla con hacha + combustible de maderas.
 */
public final class ModRegistries {
	private ModRegistries() {
	}

	public static void registerCallbacks() {
		FuelRegistry.INSTANCE.add(ModBlocks.QUIU_OAK_LOG, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.QUIU_OAK_PLANKS, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.ABEDUL_LOG, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.ABEDUL_PLANKS, 300);

		// Semilla: Hacha en mano principal + fruta en secundaria
		UseItemCallback.EVENT.register((player, level, hand) -> {
			InteractionResult result = tryExtractSeed(player, level, hand);
			ItemStack stack = player.getItemInHand(hand);
			if (result.consumesAction()) {
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
			return InteractionResultHolder.pass(stack);
		});

		UseBlockCallback.EVENT.register((player, level, hand, hit) ->
				tryExtractSeed(player, level, hand));
	}

	private static InteractionResult tryExtractSeed(Player player, Level level, InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		if (!(main.getItem() instanceof AxeItem) || !(off.getItem() instanceof FruitItem fruitItem)) {
			return InteractionResult.PASS;
		}

		FruitDefinition def = fruitItem.definition();
		if (!def.hasSeed() || def.seedItem() == null) {
			return InteractionResult.PASS;
		}

		if (!level.isClientSide) {
			ItemStack seed = new ItemStack(def.seedItem());
			if (!player.getInventory().add(seed)) {
				player.drop(seed, false);
			}
			off.shrink(1);
			main.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
			level.playSound(null, player.blockPosition(), SoundEvents.AXE_STRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		return InteractionResult.sidedSuccess(level.isClientSide());
	}
}

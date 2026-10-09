package com.quiufuits.registry;

import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.fruits.HairExtractMode;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;

/**
 * Extracciones desde ítem en mano secundaria:
 * - Hacha → Semilla
 * - Tijeras → Pelos (p. ej. Huevo Eburia)
 */
public final class ModRegistries {
	private ModRegistries() {
	}

	public static void registerCallbacks() {
		FuelRegistry.INSTANCE.add(ModBlocks.QUIU_OAK_LOG, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.QUIU_OAK_PLANKS, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.ABEDUL_LOG, 300);
		FuelRegistry.INSTANCE.add(ModBlocks.ABEDUL_PLANKS, 300);

		UseItemCallback.EVENT.register((player, level, hand) -> {
			InteractionResult result = tryExtract(player, level, hand);
			ItemStack stack = player.getItemInHand(hand);
			if (result.consumesAction()) {
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
			return InteractionResultHolder.pass(stack);
		});

		UseBlockCallback.EVENT.register((player, level, hand, hit) ->
				tryExtract(player, level, hand));
	}

	private static InteractionResult tryExtract(Player player, Level level, InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND) {
			return InteractionResult.PASS;
		}

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		if (!(off.getItem() instanceof FruitItem fruitItem)) {
			return InteractionResult.PASS;
		}

		FruitDefinition def = fruitItem.definition();

		// Semilla con Hacha (válido también si la fruta está podrida)
		if (main.getItem() instanceof AxeItem) {
			if (!def.hasSeed() || def.seedItem() == null) {
				return InteractionResult.PASS;
			}
			if (!level.isClientSide) {
				give(player, new ItemStack(def.seedItem()));
				off.shrink(1);
				main.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
				level.playSound(null, player.blockPosition(), SoundEvents.AXE_STRIP, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		// Pelos con Tijeras (referencia: Huevo Eburia)
		if (main.getItem() instanceof ShearsItem || main.is(Items.SHEARS)) {
			if (def.hairExtractMode() != HairExtractMode.SHEARS_OFFHAND || def.hairItem() == null) {
				return InteractionResult.PASS;
			}
			if (!level.isClientSide) {
				give(player, new ItemStack(def.hairItem()));
				off.shrink(1);
				main.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
				level.playSound(null, player.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
			}
			return InteractionResult.sidedSuccess(level.isClientSide());
		}

		return InteractionResult.PASS;
	}

	private static void give(Player player, ItemStack stack) {
		if (!player.getInventory().add(stack)) {
			player.drop(stack, false);
		}
	}
}

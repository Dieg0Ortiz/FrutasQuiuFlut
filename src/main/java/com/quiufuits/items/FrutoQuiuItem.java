package com.quiufuits.items;

import com.quiufuits.fruits.QuiuKeepInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Fruto Quiu: al comerlo, la próxima vez que mueras conservas inventario y experiencia
 * (también si te reviven en hardcore). Se gasta al morir y no se acumula.
 */
public class FrutoQuiuItem extends Item {
	public FrutoQuiuItem(Properties properties) {
		super(properties
				.food(new FoodProperties.Builder().nutrition(10).saturationMod(1.2F).alwaysEat().build())
				.rarity(Rarity.EPIC)
				.stacksTo(16));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (!level.isClientSide && entity instanceof Player player) {
			if (QuiuKeepInventory.isProtected(player)) {
				player.sendSystemMessage(Component.translatable("message.quiufuits.fruto_quiu.already")
						.withStyle(ChatFormatting.YELLOW));
			} else {
				QuiuKeepInventory.protect(player);
				player.sendSystemMessage(Component.translatable("message.quiufuits.fruto_quiu.protected")
						.withStyle(ChatFormatting.GOLD));
				level.playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 0.6F, 1.4F);
			}
		}
		return super.finishUsingItem(stack, level, entity);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.quiufuits.fruto_quiu.tooltip").withStyle(ChatFormatting.GOLD));
		tooltip.add(Component.translatable("item.quiufuits.fruto_quiu.tooltip.once").withStyle(ChatFormatting.GRAY));
	}
}

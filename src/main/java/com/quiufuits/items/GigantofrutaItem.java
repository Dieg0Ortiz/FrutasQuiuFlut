package com.quiufuits.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Gigantofruta: no crece en ningún árbol ni tiene crafteo; solo se consigue en creativo. */
public class GigantofrutaItem extends Item {
	public GigantofrutaItem(Properties properties) {
		super(properties.rarity(Rarity.RARE));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.quiufuits.gigantofruta.tooltip").withStyle(ChatFormatting.GRAY));
	}
}

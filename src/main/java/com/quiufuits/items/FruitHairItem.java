package com.quiufuits.items;

import com.quiufuits.fruits.FruitDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/** Pelos de fruta: no comestibles, solo para crafteo. */
public class FruitHairItem extends Item {
	private final Supplier<FruitDefinition> definition;

	public FruitHairItem(Supplier<FruitDefinition> definition, Properties properties) {
		super(properties);
		this.definition = definition;
	}

	public FruitDefinition definition() {
		return definition.get();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		List<String> lines = definition().hairTooltipLines();
		if (lines.isEmpty()) {
			tooltip.add(Component.translatable("item.quiufuits.hair.tooltip"));
			return;
		}
		for (String lineKey : lines) {
			tooltip.add(Component.translatable(lineKey));
		}
	}
}

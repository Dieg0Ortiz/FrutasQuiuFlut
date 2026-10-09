package com.quiufuits.registry;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.blocks.fruit.FruitStage;
import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.fruits.FruitDefinitions;
import com.quiufuits.items.FruitItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModItemGroups {
	public static final CreativeModeTab MAIN = FabricItemGroup.builder()
			.icon(() -> {
				FruitDefinition solaria = FruitDefinitions.SOLARIA;
				return solaria != null && solaria.fruitItem() != null
						? FruitItem.create(solaria.fruitItem(), FruitStage.RIPE)
						: new ItemStack(ModItems.QUIU_OAK_SAPLING);
			})
			.title(Component.translatable("itemGroup.quiufuits.main"))
			.displayItems((params, entries) -> {
				entries.accept(ModItems.QUIU_OAK_LOG);
				entries.accept(ModItems.STRIPPED_QUIU_OAK_LOG);
				entries.accept(ModItems.QUIU_OAK_WOOD);
				entries.accept(ModItems.STRIPPED_QUIU_OAK_WOOD);
				entries.accept(ModItems.QUIU_OAK_LEAVES);
				entries.accept(ModItems.QUIU_OAK_SAPLING);
				entries.accept(ModItems.QUIU_OAK_PLANKS);

				entries.accept(ModItems.ABEDUL_LOG);
				entries.accept(ModItems.STRIPPED_ABEDUL_LOG);
				entries.accept(ModItems.ABEDUL_WOOD);
				entries.accept(ModItems.STRIPPED_ABEDUL_WOOD);
				entries.accept(ModItems.ABEDUL_LEAVES);
				entries.accept(ModItems.ABEDUL_SAPLING);
				entries.accept(ModItems.ABEDUL_PLANKS);

				for (FruitDefinition fruit : FruitDefinitions.all()) {
					entries.accept(FruitItem.create(fruit.fruitItem(), FruitStage.UNDER_RIPE));
					entries.accept(FruitItem.create(fruit.fruitItem(), FruitStage.RIPE));
					entries.accept(FruitItem.create(fruit.fruitItem(), FruitStage.ROTTEN));
					if (fruit.seedItem() != null) {
						entries.accept(fruit.seedItem());
					}
					if (fruit.hairItem() != null) {
						entries.accept(fruit.hairItem());
					}
				}

				entries.accept(ModItems.QUIU_AMULET);
			})
			.build();

	private ModItemGroups() {
	}

	public static void register() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, QuiuFruitsMod.id("main"), MAIN);
	}
}

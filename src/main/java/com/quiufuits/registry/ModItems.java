package com.quiufuits.registry;

import com.quiufuits.QuiuFruitsMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class ModItems {
	// Wood set items
	public static final Item QUIU_OAK_LOG = blockItem("quiu_oak_log", ModBlocks.QUIU_OAK_LOG);
	public static final Item STRIPPED_QUIU_OAK_LOG = blockItem("stripped_quiu_oak_log", ModBlocks.STRIPPED_QUIU_OAK_LOG);
	public static final Item QUIU_OAK_WOOD = blockItem("quiu_oak_wood", ModBlocks.QUIU_OAK_WOOD);
	public static final Item STRIPPED_QUIU_OAK_WOOD = blockItem("stripped_quiu_oak_wood", ModBlocks.STRIPPED_QUIU_OAK_WOOD);
	public static final Item QUIU_OAK_LEAVES = blockItem("quiu_oak_leaves", ModBlocks.QUIU_OAK_LEAVES);
	public static final Item QUIU_OAK_SAPLING = blockItem("quiu_oak_sapling", ModBlocks.QUIU_OAK_SAPLING);
	public static final Item QUIU_OAK_PLANKS = blockItem("quiu_oak_planks", ModBlocks.QUIU_OAK_PLANKS);

	public static final Item ABEDUL_LOG = blockItem("abedul_log", ModBlocks.ABEDUL_LOG);
	public static final Item STRIPPED_ABEDUL_LOG = blockItem("stripped_abedul_log", ModBlocks.STRIPPED_ABEDUL_LOG);
	public static final Item ABEDUL_WOOD = blockItem("abedul_wood", ModBlocks.ABEDUL_WOOD);
	public static final Item STRIPPED_ABEDUL_WOOD = blockItem("stripped_abedul_wood", ModBlocks.STRIPPED_ABEDUL_WOOD);
	public static final Item ABEDUL_LEAVES = blockItem("abedul_leaves", ModBlocks.ABEDUL_LEAVES);
	public static final Item ABEDUL_SAPLING = blockItem("abedul_sapling", ModBlocks.ABEDUL_SAPLING);
	public static final Item ABEDUL_PLANKS = blockItem("abedul_planks", ModBlocks.ABEDUL_PLANKS);

	/** Amuleto de Quiu — crafteo de ejemplo con pelos de varias frutas. */
	public static final Item QUIU_AMULET = register("quiu_amulet", new Item(new Item.Properties().stacksTo(1)));

	private ModItems() {
	}

	public static void register() {
		// Static init registers wood items; fruit items are registered in FruitDefinitions.
	}

	public static Item register(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, QuiuFruitsMod.id(name), item);
	}

	private static Item blockItem(String name, Block block) {
		return register(name, new BlockItem(block, new Item.Properties()));
	}
}

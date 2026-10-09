package com.quiufuits.registry;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.blocks.tree.AbedulTreeGrower;
import com.quiufuits.blocks.tree.ModSaplingBlock;
import com.quiufuits.blocks.tree.QuiuOakTreeGrower;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	// --- Roble de Quiu ---
	public static final Block QUIU_OAK_LOG = register("quiu_oak_log",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_LOG).mapColor(MapColor.COLOR_ORANGE)));
	public static final Block STRIPPED_QUIU_OAK_LOG = register("stripped_quiu_oak_log",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_LOG).mapColor(MapColor.COLOR_ORANGE)));
	public static final Block QUIU_OAK_WOOD = register("quiu_oak_wood",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).mapColor(MapColor.COLOR_ORANGE)));
	public static final Block STRIPPED_QUIU_OAK_WOOD = register("stripped_quiu_oak_wood",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_OAK_WOOD).mapColor(MapColor.COLOR_ORANGE)));
	public static final Block QUIU_OAK_LEAVES = register("quiu_oak_leaves",
			new LeavesBlock(FabricBlockSettings.copyOf(Blocks.OAK_LEAVES).mapColor(MapColor.COLOR_GREEN)));
	public static final Block QUIU_OAK_SAPLING = register("quiu_oak_sapling",
			new ModSaplingBlock(new QuiuOakTreeGrower(), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING)));
	public static final Block QUIU_OAK_PLANKS = register("quiu_oak_planks",
			new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS).mapColor(MapColor.COLOR_ORANGE)));

	// --- Abedúl ---
	public static final Block ABEDUL_LOG = register("abedul_log",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LOG).mapColor(MapColor.QUARTZ)));
	public static final Block STRIPPED_ABEDUL_LOG = register("stripped_abedul_log",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_BIRCH_LOG).mapColor(MapColor.QUARTZ)));
	public static final Block ABEDUL_WOOD = register("abedul_wood",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_WOOD).mapColor(MapColor.QUARTZ)));
	public static final Block STRIPPED_ABEDUL_WOOD = register("stripped_abedul_wood",
			new RotatedPillarBlock(FabricBlockSettings.copyOf(Blocks.STRIPPED_BIRCH_WOOD).mapColor(MapColor.QUARTZ)));
	public static final Block ABEDUL_LEAVES = register("abedul_leaves",
			new LeavesBlock(FabricBlockSettings.copyOf(Blocks.BIRCH_LEAVES).mapColor(MapColor.PLANT)));
	public static final Block ABEDUL_SAPLING = register("abedul_sapling",
			new ModSaplingBlock(new AbedulTreeGrower(), FabricBlockSettings.copyOf(Blocks.BIRCH_SAPLING)));
	public static final Block ABEDUL_PLANKS = register("abedul_planks",
			new Block(FabricBlockSettings.copyOf(Blocks.BIRCH_PLANKS).mapColor(MapColor.QUARTZ)));

	private ModBlocks() {
	}

	public static void register() {
		StrippableBlockRegistry.register(QUIU_OAK_LOG, STRIPPED_QUIU_OAK_LOG);
		StrippableBlockRegistry.register(QUIU_OAK_WOOD, STRIPPED_QUIU_OAK_WOOD);
		StrippableBlockRegistry.register(ABEDUL_LOG, STRIPPED_ABEDUL_LOG);
		StrippableBlockRegistry.register(ABEDUL_WOOD, STRIPPED_ABEDUL_WOOD);

		registerFlammable(QUIU_OAK_LOG, 5, 5);
		registerFlammable(STRIPPED_QUIU_OAK_LOG, 5, 5);
		registerFlammable(QUIU_OAK_WOOD, 5, 5);
		registerFlammable(STRIPPED_QUIU_OAK_WOOD, 5, 5);
		registerFlammable(QUIU_OAK_PLANKS, 5, 20);
		registerFlammable(QUIU_OAK_LEAVES, 30, 60);

		registerFlammable(ABEDUL_LOG, 5, 5);
		registerFlammable(STRIPPED_ABEDUL_LOG, 5, 5);
		registerFlammable(ABEDUL_WOOD, 5, 5);
		registerFlammable(STRIPPED_ABEDUL_WOOD, 5, 5);
		registerFlammable(ABEDUL_PLANKS, 5, 20);
		registerFlammable(ABEDUL_LEAVES, 30, 60);
	}

	private static void registerFlammable(Block block, int burn, int spread) {
		FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
	}

	public static Block register(String name, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, QuiuFruitsMod.id(name), block);
	}

	/** Settings helper for fruit crops registered from FruitDefinitions. */
	public static BlockBehaviour.Properties fruitSettings() {
		return FabricBlockSettings.create()
				.mapColor(MapColor.COLOR_YELLOW)
				.noCollission()
				.instabreak()
				.sound(SoundType.WOOD)
				.randomTicks();
	}
}

package com.quiufuits.worldgen;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.registry.ModTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

/**
 * Worldgen data-driven + tag de bioma configurable {@link ModTags#CENTRO_QUIU}.
 * Sustituye los biomas del tag cuando exista el Centro de Quiu (sin tocar código Java).
 */
public final class ModWorldGen {
	public static final TreeDecoratorType<FruitTreeDecorator> FRUIT_TREE_DECORATOR =
			Registry.register(
					BuiltInRegistries.TREE_DECORATOR_TYPE,
					QuiuFruitsMod.id("fruit"),
					new TreeDecoratorType<>(FruitTreeDecorator.CODEC)
			);
	public static final TrunkPlacerType<QuiuTallTrunkPlacer> QUIU_TALL_TRUNK_PLACER =
			Registry.register(
					BuiltInRegistries.TRUNK_PLACER_TYPE,
					QuiuFruitsMod.id("quiu_tall_trunk_placer"),
					new TrunkPlacerType<>(QuiuTallTrunkPlacer.CODEC)
			);
	public static final TrunkPlacerType<QuiuPrimitiveTrunkPlacer> QUIU_PRIMITIVE_TRUNK_PLACER =
			Registry.register(
					BuiltInRegistries.TRUNK_PLACER_TYPE,
					QuiuFruitsMod.id("quiu_primitive_trunk_placer"),
					new TrunkPlacerType<>(QuiuPrimitiveTrunkPlacer.CODEC)
			);
	public static final FoliagePlacerType<QuiuPrimitiveFoliagePlacer> QUIU_PRIMITIVE_FOLIAGE_PLACER =
			Registry.register(
					BuiltInRegistries.FOLIAGE_PLACER_TYPE,
					QuiuFruitsMod.id("quiu_primitive_foliage_placer"),
					new FoliagePlacerType<>(QuiuPrimitiveFoliagePlacer.CODEC)
			);
	public static final FoliagePlacerType<QuiuPalmFoliagePlacer> QUIU_PALM_FOLIAGE_PLACER =
			Registry.register(
					BuiltInRegistries.FOLIAGE_PLACER_TYPE,
					QuiuFruitsMod.id("quiu_palm_foliage_placer"),
					new FoliagePlacerType<>(QuiuPalmFoliagePlacer.CODEC)
			);

	private ModWorldGen() {
	}

	public static void register() {
		BiomeModifications.addFeature(
				BiomeSelectors.tag(ModTags.CENTRO_QUIU),
				GenerationStep.Decoration.VEGETAL_DECORATION,
				ModPlacedFeatures.QUIU_OAK_PLACED
		);
		BiomeModifications.addFeature(
				BiomeSelectors.tag(ModTags.CENTRO_QUIU),
				GenerationStep.Decoration.VEGETAL_DECORATION,
				ModPlacedFeatures.ABEDUL_PLACED
		);
		QuiuFruitsMod.LOGGER.info("Worldgen asociado al tag de bioma #quiufuits:centro_quiu (placeholder).");
	}
}

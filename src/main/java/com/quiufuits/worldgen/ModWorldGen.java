package com.quiufuits.worldgen;

import com.quiufuits.QuiuFruitsMod;
import com.quiufuits.registry.ModTags;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

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

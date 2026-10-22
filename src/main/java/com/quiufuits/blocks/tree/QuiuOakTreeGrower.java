package com.quiufuits.blocks.tree;

import com.quiufuits.worldgen.ModConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.Nullable;

/**
 * Un retoño da el Roble Quiu normal; 4 retoños en cuadro (como el pino gigante de vanilla)
 * dan el Roble Quiu gigante.
 */
public class QuiuOakTreeGrower extends AbstractMegaTreeGrower {
	@Nullable
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
		return ModConfiguredFeatures.QUIU_OAK_KEY;
	}

	@Nullable
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) {
		return ModConfiguredFeatures.QUIU_OAK_GIANT_KEY;
	}
}

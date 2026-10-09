package com.quiufuits.worldgen;

import com.quiufuits.QuiuFruitsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public final class ModConfiguredFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> QUIU_OAK_KEY =
			ResourceKey.create(Registries.CONFIGURED_FEATURE, QuiuFruitsMod.id("quiu_oak"));
	public static final ResourceKey<ConfiguredFeature<?, ?>> ABEDUL_KEY =
			ResourceKey.create(Registries.CONFIGURED_FEATURE, QuiuFruitsMod.id("abedul"));

	private ModConfiguredFeatures() {
	}
}

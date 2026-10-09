package com.quiufuits.worldgen;

import com.quiufuits.QuiuFruitsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class ModPlacedFeatures {
	public static final ResourceKey<PlacedFeature> QUIU_OAK_PLACED =
			ResourceKey.create(Registries.PLACED_FEATURE, QuiuFruitsMod.id("quiu_oak_placed"));
	public static final ResourceKey<PlacedFeature> ABEDUL_PLACED =
			ResourceKey.create(Registries.PLACED_FEATURE, QuiuFruitsMod.id("abedul_placed"));

	private ModPlacedFeatures() {
	}
}

package com.quiufuits.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

/**
 * Copa del Roble Quiu: montículo redondeado e irregular (no una losa plana),
 * con borde irregular y algo de hojas colgantes.
 */
public class QuiuPalmFoliagePlacer extends FoliagePlacer {
	public static final Codec<QuiuPalmFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
			foliagePlacerParts(instance).apply(instance, QuiuPalmFoliagePlacer::new));

	public QuiuPalmFoliagePlacer(IntProvider radius, IntProvider offset) {
		super(radius, offset);
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return ModWorldGen.QUIU_PALM_FOLIAGE_PLACER;
	}

	@Override
	protected void createFoliage(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
			TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment,
			int foliageHeight, int foliageRadius, int offset) {
		BlockPos center = attachment.pos().above(offset);
		int radius = Math.min(4, Math.max(2, foliageRadius + attachment.radiusOffset()));

		// Perfil abombado: estrecho arriba → ancho en el medio → cuelga abajo
		placeLeavesRow(level, setter, random, config, center, Math.max(1, radius - 2), 2, false);
		placeLeavesRow(level, setter, random, config, center, Math.max(1, radius - 1), 1, false);
		placeLeavesRow(level, setter, random, config, center, radius, 0, false);
		placeLeavesRowWithHangingLeavesBelow(level, setter, random, config, center, radius, 0, false, 0.25F, 0.1F);
		placeLeavesRowWithHangingLeavesBelow(level, setter, random, config, center, Math.max(1, radius - 1), -1, false, 0.4F, 0.2F);
		if (radius >= 3 && random.nextFloat() < 0.7F) {
			placeLeavesRow(level, setter, random, config, center, Math.max(1, radius - 2), -2, false);
		}
	}

	@Override
	public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
		return 4 + random.nextInt(2);
	}

	@Override
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int range, boolean large) {
		int dist2 = dx * dx + dz * dz;
		// Silueta redonda (no cuadrada) + bordes mordidos
		if (dist2 > range * range + range) {
			return true;
		}
		if (dx == range && dz == range) {
			return true;
		}
		if (dist2 > (range - 1) * (range - 1) && random.nextInt(2) == 0) {
			return true;
		}
		// Más irregularidad en la capa superior para que no se vea “cortada”
		if (y >= 1 && dist2 >= (range - 1) * (range - 1) && random.nextInt(3) != 0) {
			return true;
		}
		return false;
	}
}

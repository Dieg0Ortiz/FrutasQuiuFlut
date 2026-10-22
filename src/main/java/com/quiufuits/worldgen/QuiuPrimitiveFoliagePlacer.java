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
 * Follaje del Abedul Quiu primitivo:
 * <ul>
 *   <li>Radio &gt;= 3: copa en paraguas ancha y aplanada, de bordes irregulares.</li>
 *   <li>Radio &lt; 3: matas pequeñas en las puntas de las ramas de los verticilos.</li>
 * </ul>
 * Ver {@link QuiuPrimitiveTrunkPlacer} para los offsets de radio de cada tipo.
 */
public class QuiuPrimitiveFoliagePlacer extends FoliagePlacer {
	public static final Codec<QuiuPrimitiveFoliagePlacer> CODEC = RecordCodecBuilder.create(instance ->
			foliagePlacerParts(instance).apply(instance, QuiuPrimitiveFoliagePlacer::new));

	public QuiuPrimitiveFoliagePlacer(IntProvider radius, IntProvider offset) {
		super(radius, offset);
	}

	@Override
	protected FoliagePlacerType<?> type() {
		return ModWorldGen.QUIU_PRIMITIVE_FOLIAGE_PLACER;
	}

	@Override
	protected void createFoliage(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
			TreeConfiguration config, int maxFreeTreeHeight, FoliageAttachment attachment,
			int foliageHeight, int foliageRadius, int offset) {
		BlockPos center = attachment.pos().above(offset);
		int radius = foliageRadius + attachment.radiusOffset();
		if (radius >= 3) {
			placeCrown(level, setter, random, config, center, radius, attachment.doubleTrunk());
		} else {
			placePad(level, setter, random, config, center, Math.max(1, radius));
		}
	}

	/** Paraguas: tapa estrecha, dos capas anchas y una capa inferior rala. */
	private void placeCrown(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
			TreeConfiguration config, BlockPos center, int radius, boolean doubleTrunk) {
		placeDisc(level, setter, random, config, center.above(), radius - 3, doubleTrunk, 0.0F);
		placeDisc(level, setter, random, config, center, radius - 1, doubleTrunk, 0.0F);
		placeDisc(level, setter, random, config, center.below(), radius, doubleTrunk, 0.0F);
		placeDisc(level, setter, random, config, center.below(2), radius, doubleTrunk, 0.15F);
		placeDisc(level, setter, random, config, center.below(3), radius - 2, doubleTrunk, 0.45F);
	}

	/** Mata de punta de rama: cruz con esquinas al azar y un poco de altura. */
	private void placePad(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
			TreeConfiguration config, BlockPos center, int radius) {
		placeDisc(level, setter, random, config, center, radius, false, 0.0F);
		placeDisc(level, setter, random, config, center.above(), radius - 1, false, 0.0F);
		if (random.nextBoolean()) {
			tryPlaceLeaf(level, setter, random, config, center.below());
		}
	}

	/**
	 * Disco horizontal de hojas con borde irregular. En troncos 2x2 el centro está
	 * entre los 4 troncos (desplazado medio bloque).
	 */
	private void placeDisc(LevelSimulatedReader level, FoliageSetter setter, RandomSource random,
			TreeConfiguration config, BlockPos center, int radius, boolean doubleTrunk, float holeChance) {
		if (radius < 0) {
			return;
		}
		double shift = doubleTrunk ? 0.5 : 0.0;
		int max = radius + (doubleTrunk ? 1 : 0);
		for (int dx = -radius; dx <= max; dx++) {
			for (int dz = -radius; dz <= max; dz++) {
				double fx = dx - shift;
				double fz = dz - shift;
				double dist = Math.sqrt(fx * fx + fz * fz);
				if (dist > radius + 0.4) {
					continue;
				}
				if (dist > radius - 1.0 && random.nextInt(3) == 0) {
					continue;
				}
				if (holeChance > 0.0F && random.nextFloat() < holeChance) {
					continue;
				}
				tryPlaceLeaf(level, setter, random, config, center.offset(dx, 0, dz));
			}
		}
	}

	@Override
	public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
		return 5;
	}

	@Override
	protected boolean shouldSkipLocation(RandomSource random, int dx, int y, int dz, int range, boolean large) {
		return false;
	}
}

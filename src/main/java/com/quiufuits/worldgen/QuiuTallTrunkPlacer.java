package com.quiufuits.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Árbol del Centro de Quiu: tronco cuadrado (2x2 normal, 3x3 el gigante) de la base a la punta,
 * raíces en la base, tronco limpio abajo y copa en la parte superior. Las ramas salen en espiral
 * alrededor del tronco, más largas abajo y más cortas arriba.
 * Raíces, nudos y ramas usan el bloque de madera para no mostrar el corte del tronco.
 */
public class QuiuTallTrunkPlacer extends TrunkPlacer {
	public static final Codec<QuiuTallTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
			trunkPlacerParts(instance)
					.and(BlockStateProvider.CODEC.fieldOf("wood_provider").forGetter(p -> p.woodProvider))
					.and(Codec.intRange(2, 3).optionalFieldOf("trunk_width", 2).forGetter(p -> p.width))
					.apply(instance, QuiuTallTrunkPlacer::new));

	private final BlockStateProvider woodProvider;
	/** Lado del tronco cuadrado: 2 = Roble Quiu normal, 3 = gigante (4 retoños). */
	private final int width;

	public QuiuTallTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockStateProvider woodProvider,
			int width) {
		super(baseHeight, heightRandA, heightRandB);
		this.woodProvider = woodProvider;
		this.width = width;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return ModWorldGen.QUIU_TALL_TRUNK_PLACER;
	}

	/**
	 * Normal: 30% chicos, 45% medianos y 25% altísimos.
	 * Gigante: 1.5x la altura (1.7x el 40% de las veces); se escala aquí porque base_height
	 * solo admite hasta 32 en el JSON.
	 */
	@Override
	public int getTreeHeight(RandomSource random) {
		int height = super.getTreeHeight(random);
		float size = random.nextFloat();
		if (width >= 3) {
			return size > 0.6F ? height * 17 / 10 : height * 3 / 2;
		}
		if (size < 0.3F) {
			return height * 6 / 10;
		}
		if (size > 0.75F) {
			return height * 13 / 10;
		}
		return height;
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int height,
			BlockPos pos, TreeConfiguration config) {
		BlockPos top = pos.offset(random.nextInt(width), 0, random.nextInt(width));

		for (int dx = 0; dx < width; dx++) {
			for (int dz = 0; dz < width; dz++) {
				setDirtAt(level, blockSetter, random, pos.offset(dx, -1, dz), config);
			}
		}
		for (int y = 0; y < height; y++) {
			for (int dx = 0; dx < width; dx++) {
				for (int dz = 0; dz < width; dz++) {
					placeLog(level, blockSetter, random, pos.offset(dx, y, dz), config);
				}
			}
		}

		placeRoots(level, blockSetter, random, pos);
		placeKnots(level, blockSetter, random, pos, height);

		List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
		attachments.add(new FoliagePlacer.FoliageAttachment(top.above(height - 1), width - 1, false));

		// El gigante tiene más ramas y más largas
		int extra = width - 2;
		int crownStart = height * (40 + random.nextInt(16)) / 100;
		int crownSpan = height - 3 - crownStart;
		int branches = Math.min(height / 6 + 2 + random.nextInt(3) + extra * 2, crownSpan / 2 + 1);
		boolean clockwise = random.nextBoolean();
		Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);

		for (int i = 0; i < branches; i++) {
			float t = branches == 1 ? 0 : i / (float) (branches - 1);
			int y = crownStart + Math.round(t * crownSpan);
			BlockPos start = edgeCell(pos, dir).above(y);
			int length = Math.max(2, Math.round(5 - 3 * t) + random.nextInt(2) + extra * 2);
			BlockPos end = placeBranch(level, blockSetter, random, start, dir, length);
			attachments.add(new FoliagePlacer.FoliageAttachment(end, extra, false));

			if (length >= 4 && random.nextFloat() < 0.35F + extra * 0.25F) {
				Direction side = random.nextBoolean() ? dir.getClockWise() : dir.getCounterClockWise();
				BlockPos fork = placeBranch(level, blockSetter, random, end, side, 2 + extra);
				attachments.add(new FoliagePlacer.FoliageAttachment(fork, extra - 1, false));
			}

			dir = clockwise ? dir.getClockWise() : dir.getCounterClockWise();
			if (random.nextFloat() < 0.25F) {
				dir = clockwise ? dir.getClockWise() : dir.getCounterClockWise();
			}
		}

		// Relleno alrededor del tronco en la parte alta para que la copa se vea densa
		for (int y = height - 5; y > crownStart + crownSpan / 2; y -= 5 + random.nextInt(2)) {
			BlockPos center = pos.offset(random.nextInt(width), y, random.nextInt(width));
			attachments.add(new FoliagePlacer.FoliageAttachment(center, extra - 1, false));
		}
		return attachments;
	}

	/** Celda del borde del tronco que mira hacia {@code dir}. */
	private BlockPos edgeCell(BlockPos pos, Direction dir) {
		int x = dir.getStepX() > 0 ? width - 1 : dir.getStepX() < 0 ? 0 : (width - 1) / 2;
		int z = dir.getStepZ() > 0 ? width - 1 : dir.getStepZ() < 0 ? 0 : (width - 1) / 2;
		return pos.offset(x, 0, z);
	}

	private void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos pos) {
		for (int cx = 0; cx < width; cx++) {
			for (int cz = 0; cz < width; cz++) {
				BlockPos cell = pos.offset(cx, 0, cz);
				for (Direction dir : Direction.Plane.HORIZONTAL) {
					BlockPos out = cell.relative(dir);
					int ox = out.getX() - pos.getX(), oz = out.getZ() - pos.getZ();
					if (ox >= 0 && ox < width && oz >= 0 && oz < width) {
						continue;
					}
					if (random.nextFloat() > 0.6F) {
						continue;
					}
					int rootHeight = 1 + random.nextInt(3 + width - 2);
					for (int h = 0; h < rootHeight; h++) {
						placeWood(level, blockSetter, random, out.above(h));
					}
					if (rootHeight >= 2 && random.nextFloat() < 0.4F) {
						placeWood(level, blockSetter, random, out.relative(dir));
					}
				}
			}
		}
	}

	private void placeKnots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos pos, int height) {
		for (int y = 4; y < height - 6; y++) {
			if (random.nextFloat() > 0.04F) {
				continue;
			}
			Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
			placeWood(level, blockSetter, random, edgeCell(pos, dir).above(y).relative(dir));
		}
	}

	/** Sale horizontal los primeros bloques y luego sube en escalera conectada. */
	private BlockPos placeBranch(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos start, Direction dir, int length) {
		BlockPos cur = start;
		for (int k = 1; k <= length; k++) {
			cur = cur.relative(dir);
			placeWood(level, blockSetter, random, cur);
			if (k >= 3 || (k == 2 && random.nextBoolean())) {
				cur = cur.above();
				placeWood(level, blockSetter, random, cur);
			}
		}
		return cur;
	}

	private void placeWood(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos pos) {
		if (validTreePos(level, pos)) {
			blockSetter.accept(pos, woodProvider.getState(random, pos));
		}
	}
}

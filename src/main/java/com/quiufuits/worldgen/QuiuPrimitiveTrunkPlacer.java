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
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Abedul Quiu — árbol primitivo (estilo araucaria / edad de los dinosaurios):
 * <ul>
 *   <li>Tronco muy alto y recto, 1x1 o grueso 2x2, con un escalón ocasional en la parte alta.</li>
 *   <li>Raíces/contrafuertes de tronco en la base.</li>
 *   <li>Verticilos de ramas horizontales en cruz en la mitad superior, con matas de hojas en las puntas.</li>
 *   <li>Brazos radiales bajo la copa que sostienen un paraguas ancho y plano.</li>
 * </ul>
 * Todos los bloques (también ramas y raíces) son troncos verticales: desde abajo
 * las ramas muestran los anillos del corte, como en las referencias.
 */
public class QuiuPrimitiveTrunkPlacer extends TrunkPlacer {
	public static final Codec<QuiuPrimitiveTrunkPlacer> CODEC = RecordCodecBuilder.create(instance ->
			trunkPlacerParts(instance)
					.and(Codec.floatRange(0.0F, 1.0F).fieldOf("thick_chance").forGetter(p -> p.thickChance))
					.apply(instance, QuiuPrimitiveTrunkPlacer::new));

	/** Radio extra de la copa respecto al radio base del foliage placer. */
	public static final int CROWN_RADIUS_OFFSET = 3;
	/** Las matas de las puntas de rama usan radio base - 1. */
	public static final int PAD_RADIUS_OFFSET = -1;

	private final float thickChance;

	public QuiuPrimitiveTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, float thickChance) {
		super(baseHeight, heightRandA, heightRandB);
		this.thickChance = thickChance;
	}

	@Override
	protected TrunkPlacerType<?> type() {
		return ModWorldGen.QUIU_PRIMITIVE_TRUNK_PLACER;
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level,
			BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, int height,
			BlockPos pos, TreeConfiguration config) {
		boolean thick = random.nextFloat() < thickChance;
		int width = thick ? 2 : 1;
		for (int dx = 0; dx < width; dx++) {
			for (int dz = 0; dz < width; dz++) {
				setDirtAt(level, blockSetter, random, pos.offset(dx, -1, dz), config);
			}
		}

		List<BlockPos> column = placeColumn(level, blockSetter, random, height, pos, config, width);
		placeRoots(level, blockSetter, random, pos, width, config);

		List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
		int crownArmY = height - 2;

		// Verticilos de ramas: más largos abajo, más cortos hacia la copa
		int y = height * 50 / 100 + random.nextInt(2);
		while (y < crownArmY - 1) {
			BlockPos anchor = column.get(y);
			float progress = (y - height * 0.5F) / Math.max(1F, crownArmY - height * 0.5F);
			int maxLen = (thick ? 4 : 3) - Math.round(progress * 1.5F);
			for (Direction dir : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() > 0.75F) {
					continue;
				}
				int len = 1 + random.nextInt(Math.max(1, maxLen));
				BlockPos tip = placeArm(level, blockSetter, random, config, edgeOf(anchor, dir, width, random), dir, len, true);
				attachments.add(new FoliagePlacer.FoliageAttachment(tip, PAD_RADIUS_OFFSET, false));
			}
			y += 2 + random.nextInt(2);
		}

		// Brazos radiales bajo la copa: sostienen el paraguas (evitan que las hojas se caigan)
		BlockPos crownAnchor = column.get(crownArmY);
		int armLen = (thick ? 4 : 3);
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			placeArm(level, blockSetter, random, config, edgeOf(crownAnchor, dir, width, random), dir,
					armLen - random.nextInt(2), false);
			placeDiagonalArm(level, blockSetter, random, config, edgeOf(crownAnchor, dir, width, random), dir,
					armLen - 1);
		}

		BlockPos top = column.get(column.size() - 1);
		attachments.add(new FoliagePlacer.FoliageAttachment(top.above(), CROWN_RADIUS_OFFSET + (thick ? 1 : 0), thick));
		return attachments;
	}

	/** Tronco recto; con un 35% hace un escalón de 1 bloque entre el 55% y el 75% de la altura. */
	private List<BlockPos> placeColumn(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, int height, BlockPos pos, TreeConfiguration config, int width) {
		List<BlockPos> column = new ArrayList<>(height);
		int jogY = random.nextFloat() < 0.35F
				? height * 55 / 100 + random.nextInt(Math.max(1, height * 20 / 100))
				: -1;
		Direction jogDir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
		BlockPos.MutableBlockPos cursor = pos.mutable();

		for (int y = 0; y < height; y++) {
			if (y == jogY) {
				for (int dx = 0; dx < width; dx++) {
					for (int dz = 0; dz < width; dz++) {
						BlockPos cell = cursor.offset(dx, 0, dz);
						placeLog(level, blockSetter, random, cell, config);
						placeLog(level, blockSetter, random, cell.relative(jogDir), config);
					}
				}
				cursor.move(jogDir);
				cursor.move(Direction.UP);
				column.add(cursor.below().immutable());
				continue;
			}
			for (int dx = 0; dx < width; dx++) {
				for (int dz = 0; dz < width; dz++) {
					placeLog(level, blockSetter, random, cursor.offset(dx, 0, dz), config);
				}
			}
			column.add(cursor.immutable());
			cursor.move(Direction.UP);
		}
		return column;
	}

	/** Contrafuertes de 1–2 bloques alrededor de la base; bajan si el terreno está en pendiente. */
	private void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos pos, int width, TreeConfiguration config) {
		for (Direction dir : Direction.Plane.HORIZONTAL) {
			for (int i = 0; i < width; i++) {
				if (random.nextFloat() > 0.5F) {
					continue;
				}
				BlockPos root = edgeAt(pos, dir, width, i).relative(dir);
				int rootHeight = 1 + random.nextInt(2);
				for (int h = 0; h < rootHeight; h++) {
					placeLog(level, blockSetter, random, root.above(h), config);
				}
				sinkToGround(level, blockSetter, random, root, config);
				if (rootHeight == 2 && random.nextFloat() < 0.4F) {
					BlockPos outer = root.relative(dir);
					placeLog(level, blockSetter, random, outer, config);
					sinkToGround(level, blockSetter, random, outer, config);
				}
			}
		}
	}

	private void sinkToGround(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, BlockPos pos, TreeConfiguration config) {
		BlockPos below = pos.below();
		for (int i = 0; i < 3 && validTreePos(level, below); i++) {
			placeLog(level, blockSetter, random, below, config);
			below = below.below();
		}
	}

	/** Rama horizontal; la punta puede subir un bloque. Devuelve la punta. */
	private BlockPos placeArm(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, TreeConfiguration config, BlockPos start, Direction dir, int length,
			boolean mayRise) {
		BlockPos cur = start;
		for (int i = 0; i < length; i++) {
			cur = cur.relative(dir);
			placeLog(level, blockSetter, random, cur, config);
		}
		if (mayRise && length >= 2 && random.nextFloat() < 0.3F) {
			cur = cur.above();
			placeLog(level, blockSetter, random, cur, config);
		}
		return cur;
	}

	private void placeDiagonalArm(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
			RandomSource random, TreeConfiguration config, BlockPos start, Direction dir, int length) {
		Direction side = dir.getClockWise();
		BlockPos cur = start;
		for (int i = 0; i < length; i++) {
			cur = cur.relative(dir).relative(side);
			placeLog(level, blockSetter, random, cur, config);
		}
	}

	/** Bloque del borde del tronco por el que sale una rama en {@code dir} (aleatorio si es 2x2). */
	private static BlockPos edgeOf(BlockPos anchor, Direction dir, int width, RandomSource random) {
		return edgeAt(anchor, dir, width, width > 1 ? random.nextInt(width) : 0);
	}

	private static BlockPos edgeAt(BlockPos anchor, Direction dir, int width, int index) {
		int far = width - 1;
		return switch (dir) {
			case NORTH -> anchor.offset(index, 0, 0);
			case SOUTH -> anchor.offset(index, 0, far);
			case WEST -> anchor.offset(0, 0, index);
			default -> anchor.offset(far, 0, index);
		};
	}
}

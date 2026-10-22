package com.quiufuits.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.blocks.fruit.FruitStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Coloca un único tipo de fruta por árbol (elige 1 de {@code fruit_blocks} al azar,
 * p. ej. 50/50). Se puede cultivar otra fruta compatible a mano; natural no mezcla.
 */
public class FruitTreeDecorator extends TreeDecorator {
	public static final Codec<FruitTreeDecorator> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability),
					BuiltInRegistries.BLOCK.byNameCodec().listOf().fieldOf("fruit_blocks").forGetter(d -> d.fruitBlocks),
					Codec.floatRange(0.0F, 1.0F).optionalFieldOf("min_height_fraction", 0.45F).forGetter(d -> d.minHeightFraction),
					Codec.floatRange(1.0F, 20.0F).optionalFieldOf("sheltered_multiplier", 1.0F).forGetter(d -> d.shelteredMultiplier)
			).apply(instance, FruitTreeDecorator::new));

	private final float probability;
	private final List<Block> fruitBlocks;
	/** Fracción de la altura del árbol desde la que pueden salir frutas. */
	private final float minHeightFraction;
	/** Multiplica la probabilidad cuando hay una rama justo encima (racimos bajo las ramas). */
	private final float shelteredMultiplier;

	public FruitTreeDecorator(float probability, List<Block> fruitBlocks, float minHeightFraction,
			float shelteredMultiplier) {
		this.probability = probability;
		this.fruitBlocks = List.copyOf(fruitBlocks);
		this.minHeightFraction = minHeightFraction;
		this.shelteredMultiplier = shelteredMultiplier;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return ModWorldGen.FRUIT_TREE_DECORATOR;
	}

	@Override
	public void place(Context context) {
		if (fruitBlocks.isEmpty()) {
			return;
		}
		RandomSource random = context.random();
		Block chosen = fruitBlocks.get(random.nextInt(fruitBlocks.size()));
		if (!(chosen instanceof FruitCropBlock crop)) {
			return;
		}

		List<BlockPos> logs = context.logs();
		List<BlockPos> leaves = context.leaves();
		if (logs.isEmpty()) {
			return;
		}

		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		for (BlockPos logPos : logs) {
			minY = Math.min(minY, logPos.getY());
			maxY = Math.max(maxY, logPos.getY());
		}
		int trunkSpan = Math.max(1, maxY - minY);
		int minFruitY = minY + Math.round(trunkSpan * minHeightFraction);

		Set<BlockPos> nearLeaves = new HashSet<>();
		for (BlockPos leaf : leaves) {
			for (Direction direction : Direction.values()) {
				nearLeaves.add(leaf.relative(direction));
			}
			nearLeaves.add(leaf.below());
		}

		Set<BlockPos> logSet = new HashSet<>(logs);
		// Árbol crecido de un retoño (polvo de hueso o tiempo): no está en una WorldGenRegion
		boolean fromSapling = !(context.level() instanceof WorldGenRegion);
		float shelteredProbability = Math.min(1.0F, probability * shelteredMultiplier);

		for (BlockPos logPos : logs) {
			boolean inCanopyZone = logPos.getY() >= minFruitY;
			boolean touchesLeaves = nearLeaves.contains(logPos);
			if (!inCanopyZone && !touchesLeaves) {
				continue;
			}
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				BlockPos fruitPos = logPos.relative(direction);
				float chance = logSet.contains(fruitPos.above()) ? shelteredProbability : probability;
				if (random.nextFloat() > chance) {
					continue;
				}
				if (!context.isAir(fruitPos)) {
					continue;
				}
				Direction facing = direction.getOpposite();
				BlockState state = crop.defaultBlockState()
						.setValue(FruitCropBlock.FACING, facing)
						.setValue(FruitCropBlock.STAGE, randomStage(random, fromSapling));
				if (context.level().isStateAtPosition(logPos, s -> s.is(crop.definition().plantableOnLogs()))) {
					context.setBlock(fruitPos, state);
				}
			}
		}
	}

	/**
	 * Árboles del mundo: 50% creciendo, 30% poco maduras, 20% maduras.
	 * Árboles de retoño: solo creciendo (60%) o poco maduras (40%); nunca maduras ni podridas.
	 */
	private static FruitStage randomStage(RandomSource random, boolean fromSapling) {
		int roll = random.nextInt(10);
		if (fromSapling) {
			return roll < 6 ? FruitStage.GROWING : FruitStage.UNDER_RIPE;
		}
		if (roll < 5) {
			return FruitStage.GROWING;
		}
		if (roll < 8) {
			return FruitStage.UNDER_RIPE;
		}
		return FruitStage.RIPE;
	}
}

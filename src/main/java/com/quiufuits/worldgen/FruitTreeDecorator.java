package com.quiufuits.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.blocks.fruit.FruitStage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.List;

/**
 * Coloca frutas en los troncos del árbol generado (estilo cacao).
 */
public class FruitTreeDecorator extends TreeDecorator {
	public static final Codec<FruitTreeDecorator> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(d -> d.probability),
					BuiltInRegistries.BLOCK.byNameCodec().fieldOf("fruit_block").forGetter(d -> d.fruitBlock)
			).apply(instance, FruitTreeDecorator::new));

	private final float probability;
	private final Block fruitBlock;

	public FruitTreeDecorator(float probability, Block fruitBlock) {
		this.probability = probability;
		this.fruitBlock = fruitBlock;
	}

	@Override
	protected TreeDecoratorType<?> type() {
		return ModWorldGen.FRUIT_TREE_DECORATOR;
	}

	@Override
	public void place(Context context) {
		if (!(fruitBlock instanceof FruitCropBlock crop)) {
			return;
		}
		RandomSource random = context.random();
		List<BlockPos> logs = context.logs();
		if (logs.isEmpty()) {
			return;
		}

		for (BlockPos logPos : logs) {
			for (Direction direction : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() > probability) {
					continue;
				}
				BlockPos fruitPos = logPos.relative(direction);
				if (!context.isAir(fruitPos)) {
					continue;
				}
				Direction facing = direction.getOpposite();
				BlockState state = crop.defaultBlockState()
						.setValue(FruitCropBlock.FACING, facing)
						.setValue(FruitCropBlock.STAGE, randomStage(random));
				// LevelSimulatedReader no es LevelReader: validamos el tronco vía predicate
				if (context.level().isStateAtPosition(logPos, s -> s.is(crop.definition().plantableOnLogs()))) {
					context.setBlock(fruitPos, state);
				}
			}
		}
	}

	private static FruitStage randomStage(RandomSource random) {
		int roll = random.nextInt(10);
		if (roll < 5) {
			return FruitStage.GROWING;
		}
		if (roll < 8) {
			return FruitStage.UNDER_RIPE;
		}
		return FruitStage.RIPE;
	}
}

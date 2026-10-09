package com.quiufuits.items;

import com.quiufuits.blocks.fruit.FruitCropBlock;
import com.quiufuits.blocks.fruit.FruitStage;
import com.quiufuits.fruits.FruitDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class FruitSeedItem extends Item {
	private final Supplier<FruitDefinition> definition;

	public FruitSeedItem(Supplier<FruitDefinition> definition, Properties properties) {
		super(properties);
		this.definition = definition;
	}

	public FruitDefinition definition() {
		return definition.get();
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos clicked = context.getClickedPos();
		Direction face = context.getClickedFace();
		BlockState logState = level.getBlockState(clicked);

		if (!face.getAxis().isHorizontal()) {
			return InteractionResult.FAIL;
		}
		if (!logState.is(definition().plantableOnLogs())) {
			return InteractionResult.FAIL;
		}

		BlockPos placePos = clicked.relative(face);
		if (!level.getBlockState(placePos).canBeReplaced()) {
			return InteractionResult.FAIL;
		}

		FruitCropBlock crop = definition().cropBlock();
		BlockState placed = crop.defaultBlockState()
				.setValue(FruitCropBlock.FACING, face.getOpposite())
				.setValue(FruitCropBlock.STAGE, FruitStage.GROWING);

		if (!placed.canSurvive(level, placePos)) {
			return InteractionResult.FAIL;
		}

		if (!level.isClientSide) {
			level.setBlock(placePos, placed, 3);
			level.playSound(null, placePos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
			if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
				context.getItemInHand().shrink(1);
			}
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}
}

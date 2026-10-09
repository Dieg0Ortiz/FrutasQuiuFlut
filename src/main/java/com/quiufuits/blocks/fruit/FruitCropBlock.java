package com.quiufuits.blocks.fruit;

import com.quiufuits.config.ModConfig;
import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.items.FruitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Fruta pegada al tronco (estilo cacao), con 4 etapas de maduración.
 */
public class FruitCropBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<FruitStage> STAGE = EnumProperty.create("stage", FruitStage.class);

	private static final VoxelShape EAST_AABB = Block.box(11.0, 7.0, 6.0, 15.0, 12.0, 10.0);
	private static final VoxelShape WEST_AABB = Block.box(1.0, 7.0, 6.0, 5.0, 12.0, 10.0);
	private static final VoxelShape NORTH_AABB = Block.box(6.0, 7.0, 1.0, 10.0, 12.0, 5.0);
	private static final VoxelShape SOUTH_AABB = Block.box(6.0, 7.0, 11.0, 10.0, 12.0, 15.0);

	private final Supplier<FruitDefinition> definition;

	public FruitCropBlock(Supplier<FruitDefinition> definition, BlockBehaviour.Properties properties) {
		super(properties.randomTicks().noCollission().instabreak());
		this.definition = definition;
		registerDefaultState(stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(STAGE, FruitStage.GROWING));
	}

	public FruitDefinition definition() {
		return definition.get();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, STAGE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case SOUTH -> SOUTH_AABB;
			case WEST -> WEST_AABB;
			case EAST -> EAST_AABB;
			default -> NORTH_AABB;
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = defaultBlockState();
		LevelReader level = context.getLevel();
		BlockPos pos = context.getClickedPos();

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis().isHorizontal()) {
				state = state.setValue(FACING, direction);
				if (state.canSurvive(level, pos)) {
					return state;
				}
			}
		}
		return null;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState support = level.getBlockState(pos.relative(state.getValue(FACING)));
		return support.is(definition().plantableOnLogs());
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		return direction == state.getValue(FACING) && !state.canSurvive(level, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		FruitStage stage = state.getValue(STAGE);
		if (stage == FruitStage.ROTTEN) {
			return;
		}
		ModConfig config = ModConfig.get();
		int chance = stage == FruitStage.RIPE ? config.ripeToRottenChance : config.stageAdvanceChance;
		if (chance <= 0) {
			chance = 1;
		}
		if (random.nextInt(chance) == 0) {
			level.setBlock(pos, state.setValue(STAGE, stage.next()), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		FruitStage stage = state.getValue(STAGE);

		// Quitar fruta podrida para reiniciar el ciclo (se cae / desaparece)
		if (stage == FruitStage.ROTTEN) {
			if (!level.isClientSide) {
				level.removeBlock(pos, false);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		// Cosechar desde poco maduro
		if (stage.canHarvest()) {
			if (!level.isClientSide) {
				ItemStack drop = FruitItem.create(definition().fruitItem(), stage);
				popResource(level, pos, drop);
				level.removeBlock(pos, false);
			}
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		FruitStage stage = state.getValue(STAGE);
		if (!stage.canHarvest()) {
			return List.of();
		}
		return List.of(FruitItem.create(definition().fruitItem(), stage));
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}

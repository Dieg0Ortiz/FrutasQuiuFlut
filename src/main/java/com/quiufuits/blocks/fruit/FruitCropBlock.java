package com.quiufuits.blocks.fruit;

import com.quiufuits.config.ModConfig;
import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.items.FruitItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class FruitCropBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<FruitStage> STAGE = EnumProperty.create("stage", FruitStage.class);

	/**
	 * Hitbox y colisión: la fruta sale del tronco (que está hacia FACING) y crece hacia afuera según la etapa.
	 * {@code DEPTH[stage]} = cuánto sobresale del tronco, en píxeles.
	 */
	private static final double[] DEPTH = {7.0, 10.0, 14.0, 14.0};
	private static final VoxelShape[][] SHAPES = new VoxelShape[FruitStage.values().length][4];

	static {
		for (FruitStage stage : FruitStage.values()) {
			double out = 16.0 - DEPTH[stage.getIndex()];
			VoxelShape[] byFacing = SHAPES[stage.getIndex()];
			byFacing[Direction.SOUTH.get2DDataValue()] = Block.box(1.0, 1.0, out, 15.0, 15.0, 16.0);
			byFacing[Direction.NORTH.get2DDataValue()] = Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 16.0 - out);
			byFacing[Direction.EAST.get2DDataValue()] = Block.box(out, 1.0, 1.0, 16.0, 15.0, 15.0);
			byFacing[Direction.WEST.get2DDataValue()] = Block.box(0.0, 1.0, 1.0, 16.0 - out, 15.0, 15.0);
		}
	}

	private final Supplier<FruitDefinition> definition;

	public FruitCropBlock(Supplier<FruitDefinition> definition, BlockBehaviour.Properties properties) {
		super(properties.randomTicks());
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
		return SHAPES[state.getValue(STAGE).getIndex()][state.getValue(FACING).get2DDataValue()];
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

	/**
	 * Maduración por tiempo: cada etapa programa un tick para cuando le toca avanzar.
	 * Creciendo -> Poco madura -> Madura: la mitad del tiempo total (4–5 días) cada una.
	 * Madura -> Podrida: {@link ModConfig#daysToRot} días.
	 */
	private static int ticksUntilNextStage(FruitStage stage, RandomSource random) {
		ModConfig config = ModConfig.get();
		double days;
		if (stage == FruitStage.RIPE) {
			days = config.daysToRot;
		} else {
			double min = Math.min(config.minDaysToRipe, config.maxDaysToRipe);
			double max = Math.max(config.minDaysToRipe, config.maxDaysToRipe);
			days = (min + random.nextDouble() * (max - min)) / 2.0;
		}
		return Math.max(20, (int) Math.round(days * 24000.0));
	}

	private void scheduleNextStage(BlockState state, Level level, BlockPos pos) {
		if (state.getValue(STAGE) != FruitStage.ROTTEN) {
			level.scheduleTick(pos, this, ticksUntilNextStage(state.getValue(STAGE), level.random));
		}
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		boolean sameStage = oldState.is(this) && oldState.getValue(STAGE) == state.getValue(STAGE);
		if (!level.isClientSide && !sameStage) {
			scheduleNextStage(state, level, pos);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		FruitStage stage = state.getValue(STAGE);
		if (stage != FruitStage.ROTTEN) {
			// onPlace programa la siguiente etapa
			level.setBlock(pos, state.setValue(STAGE, stage.next()), Block.UPDATE_CLIENTS);
		}
	}

	/** Respaldo: frutas generadas con el mundo (o de versiones anteriores) aún sin tick programado. */
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
			scheduleNextStage(state, level, pos);
		}
	}

	/** Se cosecha rompiéndola como un bloque (golpe corto); las frutas salen de aquí. */
	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		FruitStage stage = state.getValue(STAGE);
		if (!stage.canHarvest()) {
			return List.of();
		}
		ItemStack drop = FruitItem.create(definition().fruitItem(), stage);
		if (stage != FruitStage.ROTTEN) {
			drop.setCount(definition().rollDropCount(builder.getLevel().random));
		}
		return List.of(drop);
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

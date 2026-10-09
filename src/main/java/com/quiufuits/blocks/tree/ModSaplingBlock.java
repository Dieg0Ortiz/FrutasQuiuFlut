package com.quiufuits.blocks.tree;

import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModSaplingBlock extends SaplingBlock {
	public ModSaplingBlock(AbstractTreeGrower grower, BlockBehaviour.Properties properties) {
		super(grower, properties);
	}
}

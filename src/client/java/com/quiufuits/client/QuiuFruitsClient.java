package com.quiufuits.client;

import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.fruits.FruitDefinitions;
import com.quiufuits.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public class QuiuFruitsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.QUIU_OAK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ABEDUL_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.QUIU_OAK_LEAVES, RenderType.cutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ABEDUL_LEAVES, RenderType.cutoutMipped());

		for (FruitDefinition fruit : FruitDefinitions.all()) {
			BlockRenderLayerMap.INSTANCE.putBlock(fruit.cropBlock(), RenderType.cutout());
		}
	}
}

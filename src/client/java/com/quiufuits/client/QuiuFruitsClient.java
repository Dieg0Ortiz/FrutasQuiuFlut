package com.quiufuits.client;

import com.quiufuits.fruits.FruitDefinition;
import com.quiufuits.fruits.FruitDefinitions;
import com.quiufuits.registry.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;

public class QuiuFruitsClient implements ClientModInitializer {
	/** Verde oscuro un poco pálido (no negro). */
	private static final int ABEDUL_FOLIAGE_COLOR = 0x6B8C4E;

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.QUIU_OAK_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ABEDUL_SAPLING, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.QUIU_OAK_LEAVES, RenderType.cutoutMipped());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ABEDUL_LEAVES, RenderType.cutoutMipped());

		ColorProviderRegistry.BLOCK.register(
				(state, level, pos, tintIndex) -> ABEDUL_FOLIAGE_COLOR,
				ModBlocks.ABEDUL_LEAVES
		);
		ColorProviderRegistry.ITEM.register(
				(stack, tintIndex) -> ABEDUL_FOLIAGE_COLOR,
				ModBlocks.ABEDUL_LEAVES
		);

		for (FruitDefinition fruit : FruitDefinitions.all()) {
			BlockRenderLayerMap.INSTANCE.putBlock(fruit.cropBlock(), RenderType.cutout());
		}
	}
}

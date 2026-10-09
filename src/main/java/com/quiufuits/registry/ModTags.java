package com.quiufuits.registry;

import com.quiufuits.QuiuFruitsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public final class ModTags {
	/** Placeholder: apunta biomas de prueba hasta que exista el Centro de Quiu. */
	public static final TagKey<Biome> CENTRO_QUIU = TagKey.create(Registries.BIOME, QuiuFruitsMod.id("centro_quiu"));

	public static final TagKey<Block> QUIU_OAK_LOGS = TagKey.create(Registries.BLOCK, QuiuFruitsMod.id("quiu_oak_logs"));
	public static final TagKey<Block> ABEDUL_LOGS = TagKey.create(Registries.BLOCK, QuiuFruitsMod.id("abedul_logs"));
	/** Troncos donde se puede plantar Fruta Solaria (y otras que usen ambos). */
	public static final TagKey<Block> SOLARIA_PLANTABLE_LOGS = TagKey.create(Registries.BLOCK, QuiuFruitsMod.id("solaria_plantable_logs"));
	public static final TagKey<Block> FRUIT_PLANTABLE_LOGS = TagKey.create(Registries.BLOCK, QuiuFruitsMod.id("fruit_plantable_logs"));

	private ModTags() {
	}
}

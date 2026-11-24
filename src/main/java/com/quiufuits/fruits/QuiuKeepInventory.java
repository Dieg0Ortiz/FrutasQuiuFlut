package com.quiufuits.fruits;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Protección del Fruto Quiu: como keepInventory, pero de un solo uso por jugador.
 * <ul>
 *   <li>Al comer el fruto, el jugador queda protegido ({@link #PROTECTED_TAG}).</li>
 *   <li>Al morir protegido no suelta inventario ni experiencia; la protección se gasta
 *       y se marca {@link #KEPT_TAG} (ver {@code PlayerMixin}).</li>
 *   <li>Al reaparecer o ser revivido (también en hardcore) recupera todo.</li>
 * </ul>
 * Se guarda en las etiquetas del jugador, que se conservan al salir del server.
 */
public final class QuiuKeepInventory {
	public static final String PROTECTED_TAG = "quiufuits.inventario_protegido";
	public static final String KEPT_TAG = "quiufuits.inventario_guardado";

	private QuiuKeepInventory() {
	}

	public static boolean isProtected(Player player) {
		return player.getTags().contains(PROTECTED_TAG);
	}

	public static void protect(Player player) {
		player.addTag(PROTECTED_TAG);
	}

	/** Al morir, antes de soltar el inventario: true si se conserva (y se gasta la protección). */
	public static boolean keepOnDeath(Player player) {
		if (!player.removeTag(PROTECTED_TAG)) {
			return false;
		}
		player.addTag(KEPT_TAG);
		return true;
	}

	public static boolean keptOnDeath(Player player) {
		return player.getTags().contains(KEPT_TAG);
	}

	public static void register() {
		ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
			newPlayer.removeTag(KEPT_TAG);
			if (alive) {
				// Cambio de dimensión (p. ej. salir del End): la protección sigue activa
				if (isProtected(oldPlayer)) {
					protect(newPlayer);
				}
				return;
			}
			newPlayer.removeTag(PROTECTED_TAG);
			if (keptOnDeath(oldPlayer)) {
				newPlayer.getInventory().replaceWith(oldPlayer.getInventory());
				newPlayer.experienceLevel = oldPlayer.experienceLevel;
				newPlayer.totalExperience = oldPlayer.totalExperience;
				newPlayer.experienceProgress = oldPlayer.experienceProgress;
				newPlayer.setScore(oldPlayer.getScore());
				newPlayer.sendSystemMessage(Component.translatable("message.quiufuits.fruto_quiu.restored")
						.withStyle(ChatFormatting.GOLD));
			}
		});
	}
}

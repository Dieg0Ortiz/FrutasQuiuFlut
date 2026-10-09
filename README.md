# Quiu Fruits (`quiufuits`)

Mod de Fabric para **Minecraft 1.20.1** con árboles del Centro de Quiu, frutas con ciclo de maduración (estilo cacao) y un sistema de fichas (`FruitDefinition`) para añadir frutas nuevas fácilmente.

- **Loader:** Fabric Loader + Fabric API  
- **Java:** 17+  
- **Mappings:** oficiales de Mojang (Loom)  
- **Mod ID:** `quiufuits`

## Cómo ejecutar

```bash
./gradlew runClient
# o generar el JAR
./gradlew build
# JAR en build/libs/quiufuits-1.0.0.jar
```

Configuración runtime: `config/quiufuits.json` (se crea al arrancar).

## Árboles

| Árbol | Bloques | Worldgen |
|-------|---------|----------|
| **Roble de Quiu** | tronco, sin corteza, madera, hojas, retoño, tablones | tag `#quiufuits:centro_quiu` |
| **Abedúl** | idem | mismo tag |

El tag de bioma es un **placeholder** (hoy incluye bosques vanilla). Cuando exista el bioma Centro de Quiu, edita:

`data/quiufuits/tags/worldgen/biome/centro_quiu.json`

sin tocar código Java.

Crecimiento: retoños con tiempo o bonemeal → feature configurada data-driven.

## Frutas y efectos

Cada fruta pasa por **4 etapas** en el bloque: Creciendo → Poco maduro → Maduro → Podrido.

- Solo se cosecha desde **Poco maduro**.
- En **Maduro** hay que recoger pronto; si no, **se pudre**.
- **Podrido** se quita del tronco (clic) para reiniciar el ciclo.
- Efectos al comer dependen de la etapa de cosecha.

| Fruta | Efecto (poco / maduro / podrido) | Semilla en | Pelos | Mob (drop) |
|-------|-----------------------------------|------------|-------|------------|
| **Solaria** | Fire Resistance corto / completo / Hunger | Roble de Quiu + Abedúl | Pelo Solar | Plumosaurio → Excremento *(pendiente)* |
| **Glebanoida** | Haste I corto / Haste II / Poison | ambos troncos | Pelo Glebanoide | Vaca → Cuero |
| **Lunar** | Night Vision corto / largo / Blindness | Abedúl | Pelo Lunar | Gato → Hilo |
| **Marea** | Water Breathing corto / largo / Nausea | Roble de Quiu | Pelo Marino | Tortuga → Hierba marina |
| **Bruma** | Slow Falling corto / largo / Slowness | ambos | Pelo de Bruma | Fantasma → Membrana |
| **Ámbar** | Resistance corto / completo / Weakness | Roble de Quiu | Pelo Ambarino | Abeja → Panal |

### Por qué Fire Resistance en la Solaria

Tema solar: la fruta concentra calor del sol. En lugar de quemar al jugador, le otorga **protección térmica** (Resistencia al fuego). Poco madura = breve; madura = larga; podrida = hambre (energía solar corrompida).

### Interacciones comunes

1. **Semilla:** Hacha en mano principal + fruta en secundaria.  
2. **Pelos:** clic derecho en la fruta del tronco con Espada (−1 durabilidad).  
3. **Mob:** clic derecho en la entidad con la fruta (probabilidad configurable).

### Receta de ejemplo (pelos)

**Amuleto de Quiu** — crafteo con Pelo Solar, Glebanoide, Lunar, Marino, de Bruma y Ambarino.

## Arquitectura (`FruitDefinition`)

Paquete `com.quiufuits.fruits`: una ficha define color, efectos, tag de troncos plantables, semilla/pelos opcionales, mob feed y tooltips. Registrar en `FruitDefinitions.bootstrap()`.

Estructura de paquetes:

```
com.quiufuits
├── blocks/fruit|tree
├── config
├── fruits
├── integration
├── items
├── registry
└── worldgen
```

## Integraciones pendientes

### Plumosaurio (otro mod, en desarrollo)

- **No** se crea la entidad ni el ítem Excremento en este mod.
- IDs placeholder en `config/quiufuits.json`:

```json
"plumosaurio": {
  "entityId": "plumosaurio:plumosaurio",
  "dropItemId": "plumosaurio:excremento",
  "chance": 0.35
}
```

- Se resuelven en **runtime** vía registro de Minecraft.
- Si no existen → warning en log, interacción desactivada, **sin crash**.
- Cuando el otro mod exista: cambia solo esos IDs (o confirma que coinciden) — **sin recompilar**.
- Marcadores `TODO(integración)` en `MobFeedIntegration` y `ModConfig`.

### Centro de Quiu (bioma)

- Sustituir valores del tag `#quiufuits:centro_quiu` por el bioma real.

## Control de versiones / GitHub

```bash
git add -A
git commit -m "Descripción del cambio"
git push -u origin main
```

Si usas otra rama: `git push -u origin <rama>`.

## Licencia

MIT — ver `LICENSE`.

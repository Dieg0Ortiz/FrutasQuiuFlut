# Quiu Fruits (`quiufuits`)

Mod Fabric para **Minecraft 1.20.1**: árboles del Centro de Quiu y las frutas oficiales de las fichas de referencia.

- **Loader:** Fabric Loader + Fabric API  
- **Java:** 17+  
- **Mappings:** oficiales de Mojang  
- **Mod ID:** `quiufuits`

## Cómo ejecutar

```bash
./gradlew runClient
./gradlew build
# JAR: build/libs/quiufuits-1.0.0.jar
```

Config: `config/quiufuits.json` (se crea al arrancar).

## Árboles

| Árbol | Bloques | Worldgen |
|-------|---------|----------|
| **Roble de Quiu** | tronco, sin corteza, madera, hojas, retoño, tablones | `#quiufuits:centro_quiu` |
| **Abedúl** | idem | mismo tag |

El tag de bioma es **placeholder** (bosques vanilla). Cuando exista el Centro de Quiu, edita  
`data/quiufuits/tags/worldgen/biome/centro_quiu.json` sin tocar Java.

## Frutas oficiales

Todas se plantan en **Troncos de Abedúl** y **Roble de Quiu**, con ciclo  
Creciendo → Poco maduro → Maduro → Podrido.

| Fruta | Efecto (poco / maduro / podrido) | Semilla (Hacha + offhand) | Pelos | Mob |
|-------|----------------------------------|---------------------------|-------|-----|
| **Fruta Solaria** | Fire Resistance corto / completo / **ninguno** | Sí (también podrida) | — | Plumosaurio → Excremento *(pendiente)* |
| **Huevo Eburia** | Health Boost corto / 1:00 (+4 máx.) / **ninguno** | Sí (también podrida) | **Pelos de Eburia** (Tijeras + offhand) | — |
| **Arándano Nocturino** | Night Vision corto / largo / **ninguno** | Sí (también podrida) | — | — |
| **Baya Uvina** | Regeneration corta / completa / **ninguno** | Sí (también podrida) | — | — |

La fruta **podrida** no aplica efectos al comerla, pero sí permite extraer la semilla con hacha.

### Efectos elegidos

- **Solaria → Fire Resistance:** calor solar → protección térmica.  
- **Eburia → Health Boost:** coincide con la ficha (“Salud mejorada” / “Salud máxima: +4”).  
- **Nocturino → Night Vision:** tema nocturno.  
- **Uvina → Regeneration:** baya dulce restauradora.

### Interacciones

1. **Semilla:** Hacha en mano principal + fruta en secundaria.  
2. **Pelos (solo Eburia):** Tijeras en mano principal + Huevo Eburia en secundaria.  
3. **Plumosaurio (solo Solaria):** placeholder por ID en config.

### Receta de ejemplo

**Amuleto de Quiu** = Pelos de Eburia + las 4 frutas.

## Arquitectura

`FruitDefinition` + `FruitDefinitions.bootstrap()`. Paquetes: `blocks`, `items`, `fruits`, `worldgen`, `integration`, `registry`, `config`.

## Integraciones pendientes

### Plumosaurio

No se crea la entidad ni Excremento aquí. IDs en `config/quiufuits.json`:

```json
"plumosaurio": {
  "entityId": "plumosaurio:plumosaurio",
  "dropItemId": "plumosaurio:excremento",
  "chance": 0.35
}
```

Si no existen → warning en log, sin crash. TODO en `MobFeedIntegration` / `ModConfig`.

### Centro de Quiu

Sustituir biomas del tag `#quiufuits:centro_quiu`.

## Git

```bash
git add -A
git commit -m "Descripción"
git push -u origin main
```

## Licencia

MIT — ver `LICENSE`.

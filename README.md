# NTNHCore

Nuclear Tech: New Horizons modpack core mod for Minecraft 1.7.10.

## Modules

NTNHCore uses a module system where features are implemented as self-contained modules, each with its own config section in `ntnhcore.cfg`.

Each module can be enabled or disabled via the `modules` category in the config:

```
modules {
    B:enable_fastequip=true
    B:enable_grassisannoying=true
    B:enable_coloredhearts=true
    B:enable_betterplacement=true
    B:enable_whitelistdementianomore=true
    B:enable_gravitygunfix=true
}
```

Module-specific settings appear under their own subcategory (e.g. `modules.fastequip`, `modules.grassisannoying`) and are present in the config file regardless of whether the module is enabled.

### FastEquip

Right-click to equip armor from hotbar or inventory.

- `isHotbarFastEquipEnabled` - right-click armor in hotbar to equip
- `isInventoryFastEquipEnabled` - right-click armor in inventory to equip

### GrassIsAnnoying

Attack entities through grass and hide the block outline when aiming at an entity through a non-colliding block.

- `isModEnabled` - enable attacking through grass
- `hideBlockOutline` - hide the block outline when aiming at an entity through grass

### ColoredHearts

Custom colored health bar overlay with support for poison/wither tints, absorption, regeneration bounce, low-health shake, and >240 HP glow overlays.

- No additional config options (controlled by module enable toggle)

### BetterPlacement

Snappier block placement by removing the right-click delay when moving the cursor to a new location.

- `creativeOnly` - only apply in creative mode
- `forceNewLoc` - require cursor movement between successive placements

### WhitelistDementiaNoMore

Fixes server whitelist offline mode UUID mismatches when adding players to the whitelist.

- No additional config options (controlled by module enable toggle)

### GravityGunFix

Makes GravityGun break and place blocks properly instead of moving their tile entity NBT, which broke multiblocks and machines. Contents are kept for blocks that keep them when mined (e.g. batteries and crates), and dropped otherwise. Also lets Storage Drawers be grabbed.

- `maxBreaks` - how many times to break a block that turns into another one, like assembled multiblock parts
- `superchargedPulling` - let the supercharged gun pull chests and `grabbableBlocks` from a distance, like it could for other blocks
- `keepDataBlocks` - blocks that keep all of their data when moved (e.g. chests), supports `*` as a wildcard
- `grabbableBlocks` - blocks that can be grabbed even though they aren't full blocks (e.g. drawers), supports `*` as a wildcard

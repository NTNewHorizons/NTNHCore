# NTNHCore

Nuclear Tech: New Horizons modpack core mod for Minecraft 1.7.10.

## Modules

NTNHCore uses a module system where features are implemented as self-contained modules, each with its own config section in `ntnhcore.cfg`.

Each module can be enabled or disabled via the `modules` category in the config:

```
modules {
    B:enable_coloredhearts=true
    B:enable_betterplacement=true
    B:enable_gravitygunfix=true
}
```

Module-specific settings appear under their own subcategory and are present in the config file regardless of whether the module is enabled.

### ColoredHearts

Custom colored health bar overlay with support for poison/wither tints, absorption, regeneration bounce, low-health shake, and >240 HP glow overlays.

- No additional config options (controlled by module enable toggle)

### BetterPlacement

Snappier block placement by removing the right-click delay when moving the cursor to a new location.

- `creativeOnly` - only apply in creative mode
- `forceNewLoc` - require cursor movement between successive placements

### GravityGunFix

Makes GravityGun break and place blocks by default instead of moving their tile entity NBT, which broke a bunch of stuff like multiblocks. Contents are kept for blocks that keep them when mined, and dropped otherwise. Also lets extra blocks be grabbed.

- `maxBreaks` - how many times to break a block that turns into another one, like assembled multiblock parts
- `superchargedPulling` - let the supercharged gun pull chests and `grabbableBlocks` from a distance, like it could for other blocks
- `keepDataBlocks` - blocks that keep all of their data when moved (e.g. chests), supports `*` as a wildcard
- `grabbableBlocks` - blocks that can be grabbed even though they aren't full blocks (e.g. drawers, hoppers and shulker boxes), supports `*` as a wildcard

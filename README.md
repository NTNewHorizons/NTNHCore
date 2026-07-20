# NTNHCore

Nuclear Tech: New Horizons modpack core mod for Minecraft 1.7.10.

## Modules

NTNHCore uses a module system where features are implemented as self-contained modules, each with its own config section in `ntnhcore.cfg`.

Each module can be enabled or disabled via the `modules` category in the config:

```
modules {
    B:enable_fastequip=true
    B:enable_grassisannoying=true
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

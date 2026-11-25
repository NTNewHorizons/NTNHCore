package com.ntnh.ntnhcore.modules.thomass47.gravitygunfix;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Makes the Gravity Gun break and place blocks instead of moving their tile entities around. */
public class GravityGunFix implements IModule {

    public static final String MODULE_ID = "gravitygunfix";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    private static final int SIDE_UP = 1;

    private static final ForgeDirection[] PLACE_OFFSETS = { ForgeDirection.UP, ForgeDirection.NORTH,
        ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.DOWN };

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    /** Registered even while disabled, as the module can be enabled from the config while the game is running. */
    public GravityGunFix() {
        MinecraftForge.EVENT_BUS.register(new DropCapture());
    }

    /**
     * Breaks the block with its real tile entity still in place, so it can drop its contents and notify its multiblock.
     * Assembled multiblock parts turn back into the block they were made of when broken, in which case that one is
     * broken too.
     *
     * @return The block that was removed, or null if there's nothing left to carry
     */
    public static BrokenBlock breakBlock(World world, int x, int y, int z) {
        for (int i = 0; i < Config.gravityGunFixMaxBreaks; i++) {
            Block block = world.getBlock(x, y, z);
            if (block.isAir(world, x, y, z) || (i > 0 && block.getBlockHardness(world, x, y, z) < 0.0F)) {
                return null;
            }

            int meta = world.getBlockMetadata(x, y, z);
            if (keepsData(block)) {
                TileEntity tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity != null) {
                    return breakKeepingData(world, x, y, z, block, meta, tileEntity);
                }
            }

            BrokenBlock broken = block.hasTileEntity(meta) ? breakTileEntityBlock(world, x, y, z, block, meta)
                : breakSimpleBlock(world, x, y, z, block, meta);

            // Other blocks nearby can already have filled the space, e.g. with HBM's gases
            Block left = world.getBlock(x, y, z);
            boolean isGone = left.isAir(world, x, y, z) || left.isReplaceable(world, x, y, z)
                || left.getMaterial()
                    .isLiquid();
            boolean hasContents = broken != null && broken.stack != null && broken.stack.hasTagCompound();
            if (isGone || hasContents) {
                return broken;
            }
        }
        return null;
    }

    private static BrokenBlock breakSimpleBlock(World world, int x, int y, int z, Block block, int meta) {
        world.setBlockToAir(x, y, z);
        return new BrokenBlock(block, meta, null, null);
    }

    /**
     * The tile entity isn't kept, but blocks like batteries and crates save their contents to the item they drop when
     * mined, so that item is carried and later placed instead of being dropped.
     */
    private static BrokenBlock breakTileEntityBlock(World world, int x, int y, int z, Block block, int meta) {
        // Machines often have a separate block for when they're running, which drops the regular one
        Block carried = block;
        int carriedMeta = meta;
        Item dropped = block.getItemDropped(meta, world.rand, 0);
        if (dropped instanceof ItemBlock && Block.getBlockFromItem(dropped) != block) {
            Block droppedBlock = Block.getBlockFromItem(dropped);
            int droppedMeta = dropped.getMetadata(block.damageDropped(meta));
            // Otherwise it only breaks into that block, like ender chests into obsidian
            if (droppedBlock.hasTileEntity(droppedMeta)) {
                carried = droppedBlock;
                carriedMeta = droppedMeta;
            }
        }
        Item carriedItem = Item.getItemFromBlock(carried);

        List<ItemStack> drops = new ArrayList<>();
        ItemStack stack = null;
        for (ItemStack drop : block.getDrops(world, x, y, z, meta, 0)) {
            if (drop == null || drop.getItem() == null) {
                continue;
            }
            if (stack == null && drop.getItem() == carriedItem) {
                stack = drop;
            } else {
                drops.add(drop);
            }
        }

        if ((stack == null || !stack.hasTagCompound()) && block.getClass()
            .getName()
            .startsWith("com.hbm.")) {
            // HBM's crates drop themselves from here instead. Other mods might drop theirs after the block is gone,
            // when it can't be told apart from their contents.
            ItemStack captured = DropCapture.removeBlock(world, x, y, z, block, carriedItem);
            if (captured != null) {
                stack = captured;
            }
        } else {
            world.setBlockToAir(x, y, z);
        }

        if (world.getBlock(x, y, z) == block) {
            // Refused to be removed
            return null;
        }

        for (ItemStack drop : drops) {
            EntityItem item = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, drop);
            item.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(item);
        }

        if (stack == null && !drops.isEmpty()) {
            // Only breaks into other items, carrying the block too would duplicate them
            return null;
        }
        if (stack != null) {
            stack = stack.copy();
            stack.stackSize = 1;
        }
        return new BrokenBlock(carried, carriedMeta, stack, null);
    }

    private static boolean keepsData(Block block) {
        return matchesAny(block, Config.gravityGunFixKeepDataBlocks);
    }

    /** GravityGun itself only grabs full blocks and a few others. */
    public static boolean isMadeGrabbable(Block block) {
        return Config.moduleGravityGunFixEnabled && matchesAny(block, Config.gravityGunFixGrabbableBlocks);
    }

    private static boolean matchesAny(Block block, String[] patterns) {
        String name = Block.blockRegistry.getNameForObject(block);
        for (String pattern : patterns) {
            String regex = "\\Q" + pattern.trim()
                .replace("*", "\\E.*\\Q") + "\\E";
            if (name != null && name.matches(regex)) {
                return true;
            }
        }
        return false;
    }

    /** Swaps in a blank tile entity before breaking the block, so that the contents aren't dropped as well as kept. */
    private static BrokenBlock breakKeepingData(World world, int x, int y, int z, Block block, int meta,
        TileEntity tileEntity) {
        NBTTagCompound data = new NBTTagCompound();
        tileEntity.writeToNBT(data);
        // Item tags aren't copied when written
        data = (NBTTagCompound) data.copy();

        TileEntity blank = block.createTileEntity(world, meta);
        if (blank == null) {
            return null;
        }
        NBTTagCompound blankData = new NBTTagCompound();
        blank.writeToNBT(blankData);
        ItemStack stack = getOwnDrop(world, x, y, z, block, meta);

        world.setTileEntity(x, y, z, blank);
        world.setBlockToAir(x, y, z);
        if (world.getBlock(x, y, z) == block) {
            // Refused to be removed
            tileEntity.validate();
            world.setTileEntity(x, y, z, tileEntity);
            return null;
        }

        // Nothing may be taken out of the old one anymore, as it's also being carried
        tileEntity.invalidate();
        for (Object object : new ArrayList<>(world.playerEntities)) {
            EntityPlayer player = (EntityPlayer) object;
            if (player.openContainer != player.inventoryContainer && !player.openContainer.canInteractWith(player)) {
                player.closeScreen();
            }
        }
        if (tileEntity instanceof IInventory) {
            IInventory inventory = (IInventory) tileEntity;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                inventory.setInventorySlotContents(i, null);
            }
        }
        if (isMadeGrabbable(block)) {
            // The slots of drawers don't hold all of their contents
            blankData.setInteger("x", x);
            blankData.setInteger("y", y);
            blankData.setInteger("z", z);
            tileEntity.readFromNBT(blankData);
        }
        return new BrokenBlock(block, meta, stack, data);
    }

    private static ItemStack getOwnDrop(World world, int x, int y, int z, Block block, int meta) {
        for (ItemStack drop : block.getDrops(world, x, y, z, meta, 0)) {
            if (drop != null && drop.getItem() != null && Block.getBlockFromItem(drop.getItem()) == block) {
                ItemStack stack = drop.copy();
                stack.stackSize = 1;
                return stack;
            }
        }
        return null;
    }

    public static boolean placeWithData(World world, int x, int y, int z, Block block, int meta, NBTTagCompound data,
        Entity entity, EntityPlayer holder) {
        if (!placeBlock(world, x, y, z, block, meta, true, null, entity, holder)) {
            return false;
        }
        restoreData(world, x, y, z, block, meta, data);
        return true;
    }

    /** For when there's no space where it is, as it can't be dropped as an item without losing the data. */
    public static boolean placeNextToWithData(World world, int x, int y, int z, Block block, int meta,
        NBTTagCompound data, Entity entity, EntityPlayer holder) {
        for (ForgeDirection offset : PLACE_OFFSETS) {
            int placeX = x + offset.offsetX;
            int placeY = y + offset.offsetY;
            int placeZ = z + offset.offsetZ;
            if (placeWithData(world, placeX, placeY, placeZ, block, meta, data, entity, holder)) {
                return true;
            }
        }
        return false;
    }

    private static void restoreData(World world, int x, int y, int z, Block block, int meta, NBTTagCompound data) {
        if (world.getBlock(x, y, z) != block || world.getTileEntity(x, y, z) == null) {
            // Placing it turned it into something that can't hold the data
            world.setBlock(x, y, z, block, meta, 3);
            world.setTileEntity(x, y, z, block.createTileEntity(world, meta));
        }

        NBTTagCompound moved = (NBTTagCompound) data.copy();
        moved.setInteger("x", x);
        moved.setInteger("y", y);
        moved.setInteger("z", z);

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null) {
            LOG.error("No tile entity to restore data to at " + x + ", " + y + ", " + z + ": " + moved);
            return;
        }
        tileEntity.readFromNBT(moved);
        tileEntity.markDirty();
        world.markBlockForUpdate(x, y, z);
    }

    public static boolean placeBlock(World world, int x, int y, int z, Block block, int meta, ItemStack carried,
        Entity entity, EntityPlayer holder) {
        return placeBlock(world, x, y, z, block, meta, !block.hasTileEntity(meta), carried, entity, holder);
    }

    /**
     * Places the block the way its item would. The block is responsible for creating its own tile entity.
     *
     * @param keepMeta Whether to keep e.g. the direction of logs and hoppers instead of having it depend on the side
     *                 it's placed on. Blocks that face whoever places them still do.
     */
    private static boolean placeBlock(World world, int x, int y, int z, Block block, int meta, boolean keepMeta,
        ItemStack carried, Entity entity, EntityPlayer holder) {
        ItemStack stack = carried != null ? carried.copy() : getItemStack(world, block, meta);
        if (!world.canPlaceEntityOnSide(block, x, y, z, true, SIDE_UP, null, stack)) {
            return false;
        }

        int placedMeta = keepMeta ? meta : block.onBlockPlaced(world, x, y, z, SIDE_UP, 0.5F, 0.0F, 0.5F, meta);
        EntityPlayer placer = getPlacer(world, entity, holder);
        if (stack.getItem() instanceof ItemBlock && Block.getBlockFromItem(stack.getItem()) == block) {
            return ((ItemBlock) stack.getItem())
                .placeBlockAt(stack, placer, world, x, y, z, SIDE_UP, 0.5F, 0.0F, 0.5F, placedMeta);
        }

        if (!world.setBlock(x, y, z, block, placedMeta, 3)) {
            return false;
        }
        if (world.getBlock(x, y, z) == block) {
            if (stack.getItem() != null) {
                block.onBlockPlacedBy(world, x, y, z, placer, stack);
            }
            block.onPostBlockPlaced(world, x, y, z, placedMeta);
        }
        return true;
    }

    /** World metadata holds things like rotation, so it can't be used as item damage directly. */
    public static ItemStack getItemStack(World world, Block block, int meta) {
        Item dropped = block.getItemDropped(meta, world.rand, 0);
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
            item = dropped;
        }

        int damage = 0;
        if (dropped == null || dropped == item) {
            damage = block.damageDropped(meta);
        } else if (item.getHasSubtypes()) {
            damage = meta;
        }
        return new ItemStack(item, 1, damage);
    }

    private static EntityPlayer getPlacer(World world, Entity entity, EntityPlayer holder) {
        if (holder != null && !holder.isDead && holder.worldObj == world) {
            return holder;
        }
        // Not held by a player, e.g. pulled out of a wall and dropped, or thrown by a zombie
        EntityPlayer fakePlayer = FakePlayerFactory.getMinecraft((WorldServer) world);
        fakePlayer.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, 0.0F, 0.0F);
        return fakePlayer;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {}

    public static class BrokenBlock {

        public final Block block;
        public final int meta;
        /**
         * The item the block drops as, or null if it has no tile entity or doesn't drop itself. Only used for how it
         * looks if there's data.
         */
        public final ItemStack stack;
        /** The tile entity's data, or null if the block isn't configured to keep it. */
        public final NBTTagCompound data;

        public BrokenBlock(Block block, int meta, ItemStack stack, NBTTagCompound data) {
            this.block = block;
            this.meta = meta;
            this.stack = stack;
            this.data = data;
        }
    }

    /** Catches the item a block drops as while it's being removed. */
    public static class DropCapture {

        private static World world;
        private static int x, y, z;
        private static Block block;
        private static Item item;
        private static ItemStack captured;

        static ItemStack removeBlock(World world, int x, int y, int z, Block block, Item item) {
            DropCapture.x = x;
            DropCapture.y = y;
            DropCapture.z = z;
            DropCapture.block = block;
            DropCapture.item = item;
            DropCapture.captured = null;
            DropCapture.world = world;
            try {
                block.removedByPlayer(world, FakePlayerFactory.getMinecraft((WorldServer) world), x, y, z, true);
            } finally {
                DropCapture.world = null;
            }
            return captured;
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onEntityJoinWorld(EntityJoinWorldEvent event) {
            if (world == null || event.world != world || captured != null || !(event.entity instanceof EntityItem)) {
                return;
            }
            // Once the block is gone these are its contents, which may include more of the same item
            ItemStack stack = ((EntityItem) event.entity).getEntityItem();
            if (world.getBlock(x, y, z) == block && stack.getItem() == item) {
                captured = stack;
                event.setCanceled(true);
            }
        }
    }
}

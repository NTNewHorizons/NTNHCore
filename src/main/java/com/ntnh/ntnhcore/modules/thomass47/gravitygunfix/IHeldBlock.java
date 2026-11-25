package com.ntnh.ntnhcore.modules.thomass47.gravitygunfix;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/** Implemented on GravityGun's EntityBlock. */
public interface IHeldBlock {

    /** Sets who should count as having placed the block. */
    void ntnhcore$setHolder(EntityLivingBase holder);

    /** @return The item to render instead of the block, or null to leave rendering it to GravityGun */
    ItemStack ntnhcore$getRenderedStack();
}

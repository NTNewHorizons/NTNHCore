package com.ntnh.ntnhcore.modules.thomass47.gravitygunfix;

import net.minecraft.entity.EntityLivingBase;

/** Implemented on GravityGun's EntityBlock to remember who should count as having placed it. */
public interface IHeldBlock {

    void ntnhcore$setHolder(EntityLivingBase holder);
}

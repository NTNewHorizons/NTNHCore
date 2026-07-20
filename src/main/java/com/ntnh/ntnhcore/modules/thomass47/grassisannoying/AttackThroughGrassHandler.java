package com.thomass47.grassisannoying;

import java.util.List;

import com.ntnh.ntnhcore.Config;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AttackThroughGrassHandler {

    @SubscribeEvent
    public void onMouseInput(MouseEvent event) {
        if (!Config.grassIsAnnoyingModEnabled) return;

        if (event.button == 0 && event.buttonstate) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                int x = mc.objectMouseOver.blockX;
                int y = mc.objectMouseOver.blockY;
                int z = mc.objectMouseOver.blockZ;

                if (mc.theWorld != null) {
                    Block block = mc.theWorld.getBlock(x, y, z);
                    if (block != null && block.getCollisionBoundingBoxFromPool(mc.theWorld, x, y, z) == null) {
                        Entity pointedEntity = findEntity(mc);
                        if (pointedEntity != null) {
                            mc.objectMouseOver = new MovingObjectPosition(pointedEntity);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(net.minecraftforge.client.event.DrawBlockHighlightEvent event) {
        if (!Config.grassIsAnnoyingModEnabled || !Config.grassIsAnnoyingHideBlockOutline) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (event.target != null && event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = event.target.blockX;
            int y = event.target.blockY;
            int z = event.target.blockZ;

            if (mc.theWorld != null) {
                Block block = mc.theWorld.getBlock(x, y, z);
                if (block != null && block.getCollisionBoundingBoxFromPool(mc.theWorld, x, y, z) == null) {
                    Entity pointedEntity = findEntity(mc);
                    if (pointedEntity != null) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * Finds the entity the player is currently looking at. <br>
     * This is a recreation of the ray trace logic from Minecraft's core EntityRenderer.java,
     * but cleaned up to be more readable.
     */
    private Entity findEntity(Minecraft mc) {
        Entity renderViewEntity = mc.renderViewEntity;
        if (!(renderViewEntity instanceof net.minecraft.entity.EntityLivingBase) || mc.theWorld == null) return null;

        net.minecraft.entity.EntityLivingBase viewEntity = (net.minecraft.entity.EntityLivingBase) renderViewEntity;

        double reach = mc.playerController.extendedReach() ? 6.0D
            : Math.min(mc.playerController.getBlockReachDistance(), 3.0D);

        Vec3 eyePos = viewEntity.getPosition(1.0F);
        Vec3 lookDir = viewEntity.getLook(1.0F);
        Vec3 hitEndPos = eyePos.addVector(lookDir.xCoord * reach, lookDir.yCoord * reach, lookDir.zCoord * reach);

        List<Entity> entities = mc.theWorld.getEntitiesWithinAABBExcludingEntity(
            renderViewEntity,
            renderViewEntity.boundingBox
                .addCoord(lookDir.xCoord * reach, lookDir.yCoord * reach, lookDir.zCoord * reach)
                .expand(1.0, 1.0, 1.0));

        Entity pointedEntity = null;
        double pointedEntityDistance = reach;
        for (Entity entity : entities) {
            if (!entity.canBeCollidedWith()) continue;

            double collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB boundingBox = entity.boundingBox
                .expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);

            MovingObjectPosition movingobjectposition = boundingBox.calculateIntercept(eyePos, hitEndPos);

            if (boundingBox.isVecInside(eyePos)) {
                if (0.0D < pointedEntityDistance || pointedEntityDistance == 0.0D) {
                    pointedEntity = entity;
                    pointedEntityDistance = 0.0D;
                }
            } else if (movingobjectposition != null) {
                double hitDistance = eyePos.distanceTo(movingobjectposition.hitVec);
                if (hitDistance < pointedEntityDistance || pointedEntityDistance == 0.0D) {
                    pointedEntity = entity;
                    pointedEntityDistance = hitDistance;
                }
            }
        }

        return pointedEntity;
    }
}

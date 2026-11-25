package com.ntnh.ntnhcore.mixins.late.gravigun;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.IHeldBlock;

import gravigun.client.render.RenderBlock;
import gravigun.common.entity.EntityBlock;

@Mixin(value = RenderBlock.class, remap = false)
public class MixinRenderBlock {

    // Drawers are only rendered by their item renderer, and shulker boxes would look like regular chests
    @Redirect(
        method = "renderBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockAsItem(Lnet/minecraft/block/Block;IF)V",
            remap = true))
    private void gravityGunFix$renderStack(RenderBlocks renderBlocks, Block block, int meta, float brightness,
        EntityBlock entity) {
        ItemStack stack = ((IHeldBlock) entity).ntnhcore$getRenderedStack();
        // Unlike the other types, this one doesn't expect the entity of an item or of someone holding it
        IItemRenderer renderer = stack == null ? null
            : MinecraftForgeClient.getItemRenderer(stack, ItemRenderType.INVENTORY);
        if (renderer == null
            || !renderer.shouldUseRenderHelper(ItemRenderType.INVENTORY, stack, ItemRendererHelper.INVENTORY_BLOCK)) {
            renderBlocks.renderBlockAsItem(block, meta, brightness);
            return;
        }

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        renderer.renderItem(ItemRenderType.INVENTORY, stack, renderBlocks);
        GL11.glDisable(GL11.GL_BLEND);
    }
}

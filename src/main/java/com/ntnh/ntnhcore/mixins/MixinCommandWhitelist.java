package com.ntnh.ntnhcore.mixins;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;
import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.thomass47.whitelistdementianomore.WhitelistDementiaNoMore;

@Mixin(CommandWhitelist.class)
public class MixinCommandWhitelist {

    // Yea i know a mixin isn't needed but uhhh dementia
    @Redirect(
        method = "processCommand",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/management/PlayerProfileCache;func_152655_a(Ljava/lang/String;)Lcom/mojang/authlib/GameProfile;"))
    private GameProfile whitelistDementiaNoMore$editLookedUpProfile(PlayerProfileCache profileCache, String username,
        ICommandSender sender, String[] args) {
        GameProfile gameProfile = profileCache.func_152655_a(username);

        if (Config.moduleWhitelistDementiaNoMoreEnabled && gameProfile != null
            && !MinecraftServer.getServer()
                .isServerInOnlineMode()) {

            UUID onlineUUID = gameProfile.getId();
            UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

            if (!onlineUUID.equals(offlineUUID)) {
                try {
                    Field idField = GameProfile.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(gameProfile, offlineUUID);

                    WhitelistDementiaNoMore.LOG.info(
                        "Successfully swapped " + username
                            + "'s whitelist UUID from "
                            + onlineUUID
                            + " to "
                            + offlineUUID);
                } catch (Exception e) {
                    WhitelistDementiaNoMore.LOG
                        .error("Couldn't edit the uuid of the new player due to error: " + e.toString());
                }
            } else {
                WhitelistDementiaNoMore.LOG.info(username + "'s whitelist UUID is already offline");
            }
        }

        return gameProfile;
    }
}

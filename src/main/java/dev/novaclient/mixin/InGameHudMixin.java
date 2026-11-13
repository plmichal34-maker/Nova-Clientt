package dev.novaclient.mixin;

import dev.novaclient.modules.CrosshairModule;
import dev.novaclient.render.ItemScaler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void nova$hotbarHead(DrawContext context, int x, int y, RenderTickCounter tickCounter,
                                 PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        ItemScaler.inHotbar = true;
    }

    @Inject(method = "renderHotbarItem", at = @At("RETURN"))
    private void nova$hotbarReturn(DrawContext context, int x, int y, RenderTickCounter tickCounter,
                                   PlayerEntity player, ItemStack stack, int seed, CallbackInfo ci) {
        ItemScaler.inHotbar = false;
    }

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void nova$crosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CrosshairModule.shouldReplace()) ci.cancel();
    }
}

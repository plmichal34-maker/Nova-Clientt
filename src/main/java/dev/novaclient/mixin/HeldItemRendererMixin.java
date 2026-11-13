package dev.novaclient.mixin;

import dev.novaclient.modules.HeldItemModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Unique private boolean nova$pushed;

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void nova$head(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                           float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        nova$pushed = HeldItemModule.apply(matrices, hand);
    }

    @Inject(method = "renderFirstPersonItem", at = @At("RETURN"))
    private void nova$return(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
                             float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices,
                             VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (nova$pushed) {
            matrices.pop();
            nova$pushed = false;
        }
    }
}

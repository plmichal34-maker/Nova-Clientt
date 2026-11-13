package dev.novaclient.render;

import dev.novaclient.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;

/** Rysowanie elementow HUD (celownik, pasek szybkiego dostepu, efekty na pasku). */
public final class HudRenderer {
    private HudRenderer() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden || mc.currentScreen != null) return;
        if (ModuleManager.CROSSHAIR.isEnabled()) ModuleManager.CROSSHAIR.render(ctx, mc);
        if (mc.player.isSpectator()) return;
        if (ModuleManager.HOTBAR_STYLE.isEnabled()) ModuleManager.HOTBAR_STYLE.render(ctx, mc);
        if (Effects.active()) {
            int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
            for (int i = 0; i < 9; i++) {
                ItemStack st = mc.player.getInventory().getStack(i);
                if (!st.isEmpty()) Effects.render(ctx, w / 2 - 88 + i * 20, h - 19, st, ItemTarget.HOTBAR);
            }
        }
    }
}

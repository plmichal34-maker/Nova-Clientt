package dev.novaclient.render;

import dev.novaclient.anim.AnimType;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.modules.GuiItemsModule;
import dev.novaclient.modules.InterfaceModule;
import dev.novaclient.util.Time;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.client.util.math.MatrixStack;

/** Skalowanie i przesuwanie przedmiotow rysowanych w GUI (wywolywane z mixina DrawContext). */
public final class ItemScaler {
    /** Ustawiane przez mixin InGameHud podczas rysowania slotow paska szybkiego dostepu. */
    public static boolean inHotbar;

    private static final float[] HOVER = new float[512];
    private static long lastDt;
    private static float dt = 0.016f;

    private ItemScaler() {}

    public static boolean begin(MatrixStack ms, int x, int y) {
        GuiItemsModule m = ModuleManager.GUI_ITEMS;
        if (m == null || !m.isEnabled()) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        Screen screen = mc.currentScreen;
        float s;
        boolean handled = screen instanceof HandledScreen;
        if (inHotbar) {
            s = m.hotbarScale.get();
        } else if (handled) {
            s = (screen instanceof GenericContainerScreen || screen instanceof ShulkerBoxScreen)
                    ? m.chestScale.get() : m.inventoryScale.get();
        } else {
            return false;
        }
        float pop = m.hoverPop.get();
        if (pop > 0f && handled && !inHotbar) {
            double sf = mc.getWindow().getScaleFactor();
            double mx = mc.mouse.getX() / sf, my = mc.mouse.getY() / sf;
            boolean hover = mx >= x && mx < x + 16 && my >= y && my < y + 16;
            int idx = (x * 31 + y * 17) & 511;
            float target = hover ? 1f : 0f;
            long now = System.nanoTime();
            if (now - lastDt > 1_000_000L) {
                dt = Math.min(0.1f, (now - lastDt) / 1e9f);
                lastDt = now;
            }
            if (!InterfaceModule.animEnabled(AnimType.INVENTORY)) {
                HOVER[idx] = target;
            } else {
                HOVER[idx] += (target - HOVER[idx]) * (1f - (float) Math.exp(-18f * InterfaceModule.animSpeed() * dt));
            }
            s *= 1f + pop * HOVER[idx];
        }
        float ox = m.offsetX.get(), oy = m.offsetY.get();
        if (s == 1f && ox == 0f && oy == 0f) return false;
        ms.push();
        ms.translate(x + 8 + ox, y + 8 + oy, 0f);
        ms.scale(s, s, 1f);
        ms.translate(-(x + 8), -(y + 8), 0f);
        return true;
    }
}

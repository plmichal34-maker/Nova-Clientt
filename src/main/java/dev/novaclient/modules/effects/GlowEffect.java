package dev.novaclient.modules.effects;

import dev.novaclient.modules.PerformanceModule;
import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Delikatna poswiata wokol przedmiotu. */
public final class GlowEffect extends EffectModule {
    public GlowEffect() {
        super("fx_glow", "Glow / poswiata", "Miekka poswiata wokol przedmiotow.", 0x60A5FA, 4f, 8f);
    }

    @Override
    protected void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        int px = (int) size.get();
        int layers = Math.max(1, Math.min(px, PerformanceModule.glowQuality() * 2));
        float stepPx = px / (float) layers;
        int th = Math.max(1, Math.round(stepPx));
        float pulse = 0.85f + 0.15f * (float) Math.sin(t * 2f);
        for (int i = 1; i <= layers; i++) {
            int k = Math.round(i * stepPx);
            float fall = 1f - (i - 1) / (float) layers;
            RenderUtil.roundedOutline(c, x - k, y - k, x + 16 + k, y + 16 + k, 2 + k, th,
                    Colors.withAlpha(color.get(), a * 0.30f * fall * pulse));
        }
    }
}

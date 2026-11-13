package dev.novaclient.modules.effects;

import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Pulsujaca ramka. */
public final class PulseEffect extends EffectModule {
    public PulseEffect() {
        super("fx_pulse", "Pulsowanie", "Ramka, ktora pulsuje rozmiarem i jasnoscia.", 0xF472B6, 3f, 5f);
    }

    @Override
    protected void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        float s = 0.5f + 0.5f * (float) Math.sin(t * 3f);
        int th = Math.max(1, Math.round(1f + (size.get() - 1f) * s));
        RenderUtil.roundedOutline(c, x - th, y - th, x + 16 + th, y + 16 + th, 2 + th, th,
                Colors.withAlpha(color.get(), a * (0.35f + 0.65f * s)));
    }
}

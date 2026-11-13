package dev.novaclient.modules.effects;

import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Kolorowe obramowanie przedmiotu. */
public final class BorderEffect extends EffectModule {
    public BorderEffect() {
        super("fx_border", "Kolorowe obramowanie", "Zaokraglona ramka wokol przedmiotow.", 0xFACC15, 1f, 4f);
    }

    @Override
    protected void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        int th = (int) size.get();
        RenderUtil.roundedOutline(c, x - th, y - th, x + 16 + th, y + 16 + th, 2 + th, th,
                Colors.withAlpha(color.get(), a * 0.9f));
    }
}

package dev.novaclient.modules.effects;

import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Blysk enchantingu - tylko dla zaklietych przedmiotow. */
public final class EnchantEffect extends EffectModule {
    public EnchantEffect() {
        super("fx_enchant", "Efekt enchantingu", "Ukosny blysk i fioletowa ramka na zaklietych przedmiotach.", 0xA855F7, 1f, 3f);
    }

    @Override
    protected void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        if (!stack.hasGlint()) return;
        int th = (int) size.get();
        RenderUtil.roundedOutline(c, x - th, y - th, x + 16 + th, y + 16 + th, 2 + th, th,
                Colors.withAlpha(color.get(), a * 0.5f));
        float s = (t * 10f) % 40f - 8f;
        int col = Colors.withAlpha(0xFFFFFF, a * 0.35f);
        for (int r = 0; r < 16; r++) {
            int cx = (int) s - r;
            if (cx < 0 || cx >= 16) continue;
            c.fill(x + cx, y + r, x + Math.min(16, cx + 2), y + r + 1, col);
        }
    }
}

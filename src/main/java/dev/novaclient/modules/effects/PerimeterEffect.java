package dev.novaclient.modules.effects;

import dev.novaclient.util.Colors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Efekt rysowany jako kolorowy obwod wokol przedmiotu (kolor zalezy od pozycji i czasu). */
public abstract class PerimeterEffect extends EffectModule {
    private static final int SIDE = 20;

    protected PerimeterEffect(String id, String name, String desc, int defColor) {
        super(id, name, desc, defColor, 2f, 4f);
    }

    /** f: pozycja na obwodzie 0..1, t: czas. Zwraca kolor RGB. */
    protected abstract int colorAt(float f, float t);

    @Override
    protected final void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        int th = Math.max(2, (int) size.get());
        float alpha = a * 0.9f;
        for (int side = 0; side < 4; side++) {
            for (int o = 0; o < SIDE; o += 2) {
                int col = Colors.withAlpha(colorAt((side * SIDE + o) / (float) (4 * SIDE), t), alpha);
                switch (side) {
                    case 0: c.fill(x - 2 + o, y - th, x + o, y, col); break;
                    case 1: c.fill(x + 16, y - 2 + o, x + 16 + th, y + o, col); break;
                    case 2: { int xx = x + 18 - o; c.fill(xx - 2, y + 16, xx, y + 16 + th, col); break; }
                    default: { int yy = y + 18 - o; c.fill(x - th, yy - 2, x, yy, col); break; }
                }
            }
        }
    }
}

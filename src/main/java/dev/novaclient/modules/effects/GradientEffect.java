package dev.novaclient.modules.effects;

import dev.novaclient.core.ColorSetting;
import dev.novaclient.util.Colors;

/** Animowany gradient dwoch kolorow wokol przedmiotu. */
public final class GradientEffect extends PerimeterEffect {
    public final ColorSetting color2;

    public GradientEffect() {
        super("fx_gradient", "Animowany gradient", "Plynnie przesuwajacy sie gradient dwoch kolorow na obwodzie przedmiotu.", 0x8B5CF6);
        color2 = add(new ColorSetting("color2", "Drugi kolor", "Drugi kolor gradientu.", 0x22D3EE));
    }

    @Override
    protected int colorAt(float f, float t) {
        float mix = 0.5f + 0.5f * (float) Math.sin(f * 6.2831855f * 2f - t * 2f);
        return Colors.lerp(color.argb(), color2.argb(), mix) & 0xFFFFFF;
    }
}

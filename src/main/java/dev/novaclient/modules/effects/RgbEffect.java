package dev.novaclient.modules.effects;

import dev.novaclient.util.Colors;

/** Obwod w kolorach teczy. */
public final class RgbEffect extends PerimeterEffect {
    public RgbEffect() {
        super("fx_rgb", "Efekt RGB", "Teczowy obwod przedmiotu zmieniajacy kolory w czasie (kolor bazowy nie ma znaczenia).", 0xFF0000);
    }

    @Override
    protected int colorAt(float f, float t) {
        return Colors.hsv(f + t * 0.25f, 0.85f, 1f) & 0xFFFFFF;
    }
}

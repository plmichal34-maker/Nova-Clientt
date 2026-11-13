package dev.novaclient.util;

/** Pomocnicze funkcje na kolorach ARGB (bez alokacji). */
public final class Colors {
    private Colors() {}

    public static float clamp01(float v) { return v < 0f ? 0f : (v > 1f ? 1f : v); }

    /** Zwraca kolor z kanalem alpha ustawionym na a (0..1), reszta z rgb. */
    public static int withAlpha(int rgb, float a) {
        return ((int) (clamp01(a) * 255f + 0.5f) << 24) | (rgb & 0xFFFFFF);
    }

    /** Mnozy alpha koloru ARGB przez m. */
    public static int mulAlpha(int argb, float m) {
        int a = (int) (((argb >>> 24) & 0xFF) * clamp01(m) + 0.5f);
        return (a << 24) | (argb & 0xFFFFFF);
    }

    public static int alpha(int argb) { return (argb >>> 24) & 0xFF; }

    public static int lerp(int c1, int c2, float t) {
        t = clamp01(t);
        int a = (int) (((c1 >>> 24) & 0xFF) + (((c2 >>> 24) & 0xFF) - ((c1 >>> 24) & 0xFF)) * t);
        int r = (int) (((c1 >> 16) & 0xFF) + (((c2 >> 16) & 0xFF) - ((c1 >> 16) & 0xFF)) * t);
        int g = (int) (((c1 >> 8) & 0xFF) + (((c2 >> 8) & 0xFF) - ((c1 >> 8) & 0xFF)) * t);
        int b = (int) ((c1 & 0xFF) + ((c2 & 0xFF) - (c1 & 0xFF)) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** HSV (h,s,v w zakresie 0..1) -> 0xFFRRGGBB. */
    public static int hsv(float h, float s, float v) {
        h = h - (float) Math.floor(h);
        float h6 = h * 6f;
        int i = (int) h6;
        float f = h6 - i;
        float p = v * (1f - s), q = v * (1f - s * f), t = v * (1f - s * (1f - f));
        float r, g, b;
        switch (i % 6) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return 0xFF000000 | ((int) (r * 255f + 0.5f) << 16) | ((int) (g * 255f + 0.5f) << 8) | (int) (b * 255f + 0.5f);
    }

    /** Odcien (0..1) z koloru RGB. */
    public static float hue(int rgb) {
        float r = ((rgb >> 16) & 0xFF) / 255f, g = ((rgb >> 8) & 0xFF) / 255f, b = (rgb & 0xFF) / 255f;
        float max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        float d = max - min;
        if (d < 1e-5f) return 0f;
        float h;
        if (max == r) h = ((g - b) / d) % 6f;
        else if (max == g) h = (b - r) / d + 2f;
        else h = (r - g) / d + 4f;
        h /= 6f;
        return h < 0 ? h + 1f : h;
    }
}

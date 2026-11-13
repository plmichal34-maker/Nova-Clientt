package dev.novaclient.modules;

import dev.novaclient.anim.AnimType;
import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ChoiceSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;

/** Globalne ustawienia wygladu clienta, animacji i efektow. */
public final class InterfaceModule extends Module {
    private static InterfaceModule I;

    private static final String[] THEME_NAMES = {"Fiolet", "Ocean", "Szmaragd", "Zachod", "Roz", "Mono"};
    private static final int[] THEME_A = {0x8B5CF6, 0x38BDF8, 0x34D399, 0xFB923C, 0xF472B6, 0xD4D4D8};
    private static final int[] THEME_B = {0xEC4899, 0x6366F1, 0x22D3EE, 0xF43F5E, 0xFBBF24, 0x71717A};

    public final BoolSetting disableAnims = add(new BoolSetting("disable_all_animations", "Wylacz wszystkie animacje",
            "Globalnie wylacza kazda animacje clienta (GUI, HUD, ekwipunek). Wartosci zmieniaja sie natychmiast.", false));
    private final BoolSetting[] animToggles = new BoolSetting[AnimType.values().length];
    public final FloatSetting animSpeed = add(new FloatSetting("animation_speed", "Szybkosc animacji",
            "Mnoznik szybkosci wszystkich animacji.", 1f, 0.25f, 3f, 0.05f, "x"));
    public final BoolSetting disableEffects = add(new BoolSetting("disable_all_effects", "Wylacz wszystkie efekty",
            "Globalnie wylacza efekty wizualne przedmiotow (poswiata, ramki, czasteczki...).", false));
    public final FloatSetting effectIntensity = add(new FloatSetting("effect_intensity", "Globalna intensywnosc efektow",
            "Mnoznik intensywnosci wszystkich efektow.", 1f, 0f, 2f, 0.05f, "x"));
    public final ChoiceSetting theme = add(new ChoiceSetting("theme", "Motyw kolorystyczny",
            "Gotowy zestaw kolorow akcentu GUI.", 0, THEME_NAMES));
    public final BoolSetting useCustomAccent = add(new BoolSetting("use_custom_accent", "Wlasny kolor akcentu",
            "Uzywaj wlasnego koloru zamiast motywu.", false));
    public final ColorSetting customAccent = add(new ColorSetting("custom_accent", "Kolor akcentu",
            "Wlasny kolor akcentu GUI.", 0x8B5CF6));
    public final FloatSetting guiScale = add(new FloatSetting("gui_scale", "Skala GUI",
            "Skalowanie calego okna clienta.", 1f, 0.6f, 1.6f, 0.05f, "x"));
    public final FloatSetting guiOpacity = add(new FloatSetting("gui_opacity", "Przezroczystosc GUI",
            "Krycie tla okna clienta (nizsza wartosc = bardziej przezroczyste).", 0.82f, 0.3f, 1f, 0.02f));
    public final BoolSetting blur = add(new BoolSetting("gui_blur", "Rozmycie tla",
            "Rozmywa gre za oknem clienta (wymaga wlaczonego rozmycia tla menu w opcjach Minecrafta).", true));

    public InterfaceModule() {
        super("interface", "Interfejs i animacje", "Globalne ustawienia wygladu, animacji i efektow.",
                Category.SETTINGS, true, true);
        for (AnimType t : AnimType.values()) {
            animToggles[t.ordinal()] = add(new BoolSetting("anim_" + t.name().toLowerCase(), t.label, t.description, true));
        }
        I = this;
    }

    public static boolean animEnabled(AnimType t) {
        if (I == null) return true;
        return !I.disableAnims.get() && I.animToggles[t.ordinal()].get();
    }

    public static float animSpeed() { return I == null ? 1f : I.animSpeed.get(); }

    public static boolean effectsAllowed() { return I == null || !I.disableEffects.get(); }

    public static float effectIntensity() { return I == null ? 1f : I.effectIntensity.get(); }

    public static int accent() {
        if (I == null) return 0xFF8B5CF6;
        return 0xFF000000 | (I.useCustomAccent.get() ? I.customAccent.get() : THEME_A[I.theme.get()]);
    }

    public static int accent2() {
        if (I == null) return 0xFFEC4899;
        return 0xFF000000 | (I.useCustomAccent.get() ? shift(I.customAccent.get()) : THEME_B[I.theme.get()]);
    }

    private static int shift(int rgb) {
        float h = dev.novaclient.util.Colors.hue(rgb);
        return dev.novaclient.util.Colors.hsv(h + 0.12f, 0.7f, 1f);
    }

    public static float guiScale() { return I == null ? 1f : I.guiScale.get(); }

    public static float guiOpacity() { return I == null ? 0.82f : I.guiOpacity.get(); }

    public static boolean blur() { return I == null || I.blur.get(); }
}

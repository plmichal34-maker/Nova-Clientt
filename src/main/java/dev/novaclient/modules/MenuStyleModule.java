package dev.novaclient.modules;

import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;

/** Nakladka kolorystyczna na menu glowne i ekran pauzy. */
public final class MenuStyleModule extends Module {
    public final BoolSetting onTitle = add(new BoolSetting("on_title", "Menu glowne", "Stosuj do menu glownego.", true));
    public final BoolSetting onPause = add(new BoolSetting("on_pause", "Ekran pauzy", "Stosuj do ekranu pauzy.", true));
    public final ColorSetting color1 = add(new ColorSetting("color1", "Kolor gorny", "Kolor u gory ekranu.", 0x6D28D9));
    public final ColorSetting color2 = add(new ColorSetting("color2", "Kolor dolny", "Kolor na dole ekranu.", 0x0EA5E9));
    public final FloatSetting alpha = add(new FloatSetting("alpha", "Krycie", "Sila nakladki kolorystycznej.", 0.25f, 0f, 0.8f, 0.05f));
    public final BoolSetting animated = add(new BoolSetting("animated", "Animowany gradient", "Kolory plynnie przechodza jeden w drugi.", true));
    public final FloatSetting speed = add(new FloatSetting("speed", "Szybkosc", "Szybkosc animacji gradientu.", 0.5f, 0.1f, 3f, 0.1f, "x"));
    public final FloatSetting vignette = add(new FloatSetting("vignette", "Przyciemnienie gora/dol", "Ciemne pasy u gory i dolu ekranu.", 0.3f, 0f, 0.9f, 0.05f));

    public MenuStyleModule() {
        super("menu_style", "Menu i ekran pauzy", "Kolorystyka menu glownego i ekranu pauzy.", Category.VISUALS, false, false);
    }
}

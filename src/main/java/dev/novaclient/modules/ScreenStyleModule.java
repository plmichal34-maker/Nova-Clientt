package dev.novaclient.modules;

import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ChoiceSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;

/** Styl okien ekwipunku (instancja zwykla) i skrzyn (instancja chest). */
public final class ScreenStyleModule extends Module {
    public final boolean chest;
    public final ColorSetting tintColor, panelColor, slotColor, borderColor, borderColor2, hoverColor;
    public final FloatSetting tintAlpha, panelAlpha, slotAlpha, slotRadius, hoverAlpha, borderWidth;
    public final BoolSetting roundedSlots;
    public final ChoiceSetting borderMode;

    public ScreenStyleModule(String id, String name, String desc, boolean chest, boolean defEnabled) {
        super(id, name, desc, Category.INVENTORY, defEnabled, false);
        this.chest = chest;
        tintColor = add(new ColorSetting("tint_color", "Kolor tla ekranu", "Kolor polprzezroczystej warstwy za oknem.", 0x0B0B1A));
        tintAlpha = add(new FloatSetting("tint_alpha", "Krycie tla ekranu", "Przezroczystosc warstwy za oknem.", 0.25f, 0f, 0.9f, 0.05f));
        panelColor = add(new ColorSetting("panel_color", "Kolor panelu", "Delikatne zabarwienie panelu okna.", 0x8B5CF6));
        panelAlpha = add(new FloatSetting("panel_alpha", "Krycie panelu", "Intensywnosc zabarwienia panelu (zostaw niskie, bo nakrywa przedmioty).", 0f, 0f, 0.4f, 0.01f));
        roundedSlots = add(new BoolSetting("rounded_slots", "Zaokraglone sloty", "Rysuje zaokraglone, kolorowe sloty.", true));
        slotRadius = add(new FloatSetting("slot_radius", "Zaokraglenie slotow", "Promien zaokraglenia rogow.", 3f, 0f, 8f, 1f, " px"));
        slotColor = add(new ColorSetting("slot_color", "Kolor slotow", "Kolor wypelnienia slotow.", 0x6366F1));
        slotAlpha = add(new FloatSetting("slot_alpha", "Krycie slotow", "Przezroczystosc wypelnienia slotow.", 0.18f, 0f, 0.6f, 0.01f));
        borderMode = add(new ChoiceSetting("border_mode", "Obramowanie slotow", "Rodzaj obramowania slotow.", 1,
                "Brak", "Staly kolor", "Animowana tecza", "Animowany gradient"));
        borderColor = add(new ColorSetting("border_color", "Kolor obramowania", "Kolor (lub pierwszy kolor gradientu).", 0x8B5CF6));
        borderColor2 = add(new ColorSetting("border_color2", "Drugi kolor obramowania", "Drugi kolor gradientu.", 0x22D3EE));
        borderWidth = add(new FloatSetting("border_width", "Grubosc obramowania", "Grubosc obramowania slotow.", 1f, 1f, 2f, 1f, " px"));
        hoverColor = add(new ColorSetting("hover_color", "Kolor po najechaniu", "Kolor slotu pod kursorem.", 0xFFFFFF));
        hoverAlpha = add(new FloatSetting("hover_alpha", "Sila podswietlenia", "Intensywnosc podswietlenia slotu pod kursorem.", 0.3f, 0f, 0.8f, 0.02f));
    }
}

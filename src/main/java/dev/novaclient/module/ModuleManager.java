package dev.novaclient.module;

import dev.novaclient.modules.*;
import dev.novaclient.modules.effects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Rejestr wszystkich funkcji. Aby dodac nowa funkcje: utworz klase rozszerzajaca {@link Module}
 * i dopisz ja w {@link #init()} - pojawi sie w GUI, w wyszukiwarce i w konfiguracji.
 */
public final class ModuleManager {
    private static final List<Module> ALL = new ArrayList<>();
    private static final List<EffectModule> EFFECTS = new ArrayList<>();

    public static HeldItemModule HELD_ITEM;
    public static GuiItemsModule GUI_ITEMS;
    public static DroppedItemModule DROPPED_ITEM;
    public static ScreenStyleModule INVENTORY_STYLE;
    public static ScreenStyleModule CHEST_STYLE;
    public static HotbarStyleModule HOTBAR_STYLE;
    public static CrosshairModule CROSSHAIR;
    public static MenuStyleModule MENU_STYLE;

    private ModuleManager() {}

    public static void init() {
        ALL.clear();
        EFFECTS.clear();
        // Przedmioty
        HELD_ITEM = register(new HeldItemModule());
        GUI_ITEMS = register(new GuiItemsModule());
        DROPPED_ITEM = register(new DroppedItemModule());
        // Efekty
        registerEffect(new GlowEffect());
        registerEffect(new BorderEffect());
        registerEffect(new GradientEffect());
        registerEffect(new RgbEffect());
        registerEffect(new PulseEffect());
        registerEffect(new ParticlesEffect());
        registerEffect(new EnchantEffect());
        // Ekwipunek
        INVENTORY_STYLE = register(new ScreenStyleModule("inventory_style", "Wyglad ekwipunku",
                "Tlo, sloty, obramowania i podswietlenia okna ekwipunku.", false, true));
        CHEST_STYLE = register(new ScreenStyleModule("chest_style", "Wyglad skrzyn",
                "Tlo, sloty, obramowania i podswietlenia okien skrzyn.", true, false));
        HOTBAR_STYLE = register(new HotbarStyleModule());
        // Wizualne
        CROSSHAIR = register(new CrosshairModule());
        MENU_STYLE = register(new MenuStyleModule());
        // Ustawienia
        register(new InterfaceModule());
        register(new PerformanceModule());
    }

    private static <M extends Module> M register(M m) {
        ALL.add(m);
        return m;
    }

    private static void registerEffect(EffectModule m) {
        register(m);
        EFFECTS.add(m);
    }

    public static List<Module> all() { return ALL; }

    public static List<EffectModule> effects() { return EFFECTS; }

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) out.add(m);
        return out;
    }

    /** Wyszukiwanie po nazwie, opisie i nazwach/opisach ustawien. */
    public static List<Module> search(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        if (q.isEmpty()) return Collections.emptyList();
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) {
            boolean hit = m.name.toLowerCase(Locale.ROOT).contains(q) || m.description.toLowerCase(Locale.ROOT).contains(q);
            if (!hit) {
                for (dev.novaclient.core.Setting s : m.settings) {
                    if (s.name.toLowerCase(Locale.ROOT).contains(q) || s.description.toLowerCase(Locale.ROOT).contains(q)) { hit = true; break; }
                }
            }
            if (hit) out.add(m);
        }
        return out;
    }
}

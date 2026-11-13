package dev.novaclient.modules.effects;

import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.modules.InterfaceModule;
import dev.novaclient.modules.PerformanceModule;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import dev.novaclient.render.ItemTarget;
import dev.novaclient.util.Time;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/**
 * Bazowa klasa efektu wizualnego. Kazdy efekt to osobny modul z kolorem, intensywnoscia,
 * szybkoscia, rozmiarem i przezroczystoscia. Efekt rysuje sie wokol prostokata 16x16 (x,y).
 * Nowy efekt: rozszerz te klase i zarejestruj w ModuleManager.
 */
public abstract class EffectModule extends Module {
    public final ColorSetting color;
    public final FloatSetting intensity, speed, size, opacity;
    public final BoolSetting onHotbar, onInventory, onChest;

    protected EffectModule(String id, String name, String desc, int defColor, float defSize, float maxSize) {
        super(id, name, desc, Category.EFFECTS, false, false);
        color = add(new ColorSetting("color", "Kolor", "Kolor efektu.", defColor));
        intensity = add(new FloatSetting("intensity", "Intensywnosc", "Sila efektu.", 1f, 0f, 2f, 0.05f, "x"));
        speed = add(new FloatSetting("speed", "Szybkosc animacji", "Jak szybko animuje sie efekt.", 1f, 0.1f, 4f, 0.1f, "x"));
        size = add(new FloatSetting("size", "Rozmiar", "Rozmiar efektu w pikselach.", defSize, 1f, maxSize, 1f, " px"));
        opacity = add(new FloatSetting("opacity", "Przezroczystosc", "Krycie efektu.", 0.8f, 0.05f, 1f, 0.05f));
        onHotbar = add(new BoolSetting("on_hotbar", "Pasek szybkiego dostepu", "Rysuj efekt na pasku szybkiego dostepu.", true));
        onInventory = add(new BoolSetting("on_inventory", "Ekwipunek", "Rysuj efekt w ekwipunku i innych oknach.", true));
        onChest = add(new BoolSetting("on_chest", "Skrzynie", "Rysuj efekt w oknach skrzyn.", true));
    }

    public final void render(DrawContext c, int x, int y, ItemStack stack, ItemTarget t) {
        if (!isEnabled()) return;
        switch (t) {
            case HOTBAR: if (!onHotbar.get()) return; break;
            case INVENTORY: if (!onInventory.get()) return; break;
            default: if (!onChest.get()) return; break;
        }
        float a = opacity.get() * intensity.get() * InterfaceModule.effectIntensity();
        if (a <= 0.01f) return;
        draw(c, x, y, stack, a, Time.seconds() * speed.get());
    }

    /** a moze przekraczac 1 (intensywnosc do 2x) - Colors.withAlpha je obcina. */
    protected abstract void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t);

    public static boolean globallyAllowed() { return PerformanceModule.effectsAllowedNow(); }
}

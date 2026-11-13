package dev.novaclient.modules;

import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.util.Time;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

/** Wyglad przedmiotow wyrzuconych na ziemie (rozmiar, dodatkowy obrot i unoszenie). */
public final class DroppedItemModule extends Module {
    public final FloatSetting scale = add(new FloatSetting("scale", "Rozmiar", "Rozmiar przedmiotow lezacych na ziemi.", 1f, 0.3f, 3f, 0.05f, "x"));
    public final FloatSetting height = add(new FloatSetting("height", "Wysokosc", "Podniesienie przedmiotu nad ziemie.", 0f, -0.2f, 1f, 0.02f));
    public final FloatSetting spin = add(new FloatSetting("spin", "Dodatkowy obrot", "Dodatkowa predkosc obrotu (stopnie na sekunde).", 0f, 0f, 360f, 10f, "°/s"));
    public final FloatSetting bob = add(new FloatSetting("bob", "Dodatkowe unoszenie", "Amplituda dodatkowego unoszenia sie w gore i w dol.", 0f, 0f, 0.5f, 0.02f));

    public DroppedItemModule() {
        super("dropped_item", "Przedmioty na ziemi", "Zmiana wygladu przedmiotow wyrzuconych na ziemie.",
                Category.ITEMS, false, false);
    }

    public static boolean apply(MatrixStack ms) {
        DroppedItemModule m = ModuleManager.DROPPED_ITEM;
        if (m == null || !m.isEnabled()) return false;
        float t = Time.seconds();
        ms.push();
        ms.translate(0f, m.height.get() + (float) Math.sin(t * 2f) * m.bob.get(), 0f);
        float s = m.scale.get();
        ms.scale(s, s, s);
        if (m.spin.get() > 0f) {
            ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((t * m.spin.get()) % 360f));
        }
        return true;
    }
}

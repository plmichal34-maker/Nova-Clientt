package dev.novaclient.modules;

import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.module.Module;
import dev.novaclient.util.Time;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

/** Rozmiar, pozycja i delikatne kolysanie przedmiotu trzymanego w rece (tylko wyglad). */
public final class HeldItemModule extends Module {
    public final FloatSetting scale = add(new FloatSetting("scale", "Rozmiar", "Rozmiar przedmiotu w rece.", 1f, 0.2f, 2.5f, 0.05f, "x"));
    public final FloatSetting posX = add(new FloatSetting("pos_x", "Pozycja X", "Przesuniecie w lewo/prawo.", 0f, -1f, 1f, 0.02f));
    public final FloatSetting posY = add(new FloatSetting("pos_y", "Pozycja Y", "Przesuniecie w gore/dol.", 0f, -1f, 1f, 0.02f));
    public final FloatSetting posZ = add(new FloatSetting("pos_z", "Pozycja Z", "Przesuniecie blizej/dalej.", 0f, -1f, 1f, 0.02f));
    public final FloatSetting sway = add(new FloatSetting("sway", "Kolysanie", "Delikatne unoszenie sie przedmiotu w bezruchu.", 0f, 0f, 3f, 0.1f, "x"));
    public final BoolSetting offhand = add(new BoolSetting("offhand", "Dotyczy tez lewej reki", "Stosuj ustawienia rowniez do lewej reki.", true));

    public HeldItemModule() {
        super("held_item", "Przedmiot w rece", "Zmiana rozmiaru, pozycji i animacji przedmiotu trzymanego w rece.",
                Category.ITEMS, false, false);
    }

    /** Zwraca true, jesli wykonano push() - wtedy trzeba wywolac pop(). */
    public static boolean apply(MatrixStack ms, Hand hand) {
        HeldItemModule m = ModuleManager.HELD_ITEM;
        if (m == null || !m.isEnabled()) return false;
        if (hand == Hand.OFF_HAND && !m.offhand.get()) return false;
        float side = hand == Hand.OFF_HAND ? -1f : 1f;
        float t = Time.seconds();
        float sw = m.sway.get();
        ms.push();
        ms.translate(m.posX.get() * side + (float) Math.sin(t * 1.7f) * 0.01f * sw,
                m.posY.get() + (float) Math.cos(t * 1.3f) * 0.01f * sw,
                m.posZ.get());
        float s = m.scale.get();
        ms.translate(0.56f * side, -0.52f, -0.72f);
        ms.scale(s, s, s);
        ms.translate(-0.56f * side, 0.52f, 0.72f);
        return true;
    }
}

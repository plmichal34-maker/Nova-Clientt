package dev.novaclient.modules;

import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import net.minecraft.client.MinecraftClient;

/** Ustawienia wydajnosci efektow. */
public final class PerformanceModule extends Module {
    private static PerformanceModule I;

    public final FloatSetting maxParticles = add(new FloatSetting("max_particles", "Maks. czasteczek na przedmiot",
            "Ogranicza liczbe czasteczek rysowanych przy jednym przedmiocie.", 4f, 0f, 8f, 1f));
    public final FloatSetting glowQuality = add(new FloatSetting("glow_quality", "Jakosc poswiaty",
            "Liczba warstw poswiaty (nizsza = wyzsze FPS).", 2f, 1f, 4f, 1f));
    public final FloatSetting minFps = add(new FloatSetting("min_fps", "Wylacz efekty ponizej FPS",
            "Gdy FPS spadnie ponizej tej wartosci, efekty sa chwilowo wylaczane. 0 = nigdy.", 0f, 0f, 120f, 5f));

    public PerformanceModule() {
        super("performance", "Wydajnosc", "Ograniczenia kosztownych efektow wizualnych.",
                Category.SETTINGS, true, true);
        I = this;
    }

    public static int maxParticles() { return I == null ? 4 : I.maxParticles.getInt(); }

    public static int glowQuality() { return I == null ? 2 : I.glowQuality.getInt(); }

    /** Czy efekty moga byc teraz rysowane (globalny wylacznik + prog FPS). */
    public static boolean effectsAllowedNow() {
        if (!InterfaceModule.effectsAllowed()) return false;
        if (I == null) return true;
        int min = I.minFps.getInt();
        return min <= 0 || MinecraftClient.getInstance().getCurrentFps() >= min;
    }
}

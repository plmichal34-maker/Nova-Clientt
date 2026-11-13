package dev.novaclient.modules.effects;

import dev.novaclient.modules.PerformanceModule;
import dev.novaclient.util.Colors;
import dev.novaclient.util.Time;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

/** Unoszace sie iskierki wokol przedmiotu (deterministyczne, bez alokacji). */
public final class ParticlesEffect extends EffectModule {
    public ParticlesEffect() {
        super("fx_particles", "Czasteczki", "Male iskry unoszace sie nad przedmiotami.", 0xFDE68A, 2f, 3f);
    }

    @Override
    protected void draw(DrawContext c, int x, int y, ItemStack stack, float a, float t) {
        int n = Math.min(PerformanceModule.maxParticles(), 8);
        if (n <= 0) return;
        int sz = (int) size.get();
        for (int i = 0; i < n; i++) {
            float ph = (t * 0.4f + i / (float) n + Time.hash01(x, y, i)) % 1f;
            int px = x - 2 + (int) (Time.hash01(i, x, y) * 20f);
            int py = y + 16 - (int) (ph * 22f);
            float fade = (float) Math.sin(ph * Math.PI);
            c.fill(px, py, px + sz, py + sz, Colors.withAlpha(color.get(), a * fade));
        }
    }
}

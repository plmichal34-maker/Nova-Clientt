package dev.novaclient.anim;

import dev.novaclient.modules.InterfaceModule;

/** Plynna wartosc dazaca do celu (wygladzanie wykladnicze). Respektuje ustawienia animacji. */
public final class Anim {
    public float value;
    public float target;
    private final AnimType type;
    private final float speed;
    private long last;

    public Anim(AnimType type, float speed, float initial) {
        this.type = type;
        this.speed = speed;
        this.value = initial;
        this.target = initial;
    }

    public void set(float t) { this.target = t; }

    public void snap(float v) { this.value = v; this.target = v; }

    public float update() {
        long now = System.nanoTime();
        float dt = last == 0 ? 0.016f : Math.min(0.1f, (now - last) / 1e9f);
        last = now;
        if (!InterfaceModule.animEnabled(type)) {
            value = target;
            return value;
        }
        float k = 1f - (float) Math.exp(-speed * InterfaceModule.animSpeed() * dt);
        value += (target - value) * k;
        if (Math.abs(target - value) < 0.0015f) value = target;
        return value;
    }
}

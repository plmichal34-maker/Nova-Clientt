package dev.novaclient.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;

public final class FloatSetting extends Setting {
    public final float min, max, step;
    public final String unit;
    public final int decimals;
    private float value;
    private final float def;
    /** Animowana pozycja suwaka 0..1 (uzywana tylko w GUI). */
    public final Anim anim;

    public FloatSetting(String id, String name, String description, float def, float min, float max, float step, String unit) {
        super(id, name, description);
        this.min = min;
        this.max = max;
        this.step = step;
        this.unit = unit;
        this.def = def;
        this.value = def;
        int d = 0;
        float s = step;
        while (d < 4 && Math.abs(s - Math.round(s)) > 1e-4f) { s *= 10f; d++; }
        this.decimals = d;
        this.anim = new Anim(AnimType.SETTING_CHANGE, 18f, fraction());
    }

    public FloatSetting(String id, String name, String description, float def, float min, float max, float step) {
        this(id, name, description, def, min, max, step, "");
    }

    public float get() { return value; }

    public int getInt() { return Math.round(value); }

    public float fraction() { return max == min ? 0f : (value - min) / (max - min); }

    public void set(float v) {
        v = Math.max(min, Math.min(max, v));
        if (step > 0) v = min + Math.round((v - min) / step) * step;
        v = Math.max(min, Math.min(max, v));
        if (v != value) { value = v; ConfigManager.markDirty(); }
        anim.set(fraction());
    }

    public void setFraction(float f) { set(min + Math.max(0f, Math.min(1f, f)) * (max - min)); }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) {
        value = Math.max(min, Math.min(max, e.getAsFloat()));
        anim.snap(fraction());
    }

    @Override public void reset() {
        value = def;
        anim.set(fraction());
    }
}

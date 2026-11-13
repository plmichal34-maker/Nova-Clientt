package dev.novaclient.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** Kolor RGB (bez alpha). */
public final class ColorSetting extends Setting {
    private int value;
    private final int def;

    public ColorSetting(String id, String name, String description, int def) {
        super(id, name, description);
        this.def = def & 0xFFFFFF;
        this.value = this.def;
    }

    /** Zwraca 0xRRGGBB. */
    public int get() { return value; }

    /** Zwraca 0xFFRRGGBB. */
    public int argb() { return 0xFF000000 | value; }

    public void set(int rgb) {
        rgb &= 0xFFFFFF;
        if (rgb != value) { value = rgb; ConfigManager.markDirty(); }
    }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) { value = e.getAsInt() & 0xFFFFFF; }

    @Override public void reset() { value = def; }
}

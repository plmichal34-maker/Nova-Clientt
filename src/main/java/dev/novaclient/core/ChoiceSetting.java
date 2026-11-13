package dev.novaclient.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** Wybor jednej z kilku opcji. */
public final class ChoiceSetting extends Setting {
    public final String[] options;
    private int value;
    private final int def;

    public ChoiceSetting(String id, String name, String description, int def, String... options) {
        super(id, name, description);
        this.options = options;
        this.def = def;
        this.value = def;
    }

    public int get() { return value; }

    public String label() { return options[value]; }

    public void set(int i) {
        i = ((i % options.length) + options.length) % options.length;
        if (i != value) { value = i; ConfigManager.markDirty(); }
    }

    public void cycle(int dir) { set(value + dir); }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) { int i = e.getAsInt(); value = (i >= 0 && i < options.length) ? i : def; }

    @Override public void reset() { value = def; }
}

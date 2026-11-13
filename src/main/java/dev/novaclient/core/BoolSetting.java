package dev.novaclient.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;

public final class BoolSetting extends Setting {
    private boolean value;
    private final boolean def;
    public final Anim anim;

    public BoolSetting(String id, String name, String description, boolean def) {
        super(id, name, description);
        this.def = def;
        this.value = def;
        this.anim = new Anim(AnimType.TOGGLE, 16f, def ? 1f : 0f);
    }

    public boolean get() { return value; }

    public void set(boolean v) {
        if (value != v) { value = v; ConfigManager.markDirty(); }
        anim.set(v ? 1f : 0f);
    }

    public void toggle() { set(!value); }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }

    @Override public void fromJson(JsonElement e) {
        value = e.getAsBoolean();
        anim.snap(value ? 1f : 0f);
    }

    @Override public void reset() {
        value = def;
        anim.set(def ? 1f : 0f);
    }
}

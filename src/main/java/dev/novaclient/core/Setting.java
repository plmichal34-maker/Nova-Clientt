package dev.novaclient.core;

import com.google.gson.JsonElement;

/** Bazowa klasa ustawienia modulu. Podklasy trzymaja wartosci w polach prymitywnych (bez alokacji w renderze). */
public abstract class Setting {
    public final String id;
    public final String name;
    public final String description;

    protected Setting(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public abstract JsonElement toJson();

    public abstract void fromJson(JsonElement e);

    public abstract void reset();
}

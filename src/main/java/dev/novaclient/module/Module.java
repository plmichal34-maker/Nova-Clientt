package dev.novaclient.module;

import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;
import dev.novaclient.core.ConfigManager;
import dev.novaclient.core.Setting;

import java.util.ArrayList;
import java.util.List;

/**
 * Bazowa klasa funkcji clienta: ON/OFF, ustawienia, skrot klawiszowy, opis.
 * Nowa funkcje dodaje sie przez rozszerzenie tej klasy i rejestracje w {@link ModuleManager}.
 */
public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    public final Category category;
    public final boolean alwaysOn;
    public final List<Setting> settings = new ArrayList<>();

    private final boolean defaultEnabled;
    private boolean enabled;
    /** Kod klawisza GLFW lub -1 gdy brak. */
    public int key = -1;
    public boolean keyDown;

    // Stan uzywany przez GUI.
    public final Anim hoverAnim = new Anim(AnimType.HOVER, 14f, 0f);
    public final Anim toggleAnim;
    public final Anim expandAnim = new Anim(AnimType.TRANSITION, 14f, 0f);
    public boolean expanded;

    protected Module(String id, String name, String description, Category category, boolean defaultEnabled, boolean alwaysOn) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultEnabled = defaultEnabled;
        this.alwaysOn = alwaysOn;
        this.enabled = defaultEnabled;
        this.toggleAnim = new Anim(AnimType.TOGGLE, 16f, (defaultEnabled || alwaysOn) ? 1f : 0f);
    }

    protected <S extends Setting> S add(S s) {
        settings.add(s);
        return s;
    }

    public boolean isEnabled() { return alwaysOn || enabled; }

    public void setEnabled(boolean e) {
        if (alwaysOn) return;
        if (enabled != e) { enabled = e; ConfigManager.markDirty(); }
        toggleAnim.set(e ? 1f : 0f);
    }

    public void toggle() { setEnabled(!enabled); }

    /** Wczytanie stanu z pliku (bez znaczenia dla animacji). */
    public void loadEnabled(boolean e) {
        enabled = e;
        toggleAnim.snap(isEnabled() ? 1f : 0f);
    }

    public void resetToDefaults() {
        enabled = defaultEnabled;
        toggleAnim.set(isEnabled() ? 1f : 0f);
        key = -1;
        for (Setting s : settings) s.reset();
    }

    /** Wywolywane co tick klienta, gdy modul jest wlaczony. */
    public void onTick() {}
}

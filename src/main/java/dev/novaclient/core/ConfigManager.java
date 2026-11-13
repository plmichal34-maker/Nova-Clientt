package dev.novaclient.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.novaclient.module.Module;
import dev.novaclient.module.ModuleManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/** Zapis i odczyt konfiguracji oraz profili (config/novaclient/profiles/*.json). */
public final class ConfigManager {
    public static final String DEFAULT_PROFILE = "default";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path dir;
    private static Path profilesDir;
    private static String active = DEFAULT_PROFILE;
    private static boolean dirty;
    private static long lastChange;
    private static boolean loading;

    private ConfigManager() {}

    public static void init() {
        dir = FabricLoader.getInstance().getConfigDir().resolve("novaclient");
        profilesDir = dir.resolve("profiles");
        try {
            Files.createDirectories(profilesDir);
        } catch (IOException e) {
            NovaLog.error("Nie mozna utworzyc katalogu konfiguracji", e);
        }
        Path meta = dir.resolve("meta.json");
        if (Files.exists(meta)) {
            try {
                JsonObject o = GSON.fromJson(Files.readString(meta), JsonObject.class);
                if (o != null && o.has("active")) active = sanitize(o.get("active").getAsString());
            } catch (Exception e) {
                NovaLog.error("Uszkodzony meta.json", e);
            }
        }
        if (!Files.exists(profilePath(active))) {
            active = DEFAULT_PROFILE;
        }
        if (Files.exists(profilePath(active))) {
            load(active);
        } else {
            saveNow();
        }
    }

    public static String active() { return active; }

    public static void markDirty() {
        if (loading) return;
        dirty = true;
        lastChange = System.currentTimeMillis();
    }

    /** Wywolywane co tick - zapisuje zmiany z lekkim opoznieniem. */
    public static void tick() {
        if (dirty && System.currentTimeMillis() - lastChange > 800) saveNow();
    }

    private static Path profilePath(String name) { return profilesDir.resolve(name + ".json"); }

    private static String sanitize(String name) {
        String s = name.replaceAll("[^a-zA-Z0-9_\\- ]", "").trim();
        return s.isEmpty() ? DEFAULT_PROFILE : s;
    }

    public static void saveNow() {
        if (profilesDir == null) return;
        dirty = false;
        try {
            Files.createDirectories(profilesDir);
            Files.writeString(profilePath(active), GSON.toJson(serialize()));
            JsonObject meta = new JsonObject();
            meta.addProperty("active", active);
            Files.writeString(dir.resolve("meta.json"), GSON.toJson(meta));
        } catch (IOException e) {
            NovaLog.error("Nie mozna zapisac konfiguracji", e);
        }
    }

    private static JsonObject serialize() {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        JsonObject modules = new JsonObject();
        for (Module m : ModuleManager.all()) {
            JsonObject mo = new JsonObject();
            mo.addProperty("enabled", m.isEnabled());
            mo.addProperty("key", m.key);
            JsonObject so = new JsonObject();
            for (Setting s : m.settings) so.add(s.id, s.toJson());
            mo.add("settings", so);
            modules.add(m.id, mo);
        }
        root.add("modules", modules);
        return root;
    }

    private static void apply(JsonObject root) {
        loading = true;
        try {
            for (Module m : ModuleManager.all()) m.resetToDefaults();
            if (root == null || !root.has("modules")) return;
            JsonObject modules = root.getAsJsonObject("modules");
            for (Module m : ModuleManager.all()) {
                if (!modules.has(m.id)) continue;
                try {
                    JsonObject mo = modules.getAsJsonObject(m.id);
                    if (mo.has("enabled")) m.loadEnabled(mo.get("enabled").getAsBoolean());
                    if (mo.has("key")) m.key = mo.get("key").getAsInt();
                    if (mo.has("settings")) {
                        JsonObject so = mo.getAsJsonObject("settings");
                        for (Setting s : m.settings) {
                            JsonElement e = so.get(s.id);
                            if (e != null) {
                                try { s.fromJson(e); } catch (Exception ex) { s.reset(); }
                            }
                        }
                    }
                } catch (Exception ex) {
                    NovaLog.error("Blad wczytywania modulu " + m.id, ex);
                }
            }
        } finally {
            loading = false;
        }
    }

    public static boolean load(String name) {
        name = sanitize(name);
        Path p = profilePath(name);
        if (!Files.exists(p)) return false;
        try {
            JsonObject root = GSON.fromJson(Files.readString(p), JsonObject.class);
            apply(root);
            active = name;
            saveMeta();
            return true;
        } catch (Exception e) {
            NovaLog.error("Nie mozna wczytac profilu " + name, e);
            return false;
        }
    }

    private static void saveMeta() {
        try {
            JsonObject meta = new JsonObject();
            meta.addProperty("active", active);
            Files.writeString(dir.resolve("meta.json"), GSON.toJson(meta));
        } catch (IOException e) {
            NovaLog.error("Nie mozna zapisac meta.json", e);
        }
    }

    public static List<String> listProfiles() {
        List<String> out = new ArrayList<>();
        if (profilesDir != null && Files.isDirectory(profilesDir)) {
            try (Stream<Path> s = Files.list(profilesDir)) {
                s.map(p -> p.getFileName().toString())
                        .filter(n -> n.endsWith(".json"))
                        .map(n -> n.substring(0, n.length() - 5))
                        .forEach(out::add);
            } catch (IOException e) {
                NovaLog.error("Nie mozna wylistowac profili", e);
            }
        }
        if (!out.contains(DEFAULT_PROFILE)) out.add(DEFAULT_PROFILE);
        Collections.sort(out);
        return out;
    }

    /** Tworzy nowy profil z kopia aktualnych ustawien i przelacza na niego. Zwraca jego nazwe. */
    public static String createProfile() {
        saveNow();
        int n = 1;
        String name;
        do { name = "Profil " + n++; } while (Files.exists(profilePath(name)));
        active = name;
        saveNow();
        return name;
    }

    public static boolean deleteActive() {
        if (DEFAULT_PROFILE.equals(active)) return false;
        try {
            Files.deleteIfExists(profilePath(active));
        } catch (IOException e) {
            NovaLog.error("Nie mozna usunac profilu", e);
            return false;
        }
        if (!load(DEFAULT_PROFILE)) {
            active = DEFAULT_PROFILE;
            apply(null);
            saveNow();
        }
        return true;
    }

    /** Przywraca wszystkie ustawienia domyslne w aktywnym profilu. */
    public static void resetAll() {
        loading = true;
        try {
            for (Module m : ModuleManager.all()) m.resetToDefaults();
        } finally {
            loading = false;
        }
        saveNow();
    }
}

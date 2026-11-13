package dev.novaclient.modules;

import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;
import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ChoiceSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

/** Wlasny celownik zastepujacy wanilliowy (tylko wyglad). */
public final class CrosshairModule extends Module {
    public final ChoiceSetting style = add(new ChoiceSetting("style", "Styl", "Ksztalt celownika.", 0, "Krzyz", "Kropka", "Okrag"));
    public final ColorSetting color = add(new ColorSetting("color", "Kolor", "Kolor celownika.", 0xFFFFFF));
    public final FloatSetting opacity = add(new FloatSetting("opacity", "Przezroczystosc", "Krycie celownika.", 1f, 0.1f, 1f, 0.05f));
    public final FloatSetting size = add(new FloatSetting("size", "Rozmiar", "Dlugosc ramion / promien.", 5f, 1f, 12f, 1f, " px"));
    public final FloatSetting gap = add(new FloatSetting("gap", "Przerwa", "Odstep ramion od srodka.", 2f, 0f, 10f, 1f, " px"));
    public final FloatSetting thickness = add(new FloatSetting("thickness", "Grubosc", "Grubosc linii.", 1f, 1f, 4f, 1f, " px"));
    public final BoolSetting outline = add(new BoolSetting("outline", "Czarny kontur", "Dodaje czarny kontur dla lepszej widocznosci.", true));
    public final BoolSetting dynamic = add(new BoolSetting("dynamic", "Dynamiczny", "Celownik rozszerza sie podczas ruchu (animacja HUD).", false));

    private final Anim spread = new Anim(AnimType.HUD, 12f, 0f);

    public CrosshairModule() {
        super("crosshair", "Celownik", "Wlasny wyglad celownika zamiast domyslnego.", Category.VISUALS, false, false);
    }

    public static boolean shouldReplace() {
        CrosshairModule m = ModuleManager.CROSSHAIR;
        if (m == null || !m.isEnabled()) return false;
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.options.getPerspective().isFirstPerson();
    }

    public void render(DrawContext ctx, MinecraftClient mc) {
        if (!mc.options.getPerspective().isFirstPerson()) return;
        float target = 0f;
        if (dynamic.get()) {
            Vec3d v = mc.player.getVelocity();
            target = Math.min(1f, (float) Math.sqrt(v.x * v.x + v.z * v.z) * 5f);
        }
        spread.set(target);
        float sp = spread.update() * 4f;
        int cx = ctx.getScaledWindowWidth() / 2, cy = ctx.getScaledWindowHeight() / 2;
        int th = thickness.getInt(), sz = size.getInt(), gp = gap.getInt() + Math.round(sp);
        int col = Colors.withAlpha(color.get(), opacity.get());
        int black = Colors.withAlpha(0x000000, opacity.get() * 0.85f);
        switch (style.get()) {
            case 1: {
                int d = Math.max(1, th);
                if (outline.get()) RenderUtil.rect(ctx, cx - d / 2 - 1, cy - d / 2 - 1, cx - d / 2 + d + 1, cy - d / 2 + d + 1, black);
                RenderUtil.rect(ctx, cx - d / 2, cy - d / 2, cx - d / 2 + d, cy - d / 2 + d, col);
                break;
            }
            case 2: {
                int rad = Math.min(16, sz + gp);
                if (outline.get()) RenderUtil.roundedOutline(ctx, cx - rad - 1, cy - rad - 1, cx + rad + 1, cy + rad + 1, rad + 1, th + 2, black);
                RenderUtil.roundedOutline(ctx, cx - rad, cy - rad, cx + rad, cy + rad, rad, th, col);
                break;
            }
            default: {
                if (outline.get()) cross(ctx, cx, cy, th + 2, sz + 1, gp - 1, black);
                cross(ctx, cx, cy, th, sz, gp, col);
            }
        }
    }

    private static void cross(DrawContext c, int cx, int cy, int th, int sz, int gp, int col) {
        int left = cx - th / 2, top = cy - th / 2;
        RenderUtil.rect(c, left, cy - gp - sz, left + th, cy - gp, col);
        RenderUtil.rect(c, left, cy + gp, left + th, cy + gp + sz, col);
        RenderUtil.rect(c, cx - gp - sz, top, cx - gp, top + th, col);
        RenderUtil.rect(c, cx + gp, top, cx + gp + sz, top + th, col);
    }
}

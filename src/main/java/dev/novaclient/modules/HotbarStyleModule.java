package dev.novaclient.modules;

import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;
import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import dev.novaclient.util.Time;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Wyglad paska szybkiego dostepu i zaznaczonego slotu. */
public final class HotbarStyleModule extends Module {
    public final ColorSetting color = add(new ColorSetting("color", "Kolor zaznaczenia", "Kolor ramki zaznaczonego slotu.", 0x8B5CF6));
    public final BoolSetting rainbow = add(new BoolSetting("rainbow", "Teczowa ramka", "Ramka zmienia kolory w czasie.", false));
    public final FloatSetting thickness = add(new FloatSetting("thickness", "Grubosc ramki", "Grubosc ramki zaznaczenia.", 2f, 1f, 3f, 1f, " px"));
    public final FloatSetting radius = add(new FloatSetting("radius", "Zaokraglenie", "Promien zaokraglenia ramki.", 4f, 0f, 8f, 1f, " px"));
    public final BoolSetting fill = add(new BoolSetting("fill", "Wypelnienie", "Delikatne wypelnienie zaznaczonego slotu.", true));
    public final FloatSetting fillAlpha = add(new FloatSetting("fill_alpha", "Krycie wypelnienia", "Przezroczystosc wypelnienia.", 0.18f, 0.02f, 0.6f, 0.02f));
    public final BoolSetting slide = add(new BoolSetting("slide", "Plynne przesuwanie", "Zaznaczenie plynnie przesuwa sie miedzy slotami (animacja HUD).", true));
    public final BoolSetting outlineAll = add(new BoolSetting("outline_all", "Kontury wszystkich slotow", "Rysuje delikatne zaokraglone kontury wszystkich slotow.", true));
    public final FloatSetting outlineAlpha = add(new FloatSetting("outline_alpha", "Krycie konturow", "Przezroczystosc konturow slotow.", 0.25f, 0.05f, 0.8f, 0.05f));

    private final Anim selX = new Anim(AnimType.HUD, 20f, 0f);

    public HotbarStyleModule() {
        super("hotbar_style", "Pasek szybkiego dostepu", "Personalizacja paska szybkiego dostepu i zaznaczonego slotu.",
                Category.INVENTORY, true, false);
    }

    public void render(DrawContext ctx, MinecraftClient mc) {
        int sel = mc.player.getInventory().selectedSlot;
        if (!slide.get()) selX.snap(sel); else selX.set(sel);
        float pos = selX.update();
        int w = ctx.getScaledWindowWidth(), h = ctx.getScaledWindowHeight();
        int baseX = w / 2 - 88, y0 = h - 19;
        int r = radius.getInt();
        if (outlineAll.get()) {
            int oc = Colors.withAlpha(0xFFFFFF, outlineAlpha.get());
            for (int i = 0; i < 9; i++) {
                RenderUtil.roundedOutline(ctx, baseX + i * 20 - 2, y0 - 2, baseX + i * 20 + 18, y0 + 18, Math.min(r, 5), 1, oc);
            }
        }
        int sx = baseX - 3 + Math.round(pos * 20f);
        int col = rainbow.get() ? Colors.hsv(Time.seconds() * 0.2f, 0.8f, 1f) : color.argb();
        if (fill.get()) RenderUtil.roundedRect(ctx, sx, y0 - 3, sx + 22, y0 + 19, r, Colors.withAlpha(col, fillAlpha.get()));
        RenderUtil.roundedOutline(ctx, sx, y0 - 3, sx + 22, y0 + 19, r, thickness.getInt(), col | 0xFF000000);
    }
}

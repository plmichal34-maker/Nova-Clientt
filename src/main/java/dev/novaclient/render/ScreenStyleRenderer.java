package dev.novaclient.render;

import dev.novaclient.anim.AnimType;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.modules.InterfaceModule;
import dev.novaclient.modules.MenuStyleModule;
import dev.novaclient.modules.ScreenStyleModule;
import dev.novaclient.mixin.HandledScreenAccessor;
import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import dev.novaclient.util.Time;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

/** Stylizacja okien ekwipunku, skrzyn, menu glownego i ekranu pauzy (przez zdarzenia Fabric API). */
public final class ScreenStyleRenderer {
    private static final float[] HOVER = new float[256];

    private ScreenStyleRenderer() {}

    public static void onScreenInit() {
        java.util.Arrays.fill(HOVER, 0f);
    }

    private static boolean isChest(Screen s) {
        return s instanceof GenericContainerScreen || s instanceof ShulkerBoxScreen;
    }

    /** Przed rysowaniem ekranu: warstwa tla. */
    public static void before(Screen screen, DrawContext ctx) {
        if (!(screen instanceof HandledScreen<?>)) return;
        ScreenStyleModule m = isChest(screen) ? ModuleManager.CHEST_STYLE : ModuleManager.INVENTORY_STYLE;
        if (!m.isEnabled() || m.tintAlpha.get() <= 0f) return;
        ctx.fill(0, 0, screen.width, screen.height, Colors.withAlpha(m.tintColor.get(), m.tintAlpha.get()));
    }

    /** Po narysowaniu ekranu (przed tooltipami). */
    public static void after(Screen screen, DrawContext ctx, int mx, int my) {
        if (screen instanceof HandledScreen<?> hs) {
            afterHandled(hs, ctx, mx, my);
        } else if (screen instanceof TitleScreen || screen instanceof GameMenuScreen) {
            afterMenu(screen, ctx);
        }
    }

    private static void afterHandled(HandledScreen<?> hs, DrawContext ctx, int mx, int my) {
        boolean chest = isChest(hs);
        ScreenStyleModule m = chest ? ModuleManager.CHEST_STYLE : ModuleManager.INVENTORY_STYLE;
        boolean style = m.isEnabled();
        boolean fx = Effects.active();
        if (!style && !fx) return;
        HandledScreenAccessor acc = (HandledScreenAccessor) hs;
        int gx = acc.novaGetX(), gy = acc.novaGetY();
        float t = Time.seconds();
        float dt = Time.frameDelta();
        ItemTarget target = chest ? ItemTarget.CHEST : ItemTarget.INVENTORY;

        if (style && m.panelAlpha.get() > 0f) {
            RenderUtil.roundedRect(ctx, gx, gy, gx + acc.novaGetWidth(), gy + acc.novaGetHeight(), 4,
                    Colors.withAlpha(m.panelColor.get(), m.panelAlpha.get()));
        }

        List<Slot> slots = hs.getScreenHandler().slots;
        boolean animHover = InterfaceModule.animEnabled(AnimType.INVENTORY);
        float k = 1f - (float) Math.exp(-18f * InterfaceModule.animSpeed() * dt);
        int radius = m.slotRadius.getInt();
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (!slot.isEnabled()) continue;
            int sx = gx + slot.x, sy = gy + slot.y;
            if (style) {
                if (m.roundedSlots.get() && m.slotAlpha.get() > 0f) {
                    RenderUtil.roundedRect(ctx, sx - 1, sy - 1, sx + 17, sy + 17, radius,
                            Colors.withAlpha(m.slotColor.get(), m.slotAlpha.get()));
                }
                int mode = m.borderMode.get();
                if (mode != 0) {
                    int col;
                    if (mode == 1) col = m.borderColor.argb();
                    else if (mode == 2) col = Colors.hsv(t * 0.2f + i * 0.03f, 0.8f, 1f);
                    else col = Colors.lerp(m.borderColor.argb(), m.borderColor2.argb(), 0.5f + 0.5f * (float) Math.sin(t * 1.5f + i * 0.25f));
                    RenderUtil.roundedOutline(ctx, sx - 1, sy - 1, sx + 17, sy + 17, radius, m.borderWidth.getInt(), col | 0xFF000000);
                }
                if (i < HOVER.length) {
                    boolean over = RenderUtil.inside(mx, my, sx, sy, 16, 16);
                    float tg = over ? 1f : 0f;
                    HOVER[i] = animHover ? HOVER[i] + (tg - HOVER[i]) * k : tg;
                    if (HOVER[i] > 0.01f && m.hoverAlpha.get() > 0f) {
                        RenderUtil.roundedRect(ctx, sx - 1, sy - 1, sx + 17, sy + 17, radius,
                                Colors.withAlpha(m.hoverColor.get(), m.hoverAlpha.get() * HOVER[i]));
                    }
                }
            }
            if (fx) {
                ItemStack st = slot.getStack();
                if (!st.isEmpty()) Effects.render(ctx, sx, sy, st, target);
            }
        }
    }

    private static void afterMenu(Screen screen, DrawContext ctx) {
        MenuStyleModule m = ModuleManager.MENU_STYLE;
        if (!m.isEnabled()) return;
        if (screen instanceof TitleScreen ? !m.onTitle.get() : !m.onPause.get()) return;
        int w = screen.width, h = screen.height;
        float mix = m.animated.get() ? 0.5f + 0.5f * (float) Math.sin(Time.seconds() * m.speed.get()) : 0f;
        int c1 = Colors.lerp(m.color1.argb(), m.color2.argb(), mix);
        int c2 = Colors.lerp(m.color2.argb(), m.color1.argb(), mix);
        float a = m.alpha.get();
        if (a > 0f) ctx.fillGradient(0, 0, w, h, Colors.mulAlpha(c1, a), Colors.mulAlpha(c2, a));
        float v = m.vignette.get();
        if (v > 0f) {
            ctx.fillGradient(0, 0, w, h / 3, Colors.withAlpha(0x000000, v), 0x00000000);
            ctx.fillGradient(0, h - h / 3, w, h, 0x00000000, Colors.withAlpha(0x000000, v));
        }
    }
}

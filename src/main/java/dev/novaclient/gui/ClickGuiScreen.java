package dev.novaclient.gui;

import dev.novaclient.anim.Anim;
import dev.novaclient.anim.AnimType;
import dev.novaclient.core.BoolSetting;
import dev.novaclient.core.ChoiceSetting;
import dev.novaclient.core.ColorSetting;
import dev.novaclient.core.ConfigManager;
import dev.novaclient.core.FloatSetting;
import dev.novaclient.core.NovaClient;
import dev.novaclient.core.Setting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;
import dev.novaclient.module.ModuleManager;
import dev.novaclient.modules.InterfaceModule;
import dev.novaclient.util.Colors;
import dev.novaclient.util.RenderUtil;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

/**
 * Glowne okno clienta: zakladki, wyszukiwarka, karty funkcji z ustawieniami, profile.
 * Wspolrzedne "GUI space" = wspolrzedne ekranu przed skalowaniem wokol srodka.
 */
public final class ClickGuiScreen extends Screen {
    private static final int W = 580, H = 380, SIDE = 132, PAD = 16;
    private static final int CARD_H = 40, GAP = 8;
    private static final int RENDER = 0, CLICK = 1;

    private static final int TXT = 0xFFEDEDF5, SUB = 0xFF9696AC, DARK = 0xFF0E0E18;
    private static final int[] SWATCHES = {0xFFFFFF, 0xF87171, 0xFB923C, 0xFACC15, 0x4ADE80,
            0x2DD4BF, 0x38BDF8, 0x818CF8, 0xC084FC, 0xF472B6};

    private final Anim open = new Anim(AnimType.GUI_OPEN, 12f, 0f);
    private final Anim closeA = new Anim(AnimType.GUI_CLOSE, 14f, 0f);
    private final Anim tabAnim = new Anim(AnimType.TRANSITION, 12f, 1f);
    private final Anim tabInd = new Anim(AnimType.TRANSITION, 16f, 0f);
    private final Anim scroll = new Anim(AnimType.TRANSITION, 16f, 0f);
    private final Anim[] tabHover = new Anim[Category.values().length];

    private Category tab = Category.ITEMS;
    private String search = "";
    private List<Module> list;
    private List<String> profiles;
    private boolean closing;
    private Module listening;
    private float scrollTarget;
    private int contentH;

    // Przeciaganie suwaka / paska odcienia
    private FloatSetting dragF;
    private int dragFx, dragFw;
    private ColorSetting dragC;
    private int dragCx, dragCw;

    // Stan klatki
    private float fade = 1f, cardA = 1f, scale = 1f, cx, cy;
    private int px, py, gmx, gmy;
    private boolean consumed;

    public ClickGuiScreen() {
        super(Text.literal("Nova Client"));
        open.set(1f);
        for (int i = 0; i < tabHover.length; i++) tabHover[i] = new Anim(AnimType.HOVER, 14f, 0f);
        tabInd.snap(tab.ordinal());
        rebuild();
        refreshProfiles();
    }

    // ---------------------------------------------------------------- helpers

    private TextRenderer tr() { return client.textRenderer; }

    private int c(int argb) { return Colors.mulAlpha(argb, fade * cardA); }

    private void fl(DrawContext g, int x1, int y1, int x2, int y2, int col) { RenderUtil.rect(g, x1, y1, x2, y2, c(col)); }

    private void rr(DrawContext g, int x1, int y1, int x2, int y2, int r, int col) { RenderUtil.roundedRect(g, x1, y1, x2, y2, r, c(col)); }

    private void ro(DrawContext g, int x1, int y1, int x2, int y2, int r, int th, int col) { RenderUtil.roundedOutline(g, x1, y1, x2, y2, r, th, c(col)); }

    private void tx(DrawContext g, String s, int x, int y, int col) {
        int cc = c(col);
        if (Colors.alpha(cc) < 6) return;
        g.drawText(tr(), s, x, y, cc, false);
    }

    private void txc(DrawContext g, String s, int cxx, int y, int col) { tx(g, s, cxx - tr().getWidth(s) / 2, y, col); }

    private void txr(DrawContext g, String s, int right, int y, int col) { tx(g, s, right - tr().getWidth(s), y, col); }

    private String trim(String s, int w) { return tr().getWidth(s) <= w ? s : tr().trimToWidth(s, Math.max(0, w - 6)) + "..."; }

    private boolean in(int x, int y, int w, int h) { return RenderUtil.inside(gmx, gmy, x, y, w, h); }

    private void scissor(DrawContext g, int x1, int y1, int x2, int y2) {
        int a = (int) Math.floor((x1 - cx) * scale + cx), b = (int) Math.floor((y1 - cy) * scale + cy);
        int d = (int) Math.ceil((x2 - cx) * scale + cx), e = (int) Math.ceil((y2 - cy) * scale + cy);
        g.enableScissor(a, b, d, e);
    }

    private void computeFrame() {
        float base = Math.min(InterfaceModule.guiScale(), Math.min((width - 16f) / W, (height - 16f) / H));
        scale = Math.max(0.3f, base * (0.94f + 0.06f * open.value) * (1f - 0.04f * closeA.value));
        cx = width / 2f;
        cy = height / 2f;
        px = Math.round(cx - W / 2f);
        py = Math.round(cy - H / 2f);
    }

    private void mouseToGui(double mx, double my) {
        gmx = (int) ((mx - cx) / scale + cx);
        gmy = (int) ((my - cy) / scale + cy);
    }

    private void rebuild() {
        list = search.isEmpty() ? ModuleManager.byCategory(tab) : ModuleManager.search(search);
        tabAnim.snap(0f);
        tabAnim.set(1f);
        scroll.snap(0f);
        scrollTarget = 0f;
    }

    private void refreshProfiles() { profiles = ConfigManager.listProfiles(); }

    private boolean showProfiles() { return tab == Category.SETTINGS && search.isEmpty(); }

    @Override public boolean shouldPause() { return false; }

    @Override public void close() {
        if (closing) return;
        closing = true;
        closeA.set(1f);
    }

    @Override public void removed() { ConfigManager.saveNow(); }

    // ---------------------------------------------------------------- render

    @Override
    public void render(DrawContext g, int mouseX, int mouseY, float delta) {
        open.update();
        closeA.update();
        if (closing && closeA.value >= 0.999f) {
            client.setScreen(null);
            return;
        }
        fade = Colors.clamp01(open.value * (1f - closeA.value));
        cardA = 1f;
        computeFrame();
        mouseToGui(mouseX, mouseY);

        if (InterfaceModule.blur()) renderBackground(g, mouseX, mouseY, delta);
        else g.fill(0, 0, width, height, Colors.withAlpha(0x000000, 0.45f * fade));

        MatrixStack ms = g.getMatrices();
        ms.push();
        ms.translate(cx, cy, 0f);
        ms.scale(scale, scale, 1f);
        ms.translate(-cx, -cy, 0f);

        int acc = InterfaceModule.accent();
        float op = InterfaceModule.guiOpacity();

        // Panel "glass"
        rr(g, px, py, px + W, py + H, 12, Colors.withAlpha(0x0E0E18, op));
        rr(g, px, py, px + SIDE, py + H, 12, Colors.withAlpha(0xFFFFFF, 0.045f));
        ro(g, px, py, px + W, py + H, 12, 1, Colors.withAlpha(0xFFFFFF, 0.13f));
        fl(g, px + 14, py, px + W - 14, py + 1, Colors.withAlpha(acc, 0.75f));

        drawSidebar(g, acc);
        drawTopBar(g, acc);
        pass(g, RENDER, 0);

        ms.pop();
    }

    private void drawSidebar(DrawContext g, int acc) {
        MatrixStack ms = g.getMatrices();
        ms.push();
        ms.translate(px + 18, py + 16, 0f);
        ms.scale(1.6f, 1.6f, 1f);
        tx(g, "NOVA", 0, 0, acc);
        ms.pop();
        tx(g, "CLIENT", px + 19, py + 33, SUB);

        Category[] cats = Category.values();
        tabInd.set(search.isEmpty() ? tab.ordinal() : -1f);
        float iy = tabInd.update();
        int base = py + 60;
        if (iy >= -0.5f) {
            int yy = base + Math.round(iy * 34f);
            rr(g, px + 10, yy, px + SIDE - 10, yy + 28, 8, Colors.withAlpha(acc, 0.18f));
            rr(g, px + 10, yy + 6, px + 13, yy + 22, 1, acc);
        }
        for (int i = 0; i < cats.length; i++) {
            int ty = base + i * 34;
            boolean hov = in(px + 10, ty, SIDE - 20, 28);
            tabHover[i].set(hov ? 1f : 0f);
            float h = tabHover[i].update();
            if (h > 0.01f) rr(g, px + 10, ty, px + SIDE - 10, ty + 28, 8, Colors.withAlpha(0xFFFFFF, 0.06f * h));
            boolean active = search.isEmpty() && cats[i] == tab;
            int col = active ? acc : Colors.lerp(SUB, TXT, h);
            Icons.draw(g, i, px + 20, ty + 6, c(col));
            tx(g, cats[i].label, px + 44, ty + 10, col);
        }
        tx(g, trim("Profil: " + ConfigManager.active(), SIDE - 28), px + 16, py + H - 34, SUB);
        tx(g, "Zamknij: ESC", px + 16, py + H - 22, 0xFF6E6E86);
    }

    private void drawTopBar(DrawContext g, int acc) {
        int sx = px + SIDE + PAD, sw = W - SIDE - PAD * 2 - 26;
        rr(g, sx, py + 14, sx + sw, py + 38, 8, Colors.withAlpha(0xFFFFFF, 0.06f));
        ro(g, sx, py + 14, sx + sw, py + 38, 8, 1, Colors.withAlpha(search.isEmpty() ? 0xFFFFFF : acc, search.isEmpty() ? 0.08f : 0.6f));
        Icons.draw(g, Icons.SEARCH, sx + 8, py + 18, c(SUB));
        boolean caret = (System.currentTimeMillis() / 500) % 2 == 0;
        if (search.isEmpty()) tx(g, "Szukaj funkcji i ustawien... (wpisz tekst)", sx + 30, py + 22, 0xFF6E6E86);
        else tx(g, trim(search, sw - 44) + (caret ? "|" : ""), sx + 30, py + 22, TXT);
        boolean hc = in(px + W - 30, py + 18, 16, 16);
        Icons.draw(g, Icons.CLOSE, px + W - 30, py + 18, c(hc ? 0xFFF87171 : SUB));
    }

    // ---------------------------------------------------------------- content

    private void pass(DrawContext g, int mode, int button) {
        int lx = px + SIDE + PAD, lw = W - SIDE - PAD * 2;
        int top = py + 50, bottom = py + H - 12;
        boolean render = mode == RENDER;
        if (mode == CLICK && !(gmy >= top && gmy < bottom && gmx >= lx && gmx < lx + lw)) return;
        if (render) {
            scroll.set(scrollTarget);
            scroll.update();
            scissor(g, lx - 6, top, lx + lw + 8, bottom);
        }
        float tv = tabAnim.update();
        boolean appearOn = InterfaceModule.animEnabled(AnimType.APPEAR);
        int n = list.size() + (showProfiles() ? 1 : 0);
        float y = top - scroll.value;
        int idx = 0;
        consumed = false;

        if (showProfiles()) {
            cardA = stagger(tv, idx, n, appearOn);
            int yy = (int) y + Math.round((1f - cardA) * 14f);
            int h = profileCard(g, lx, yy, lw, top, bottom, mode, button);
            y += h + GAP;
            idx++;
        }
        for (int i = 0; i < list.size() && !consumed; i++) {
            Module m = list.get(i);
            cardA = stagger(tv, idx, n, appearOn);
            int yy = (int) y + Math.round((1f - cardA) * 14f);
            int h = moduleCard(g, m, lx, yy, lw, top, bottom, mode, button);
            y += h + GAP;
            idx++;
        }
        cardA = 1f;
        if (render) {
            g.disableScissor();
            contentH = (int) (y - (top - scroll.value));
            int viewH = bottom - top;
            if (contentH > viewH) {
                float frac = viewH / (float) contentH;
                int bh = Math.max(20, (int) (viewH * frac));
                int by = top + (int) ((viewH - bh) * (scroll.value / Math.max(1f, contentH - viewH)));
                rr(g, lx + lw + 4, by, lx + lw + 7, by + bh, 1, Colors.withAlpha(0xFFFFFF, 0.25f));
            }
            float maxScroll = Math.max(0, contentH - viewH);
            if (scrollTarget > maxScroll) scrollTarget = maxScroll;
        }
    }

    private static float stagger(float tv, int idx, int n, boolean appearOn) {
        if (!appearOn) return tv;
        return Colors.clamp01(tv * (1f + 0.25f * Math.min(n, 8)) - idx * 0.25f);
    }

    // ---- profile card

    private int chipRows(int lw) {
        int rows = 1, xx = 0, avail = lw - 24;
        for (String p : profiles) {
            int w = tr().getWidth(p) + 18;
            if (xx + w > avail && xx > 0) { rows++; xx = 0; }
            xx += w + 6;
        }
        return rows;
    }

    private int profileCard(DrawContext g, int lx, int y0, int lw, int top, int bottom, int mode, int button) {
        int rows = chipRows(lw);
        int h = 34 + rows * 24 + 34;
        int acc = InterfaceModule.accent();
        boolean render = mode == RENDER;
        if (render) {
            rr(g, lx, y0, lx + lw, y0 + h, 10, Colors.withAlpha(0xFFFFFF, 0.05f));
            ro(g, lx, y0, lx + lw, y0 + h, 10, 1, Colors.withAlpha(0xFFFFFF, 0.07f));
            tx(g, "Profile ustawien", lx + 14, y0 + 9, TXT);
            tx(g, "Kliknij profil, aby go wczytac. Zmiany zapisuja sie automatycznie w aktywnym profilu.", lx + 14, y0 + 21, SUB);
        }
        int xx = lx + 12, yy = y0 + 34, avail = lw - 24, startX = lx + 12;
        for (String p : profiles) {
            int w = tr().getWidth(p) + 18;
            if (xx - startX + w > avail && xx > startX) { xx = startX; yy += 24; }
            boolean active = p.equals(ConfigManager.active());
            if (render) {
                boolean hov = in(xx, yy, w, 20);
                rr(g, xx, yy, xx + w, yy + 20, 8, active ? Colors.withAlpha(acc, 0.35f) : Colors.withAlpha(0xFFFFFF, hov ? 0.12f : 0.07f));
                if (active) ro(g, xx, yy, xx + w, yy + 20, 8, 1, acc);
                txc(g, p, xx + w / 2, yy + 6, active ? TXT : SUB);
            } else if (in(xx, yy, w, 20) && button == 0) {
                if (!active) {
                    ConfigManager.saveNow();
                    ConfigManager.load(p);
                    refreshProfiles();
                }
                consumed = true;
                return h;
            }
            xx += w + 6;
        }
        String[] labels = {"Zapisz teraz", "Nowy profil", "Usun aktywny", "Domyslne"};
        int bw = (lw - 24 - 3 * 8) / 4, by = y0 + h - 30;
        for (int i = 0; i < 4; i++) {
            int bx = lx + 12 + i * (bw + 8);
            boolean enabled = i != 2 || !ConfigManager.DEFAULT_PROFILE.equals(ConfigManager.active());
            if (render) {
                boolean hov = enabled && in(bx, by, bw, 22);
                rr(g, bx, by, bx + bw, by + 22, 8, Colors.withAlpha(i == 2 ? 0xF87171 : 0xFFFFFF, hov ? 0.20f : 0.09f));
                txc(g, labels[i], bx + bw / 2, by + 7, enabled ? TXT : 0xFF5A5A6E);
            } else if (enabled && in(bx, by, bw, 22) && button == 0) {
                switch (i) {
                    case 0: ConfigManager.saveNow(); break;
                    case 1: ConfigManager.createProfile(); break;
                    case 2: ConfigManager.deleteActive(); break;
                    default: ConfigManager.resetAll(); break;
                }
                refreshProfiles();
                consumed = true;
                return h;
            }
        }
        return h;
    }

    // ---- module card

    private int settingH(Setting s) {
        return s instanceof ColorSetting ? 56 : 34;
    }

    private int expandedHeight(Module m) {
        if (m.settings.isEmpty()) return 0;
        int h = 12;
        for (Setting s : m.settings) h += settingH(s);
        return h;
    }

    private String keyLabel(Module m) {
        if (listening == m) return "...";
        if (m.key < 0) return "BRAK";
        return InputUtil.fromKeyCode(m.key, -1).getLocalizedText().getString().toUpperCase(Locale.ROOT);
    }

    private int moduleCard(DrawContext g, Module m, int lx, int y0, int lw, int top, int bottom, int mode, int button) {
        boolean render = mode == RENDER;
        m.expandAnim.set(m.expanded ? 1f : 0f);
        float ex = m.expandAnim.update();
        int full = expandedHeight(m);
        int h = CARD_H + Math.round(full * ex);
        if (y0 + h < top - 2 || y0 > bottom + 2) {
            if (render) return h;
        }
        int acc = InterfaceModule.accent();
        int toggleX = lx + lw - 46;
        int keyW = 56, keyX = toggleX - keyW - 10;
        boolean hasSettings = !m.settings.isEmpty();

        if (render) {
            boolean hov = in(lx, y0, lw, CARD_H) && gmy >= top && gmy < bottom;
            m.hoverAnim.set(hov ? 1f : 0f);
            float hv = m.hoverAnim.update();
            float tg = m.toggleAnim.update();
            rr(g, lx, y0, lx + lw, y0 + h, 10, Colors.withAlpha(0xFFFFFF, 0.05f + 0.045f * hv));
            ro(g, lx, y0, lx + lw, y0 + h, 10, 1, Colors.lerp(Colors.withAlpha(0xFFFFFF, 0.07f), Colors.withAlpha(acc, 0.55f), tg));

            rr(g, lx + 10, y0 + 9, lx + 32, y0 + 31, 7, Colors.withAlpha(acc, 0.10f + 0.18f * tg));
            Icons.draw(g, m.category.ordinal(), lx + 13, y0 + 12, c(Colors.lerp(SUB, acc, tg)));
            tx(g, m.name, lx + 42, y0 + 9, TXT);
            int descW = (m.alwaysOn ? lw - 70 : keyX - lx - 70);
            tx(g, trim(m.description, descW), lx + 42, y0 + 22, SUB);

            if (hasSettings) Icons.draw(g, ex > 0.5f ? Icons.CHEVRON_UP : Icons.CHEVRON_DOWN, (m.alwaysOn ? lx + lw - 30 : keyX - 22), y0 + 12, c(SUB));
            if (!m.alwaysOn) {
                boolean hk = in(keyX, y0 + 10, keyW, 20);
                rr(g, keyX, y0 + 10, keyX + keyW, y0 + 30, 7, Colors.withAlpha(listening == m ? acc : 0xFFFFFF, listening == m ? 0.3f : (hk ? 0.14f : 0.07f)));
                txc(g, keyLabel(m), keyX + keyW / 2, y0 + 16, listening == m ? TXT : SUB);
                switchControl(g, toggleX, y0 + 12, tg, acc);
            } else {
                txr(g, "zawsze aktywne", lx + lw - 32 - (hasSettings ? 0 : -20), y0 + 16, 0xFF6E6E86);
            }

            if (ex > 0.01f) {
                scissor(g, lx, Math.max(top, y0), lx + lw, Math.min(bottom, y0 + h));
                int ry = y0 + CARD_H + 6;
                for (Setting s : m.settings) {
                    drawSetting(g, s, lx + 16, ry, lw - 32, acc);
                    ry += settingH(s);
                }
                g.disableScissor();
            }
            return h;
        }

        // ---- klikniecia
        if (!in(lx, y0, lw, h)) return h;
        if (gmy < y0 + CARD_H) {
            if (button == 0) {
                if (!m.alwaysOn && in(toggleX - 2, y0 + 8, 40, 24)) {
                    m.toggle();
                } else if (!m.alwaysOn && in(keyX, y0 + 10, keyW, 20)) {
                    listening = (listening == m) ? null : m;
                } else if (hasSettings) {
                    m.expanded = !m.expanded;
                }
                consumed = true;
            } else if (button == 1 && hasSettings) {
                m.expanded = !m.expanded;
                consumed = true;
            }
            return h;
        }
        if (button != 0 || ex < 0.9f) return h;
        int ry = y0 + CARD_H + 6;
        for (Setting s : m.settings) {
            if (clickSetting(s, lx + 16, ry, lw - 32)) { consumed = true; break; }
            ry += settingH(s);
        }
        return h;
    }

    private void switchControl(DrawContext g, int x, int y, float anim, int acc) {
        rr(g, x, y, x + 34, y + 16, 8, Colors.lerp(0xFF3A3A4E, acc, anim));
        int kx = x + 2 + Math.round(18f * anim);
        rr(g, kx, y + 2, kx + 12, y + 14, 6, 0xFFFFFFFF);
    }

    private String formatValue(FloatSetting f) {
        String v = f.decimals == 0 ? Integer.toString(f.getInt()) : String.format(Locale.ROOT, "%." + f.decimals + "f", f.get());
        return v + f.unit;
    }

    private void drawSetting(DrawContext g, Setting s, int x0, int ry, int w, int acc) {
        int rh = settingH(s);
        int controlW = 150;
        int labelW = w - controlW - 30;
        tx(g, trim(s.name, labelW), x0, ry + 4, TXT);
        tx(g, trim(s.description, labelW), x0, ry + 16, 0xFF7C7C94);

        if (s instanceof BoolSetting b) {
            float a = b.anim.update();
            switchControl(g, x0 + w - 34, ry + 6, a, acc);
        } else if (s instanceof FloatSetting f) {
            float a = f.anim.update();
            int tx0 = x0 + w - controlW, ty = ry + 18;
            txr(g, formatValue(f), x0 + w, ry + 4, SUB);
            rr(g, tx0, ty, tx0 + controlW, ty + 6, 3, Colors.withAlpha(0xFFFFFF, 0.12f));
            int fw = Math.max(6, Math.round(controlW * a));
            rr(g, tx0, ty, tx0 + fw, ty + 6, 3, acc);
            rr(g, tx0 + Math.round(controlW * a) - 5, ty - 2, tx0 + Math.round(controlW * a) + 5, ty + 8, 5, 0xFFFFFFFF);
        } else if (s instanceof ChoiceSetting ch) {
            int cxx = x0 + w - controlW + 10;
            rr(g, cxx, ry + 6, cxx + controlW - 10, ry + 26, 8, Colors.withAlpha(0xFFFFFF, 0.08f));
            tx(g, "<", cxx + 6, ry + 12, acc);
            tx(g, ">", cxx + controlW - 10 - 12, ry + 12, acc);
            txc(g, ch.label(), cxx + (controlW - 10) / 2, ry + 12, TXT);
        } else if (s instanceof ColorSetting cs) {
            int bx = x0 + w - controlW;
            rr(g, bx - 26, ry + 4, bx - 6, ry + 32, 6, cs.argb());
            ro(g, bx - 26, ry + 4, bx - 6, ry + 32, 6, 1, Colors.withAlpha(0xFFFFFF, 0.25f));
            for (int i = 0; i < SWATCHES.length; i++) {
                int sx = bx + i * 15;
                rr(g, sx, ry + 4, sx + 12, ry + 16, 3, 0xFF000000 | SWATCHES[i]);
                if (cs.get() == SWATCHES[i]) ro(g, sx - 1, ry + 3, sx + 13, ry + 17, 4, 1, 0xFFFFFFFF);
            }
            for (int i = 0; i < 75; i++) {
                fl(g, bx + i * 2, ry + 24, bx + i * 2 + 2, ry + 34, Colors.hsv(i / 75f, 1f, 1f));
            }
            int mxk = bx + Math.round(Colors.hue(cs.get()) * 150f);
            rr(g, mxk - 1, ry + 22, mxk + 2, ry + 36, 1, 0xFFFFFFFF);
        }
    }

    private boolean clickSetting(Setting s, int x0, int ry, int w) {
        int rh = settingH(s);
        int controlW = 150;
        if (s instanceof BoolSetting b) {
            if (in(x0, ry, w, rh)) { b.toggle(); return true; }
        } else if (s instanceof FloatSetting f) {
            int tx0 = x0 + w - controlW, ty = ry + 18;
            if (in(tx0 - 6, ty - 8, controlW + 12, 22)) {
                dragF = f; dragFx = tx0; dragFw = controlW;
                f.setFraction((gmx - tx0) / (float) controlW);
                return true;
            }
        } else if (s instanceof ChoiceSetting ch) {
            int cxx = x0 + w - controlW + 10;
            if (in(cxx, ry + 6, controlW - 10, 20)) {
                ch.cycle(gmx < cxx + (controlW - 10) / 3 ? -1 : 1);
                return true;
            }
        } else if (s instanceof ColorSetting cs) {
            int bx = x0 + w - controlW;
            for (int i = 0; i < SWATCHES.length; i++) {
                if (in(bx + i * 15, ry + 4, 12, 12)) { cs.set(SWATCHES[i]); return true; }
            }
            if (in(bx, ry + 22, controlW, 14)) {
                dragC = cs; dragCx = bx; dragCw = controlW;
                cs.set(Colors.hsv((gmx - bx) / (float) controlW, 1f, 1f));
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- input

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (closing) return true;
        computeFrame();
        mouseToGui(mx, my);
        if (listening != null) { listening = null; }
        if (in(px + W - 30, py + 18, 16, 16)) { close(); return true; }
        if (button == 0) {
            Category[] cats = Category.values();
            for (int i = 0; i < cats.length; i++) {
                if (in(px + 10, py + 60 + i * 34, SIDE - 20, 28)) {
                    tab = cats[i];
                    search = "";
                    rebuild();
                    return true;
                }
            }
        }
        pass(null, CLICK, button);
        return true;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        computeFrame();
        mouseToGui(mx, my);
        if (dragF != null) { dragF.setFraction((gmx - dragFx) / (float) dragFw); return true; }
        if (dragC != null) { dragC.set(Colors.hsv(Colors.clamp01((gmx - dragCx) / (float) dragCw), 1f, 1f)); return true; }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        dragF = null;
        dragC = null;
        return true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        int viewH = H - 62;
        float max = Math.max(0, contentH - viewH);
        scrollTarget = Math.max(0f, Math.min(max, scrollTarget - (float) vertical * 44f));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listening != null) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) { /* anuluj */ }
            else if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) { listening.key = -1; dev.novaclient.core.ConfigManager.markDirty(); }
            else { listening.key = keyCode; dev.novaclient.core.ConfigManager.markDirty(); }
            listening = null;
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (!search.isEmpty()) { search = ""; rebuild(); } else close();
            return true;
        }
        if (NovaClient.OPEN_GUI != null && NovaClient.OPEN_GUI.matchesKey(keyCode, scanCode)) { close(); return true; }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !search.isEmpty()) {
            search = search.substring(0, search.length() - 1);
            rebuild();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (listening != null || chr < 32 || chr == 127 || search.length() >= 40) return false;
        search += chr;
        rebuild();
        return true;
    }
}

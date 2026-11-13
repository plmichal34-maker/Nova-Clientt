package dev.novaclient.util;

import net.minecraft.client.gui.DrawContext;

/** Rysowanie zaokraglonych ksztaltow na DrawContext (same wypelnienia, bez alokacji). */
public final class RenderUtil {
    private static final int MAX_R = 16;
    private static final int[][] INSET = new int[MAX_R + 1][];

    static {
        for (int r = 1; r <= MAX_R; r++) {
            INSET[r] = new int[r];
            for (int i = 0; i < r; i++) {
                double dy = r - i - 0.5;
                double dx = Math.sqrt(Math.max(0, (double) r * r - dy * dy));
                INSET[r][i] = r - (int) Math.round(dx);
            }
        }
    }

    private RenderUtil() {}

    private static int inset(int y, int y1, int y2, int r) {
        if (r <= 0) return 0;
        if (y < y1 + r) return INSET[r][y - y1];
        if (y >= y2 - r) return INSET[r][y2 - 1 - y];
        return 0;
    }

    private static int clampR(int r, int x1, int y1, int x2, int y2) {
        int m = Math.min(x2 - x1, y2 - y1) / 2;
        return Math.max(0, Math.min(Math.min(r, m), MAX_R));
    }

    public static void rect(DrawContext c, int x1, int y1, int x2, int y2, int color) {
        if (Colors.alpha(color) <= 0 || x2 <= x1 || y2 <= y1) return;
        c.fill(x1, y1, x2, y2, color);
    }

    public static void roundedRect(DrawContext c, int x1, int y1, int x2, int y2, int r, int color) {
        if (Colors.alpha(color) <= 0 || x2 <= x1 || y2 <= y1) return;
        r = clampR(r, x1, y1, x2, y2);
        if (r == 0) { c.fill(x1, y1, x2, y2, color); return; }
        for (int i = 0; i < r; i++) {
            int in = INSET[r][i];
            c.fill(x1 + in, y1 + i, x2 - in, y1 + i + 1, color);
            c.fill(x1 + in, y2 - 1 - i, x2 - in, y2 - i, color);
        }
        c.fill(x1, y1 + r, x2, y2 - r, color);
    }

    /** Obramowanie o grubosci th wewnatrz prostokata. */
    public static void roundedOutline(DrawContext c, int x1, int y1, int x2, int y2, int r, int th, int color) {
        if (Colors.alpha(color) <= 0 || x2 <= x1 || y2 <= y1 || th <= 0) return;
        r = clampR(r, x1, y1, x2, y2);
        int ix1 = x1 + th, iy1 = y1 + th, ix2 = x2 - th, iy2 = y2 - th;
        int ir = Math.max(0, r - th);
        boolean hasInner = ix2 > ix1 && iy2 > iy1;
        for (int y = y1; y < y2; y++) {
            int in = inset(y, y1, y2, r);
            int oL = x1 + in, oR = x2 - in;
            if (hasInner && y >= iy1 && y < iy2) {
                int ii = inset(y, iy1, iy2, ir);
                int iL = ix1 + ii, iR = ix2 - ii;
                if (iL > oL) c.fill(oL, y, iL, y + 1, color);
                if (oR > iR) c.fill(iR, y, oR, y + 1, color);
            } else {
                c.fill(oL, y, oR, y + 1, color);
            }
        }
    }

    public static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }
}

package dev.novaclient.gui;

import dev.novaclient.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Wlasne ikony 16x16 rysowane z prostych ksztaltow (bez tekstur). */
public final class Icons {
    public static final int SEARCH = 5, CHEVRON_DOWN = 6, CHEVRON_UP = 7, CLOSE = 8;

    private Icons() {}

    /** id 0..4 = kategorie (kolejnosc enum Category), pozostale - stale powyzej. */
    public static void draw(DrawContext c, int id, int x, int y, int col) {
        switch (id) {
            case 0: // przedmioty (skrzynka)
                RenderUtil.roundedOutline(c, x + 2, y + 4, x + 14, y + 14, 3, 1, col);
                c.fill(x + 4, y + 1, x + 12, y + 3, col);
                c.fill(x + 7, y + 7, x + 9, y + 10, col);
                break;
            case 1: // efekty (iskra)
                c.fill(x + 7, y + 1, x + 9, y + 15, col);
                c.fill(x + 1, y + 7, x + 15, y + 9, col);
                c.fill(x + 3, y + 3, x + 5, y + 5, col);
                c.fill(x + 11, y + 3, x + 13, y + 5, col);
                c.fill(x + 3, y + 11, x + 5, y + 13, col);
                c.fill(x + 11, y + 11, x + 13, y + 13, col);
                break;
            case 2: // ekwipunek (siatka 3x3)
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j < 3; j++)
                        RenderUtil.roundedRect(c, x + 1 + i * 5, y + 1 + j * 5, x + 4 + i * 5, y + 4 + j * 5, 1, col);
                break;
            case 3: // wizualne (oko)
                RenderUtil.roundedOutline(c, x + 1, y + 4, x + 15, y + 12, 4, 1, col);
                RenderUtil.roundedRect(c, x + 6, y + 6, x + 10, y + 10, 2, col);
                break;
            case 4: // ustawienia (suwaki)
                c.fill(x + 1, y + 3, x + 15, y + 4, col);
                c.fill(x + 1, y + 8, x + 15, y + 9, col);
                c.fill(x + 1, y + 13, x + 15, y + 14, col);
                RenderUtil.roundedRect(c, x + 9, y + 1, x + 12, y + 6, 1, col);
                RenderUtil.roundedRect(c, x + 3, y + 6, x + 6, y + 11, 1, col);
                RenderUtil.roundedRect(c, x + 10, y + 11, x + 13, y + 16, 1, col);
                break;
            case SEARCH:
                RenderUtil.roundedOutline(c, x + 1, y + 1, x + 11, y + 11, 5, 1, col);
                for (int i = 0; i < 4; i++) c.fill(x + 10 + i, y + 10 + i, x + 12 + i, y + 12 + i, col);
                break;
            case CHEVRON_DOWN:
                for (int i = 0; i < 5; i++) {
                    c.fill(x + 2 + i, y + 5 + i, x + 4 + i, y + 6 + i, col);
                    c.fill(x + 12 - i, y + 5 + i, x + 14 - i, y + 6 + i, col);
                }
                break;
            case CHEVRON_UP:
                for (int i = 0; i < 5; i++) {
                    c.fill(x + 2 + i, y + 10 - i, x + 4 + i, y + 11 - i, col);
                    c.fill(x + 12 - i, y + 10 - i, x + 14 - i, y + 11 - i, col);
                }
                break;
            case CLOSE:
                for (int i = 0; i < 10; i++) {
                    c.fill(x + 3 + i, y + 3 + i, x + 5 + i, y + 4 + i, col);
                    c.fill(x + 12 - i, y + 3 + i, x + 14 - i, y + 4 + i, col);
                }
                break;
            default:
                break;
        }
    }
}

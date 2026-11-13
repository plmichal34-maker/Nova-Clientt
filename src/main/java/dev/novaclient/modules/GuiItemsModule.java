package dev.novaclient.modules;

import dev.novaclient.core.FloatSetting;
import dev.novaclient.module.Category;
import dev.novaclient.module.Module;

/** Rozmiar i pozycja przedmiotow w ekwipunku, na pasku szybkiego dostepu i w skrzyniach. */
public final class GuiItemsModule extends Module {
    public final FloatSetting inventoryScale = add(new FloatSetting("inventory_scale", "Rozmiar w ekwipunku", "Rozmiar przedmiotow w oknie ekwipunku.", 1f, 0.5f, 1.5f, 0.05f, "x"));
    public final FloatSetting hotbarScale = add(new FloatSetting("hotbar_scale", "Rozmiar na pasku", "Rozmiar przedmiotow na pasku szybkiego dostepu.", 1f, 0.5f, 1.5f, 0.05f, "x"));
    public final FloatSetting chestScale = add(new FloatSetting("chest_scale", "Rozmiar w skrzyniach", "Rozmiar przedmiotow w oknach skrzyn.", 1f, 0.5f, 1.5f, 0.05f, "x"));
    public final FloatSetting hoverPop = add(new FloatSetting("hover_pop", "Powiekszenie po najechaniu", "O ile przedmiot powieksza sie pod kursorem (animacja ekwipunku).", 0f, 0f, 0.5f, 0.02f));
    public final FloatSetting offsetX = add(new FloatSetting("offset_x", "Przesuniecie X", "Przesuniecie przedmiotow w poziomie (piksele).", 0f, -4f, 4f, 1f, " px"));
    public final FloatSetting offsetY = add(new FloatSetting("offset_y", "Przesuniecie Y", "Przesuniecie przedmiotow w pionie (piksele).", 0f, -4f, 4f, 1f, " px"));

    public GuiItemsModule() {
        super("gui_items", "Przedmioty w GUI", "Rozmiar i pozycja przedmiotow w ekwipunku, na pasku i w skrzyniach.",
                Category.ITEMS, false, false);
    }
}

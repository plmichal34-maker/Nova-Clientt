package dev.novaclient.module;

public enum Category {
    ITEMS("Przedmioty"),
    EFFECTS("Efekty"),
    INVENTORY("Ekwipunek"),
    VISUALS("Wizualne"),
    SETTINGS("Ustawienia");

    public final String label;

    Category(String label) { this.label = label; }
}

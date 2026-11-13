package dev.novaclient.anim;

/** Rodzaje animacji - kazdy mozna wylaczyc osobno w ustawieniach. */
public enum AnimType {
    GUI_OPEN("Otwieranie GUI", "Animacja otwierania okna clienta."),
    GUI_CLOSE("Zamykanie GUI", "Animacja zamykania okna clienta."),
    APPEAR("Pojawianie sie elementow", "Karty funkcji pojawiaja sie kolejno z lekkim przesunieciem."),
    HOVER("Najechanie kursorem", "Plynne podswietlenia po najechaniu na elementy GUI."),
    TOGGLE("Przelaczniki", "Animowane przesuwanie przelacznikow ON/OFF."),
    TRANSITION("Przejscia miedzy zakladkami", "Plynne przejscia przy zmianie zakladki i rozwijaniu kart."),
    HUD("Animacje HUD", "Plynne przesuwanie zaznaczenia na pasku szybkiego dostepu i celownika."),
    INVENTORY("Animacje ekwipunku", "Plynne podswietlanie slotow i powiekszanie przedmiotow po najechaniu."),
    SETTING_CHANGE("Zmiany ustawien", "Plynne zmiany wartosci suwakow i kolorow.");

    public final String label;
    public final String description;

    AnimType(String label, String description) {
        this.label = label;
        this.description = description;
    }
}

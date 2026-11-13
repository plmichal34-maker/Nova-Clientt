# Nova Client (czesc 1) - Fabric, Minecraft 1.21.4

Wizualny client-mod: okno GUI (glassmorphism), system animacji, efekty przedmiotow,
personalizacja ekwipunku, skrzyn, paska szybkiego dostepu, celownika i menu. Tylko wyglad - bez zmian mechaniki gry.

## Jak zbudowac plik .jar

### Opcja A - GitHub buduje za ciebie (bez instalowania czegokolwiek)
1. Utworz repozytorium na GitHub (moze byc prywatne) i wrzuc do niego **cala zawartosc tego folderu**
   (razem z ukrytym folderem `.github`).
2. Wejdz w zakladke **Actions** -> workflow **build** -> po kilku minutach otworz ostatni przebieg.
3. W sekcji **Artifacts** pobierz `novaclient-jar` (zip z plikiem `novaclient-0.1.0.jar`).
   Uwaga: w zipie jest tez `-sources.jar` - do gry potrzebny jest ten bez `-sources`.

### Opcja B - lokalnie
Wymagane JDK 21. W folderze projektu: `./gradlew build` (Windows: `gradlew.bat build`).
Gotowy plik: `build/libs/novaclient-0.1.0.jar`.

## Instalacja w grze
1. Zainstaluj **Fabric Loader** dla Minecrafta **1.21.4** (https://fabricmc.net/use/installer/).
2. Pobierz **Fabric API** dla 1.21.4 (Modrinth / CurseForge).
3. Wrzuc do folderu `mods` (`%appdata%\.minecraft\mods`): `fabric-api-....jar` oraz `novaclient-0.1.0.jar` (ten bez `-sources`).
4. Uruchom profil Fabric 1.21.4. Okno clienta: **Prawy Shift** (zmiana klawisza: Opcje -> Sterowanie -> Nova Client).

## Obsluga GUI
- Zakladki po lewej: Przedmioty, Efekty, Ekwipunek, Wizualne, Ustawienia.
- **Wyszukiwarka**: po prostu zacznij pisac - szuka w nazwach, opisach i ustawieniach wszystkich funkcji.
- Karta funkcji: kliknij przelacznik (ON/OFF), kliknij pole klawisza i nacisnij klawisz (Backspace = usun skrot),
  kliknij karte, aby rozwinac ustawienia (suwaki, kolory, wybory).
- Zakladka **Ustawienia**: profile (wczytaj / nowy / usun / domyslne / zapisz), globalne wylaczenie animacji
  i efektow, osobne wylaczniki kazdej animacji, motyw kolorow, skala i przezroczystosc GUI, wydajnosc.

## Konfiguracja
Zapisuje sie automatycznie (`.minecraft/config/novaclient/profiles/<profil>.json`, aktywny profil w `meta.json`).

## Co dziala
- Przedmiot w rece: rozmiar, pozycja XYZ, kolysanie, lewa reka.
- Przedmioty w GUI: osobne rozmiary dla ekwipunku, paska i skrzyn, powiekszenie po najechaniu, przesuniecie.
- Przedmioty na ziemi: rozmiar, wysokosc, dodatkowy obrot i unoszenie.
- Efekty (kazdy z kolorem, intensywnoscia, szybkoscia, rozmiarem, przezroczystoscia): poswiata, ramka, animowany gradient,
  RGB, pulsowanie, czasteczki, enchant. Rysowane na pasku, w ekwipunku i w skrzyniach.
- Ekwipunek i skrzynie: tlo, zaokraglone kolorowe sloty, obramowanie (staly kolor / tecza / gradient), podswietlenie po najechaniu.
- Pasek szybkiego dostepu: zaznaczenie z plynnym przesuwaniem, kontury slotow.
- Celownik (krzyz / kropka / okrag, dynamiczny), menu glowne i ekran pauzy (animowany gradient, przyciemnienie).
- Moduly: ON/OFF, skrot klawiszowy, opis, zapis ustawien, wyszukiwanie.

## Czego jeszcze NIE ma (zaznaczone uczciwie)
- Odstepy miedzy slotami, animacje przesuwania przedmiotow miedzy slotami - wymagaja ingerencji w uklad okien (czesc 2).
- Efekty (poswiata, czasteczki) na przedmiocie trzymanym w rece i na ziemi (czesc 2).
- Animacje jedzenia i poruszania, efekty blokow i czasteczek swiata (czesc 2).
- Prawdziwe rozmycie "glass" robi silnik gry (opcja rozmycia tla menu w ustawieniach Minecrafta), GUI dodaje polprzezroczystosc.

## UWAGA - kod nie byl uruchamiany w grze
Ten projekt napisano bez dostepu do bibliotek Minecrafta, wiec nie przeszedl kompilacji ani testu w grze.
Sprawdzono tylko logike (konfiguracja, profile, animacje, wyszukiwarka) na atrapach klas.
Najbardziej ryzykowne miejsca to mixiny (`src/main/java/dev/novaclient/mixin`) - jesli build lub start gry zglosi blad,
wklej log, a poprawie. Mixiny sa ustawione tolerancyjnie (`defaultRequire: 0`): gdy nazwa metody w 1.21.4 sie nie zgadza, ta jedna funkcja po prostu nie dziala, a reszta (GUI, konfiguracja, efekty w oknach) dziala dalej.

## Rozbudowa (czesc 2+)
Nowa funkcja = klasa rozszerzajaca `Module` + jedna linia w `ModuleManager.init()`. Nowy efekt = klasa rozszerzajaca
`EffectModule`. Pojawi sie w GUI, wyszukiwarce i konfiguracji automatycznie.

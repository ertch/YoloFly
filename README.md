# Butterfly Detector Android App

Eine Android-App zur automatischen Schmetterlings-Erkennung mit YOLO und TensorFlow Lite.

## Features

- **Automatische Erkennung**: Kamera startet direkt und erkennt Schmetterlinge in Echtzeit
- **Auto-Foto-Modus**: Nimmt automatisch 30 Fotos bei Schmetterlings-Erkennung auf
- **Manueller Modus**: Nach 30 Fotos wechselt die App in den manuellen Fotografier-Modus
- **Top-9-Galerie**: Zeigt die 9 Fotos mit der höchsten Erkennungsrate an
- **TensorFlow Lite Integration**: Optimiert für mobile Geräte

## Setup in Android Studio

1. **Projekt importieren**:
   ```
   File → Open → taiwan-workshop-ai Ordner auswählen
   ```

2. **YOLO-Modell hinzufügen**:
   - Dein trainiertes Schmetterlings-Modell nach `app/src/main/assets/butterfly_model.tflite` kopieren
   - Falls nötig, Labels in `app/src/main/assets/butterfly_labels.txt` anpassen

3. **Build & Run**:
   ```
   Build → Rebuild Project
   Run → Run 'app'
   ```

## Projektstruktur

```
app/
├── src/main/
│   ├── java/com/butterfly/detector/
│   │   ├── MainActivity.kt              # Haupt-Kamera-Activity
│   │   ├── GalleryActivity.kt           # Top-9-Fotos Galerie
│   │   ├── ml/ButterflyDetector.kt      # TensorFlow Lite Integration
│   │   ├── model/DetectionResult.kt     # Erkennungs-Datenmodell
│   │   ├── adapter/PhotoAdapter.kt      # RecyclerView Adapter
│   │   └── utils/ImageUtils.kt          # Bild-Verarbeitungshelfer
│   ├── res/
│   │   ├── layout/                      # UI Layouts
│   │   ├── values/                      # Strings, Colors, Themes
│   │   └── xml/                         # Android Konfiguration
│   └── assets/
│       ├── butterfly_model.tflite      # DEIN TRAINIERTES MODELL HIER
│       └── butterfly_labels.txt        # Schmetterlings-Klassen
└── build.gradle                        # Dependencies & Build Config
```

## Benötigte Berechtigungen

- **Kamera**: Für Live-Vorschau und Foto-Aufnahme
- **Speicher**: Für das Speichern der aufgenommenen Fotos

## Dependencies

- **CameraX**: Moderne Android Kamera-API
- **TensorFlow Lite**: Mobile ML-Inferenz
- **Glide**: Bild-Loading und -Caching
- **Material Design**: Moderne UI-Komponenten

## Anpassungen

### Eigenes YOLO-Modell verwenden:
1. Modell nach TensorFlow Lite konvertieren
2. Als `butterfly_model.tflite` in `assets/` ablegen
3. Labels in `butterfly_labels.txt` anpassen
4. Falls nötig, Input-Größe in `ButterflyDetector.kt` anpassen

### Erkennungs-Schwellwert ändern:
```kotlin
// In ButterflyDetector.kt
private const val CONFIDENCE_THRESHOLD = 0.3f // Anpassen
```

### Auto-Foto-Anzahl ändern:
```kotlin
// In MainActivity.kt
if (autoPhotoCount >= 30) // Zahl anpassen
```

## Troubleshooting

- **Kamera startet nicht**: Berechtigungen in den App-Einstellungen prüfen
- **Modell lädt nicht**: Dateiname und Pfad in `assets/` überprüfen
- **Langsame Inferenz**: GPU-Delegate aktivieren (siehe `ButterflyDetector.kt`)
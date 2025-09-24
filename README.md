# YoloFly 🦋

Android App für automatische Schmetterlings-Erkennung mit YOLO und TensorFlow Lite.

## Branches

### Repository Management
- **main**

### Development Branch
- **develop**
    - every feature branch should originate from develop

### feature/base-structure
- **feature/base-structure**
    - the base structure of the app
    - no real functionality
    - starting point of development

### KI Training
- **tensorflow-yolo-training** - TensorFlow/YOLO Modell-Training [vibecoded] (Training Branch)


### Android App Development
- **vibecode-ertch** - Kotlin App [videcoded] (Working Branch ertch)



## Features

### Main
- Prasentation Branch

### App (vibecode-ertch Branch)

- **Auto-Capture** (bis zu 30 Fotos bei Schmetterlings-Detektion)
- **Manueller Modus** nach Auto-Phase
- **Top-9 Galerie** mit höchsten Konfidenz-Werten
- **TensorFlow Lite** YOLO Integration
- **Material Design** UI

### KI Training (tensorflow-yolo-training Branch)
- **YOLO Training Pipeline** für Schmetterlings-Erkennung
- **Modell-Evaluation** und Metriken
- **TensorFlow Lite Export** für Android Integration
- **10 Schmetterlings-Klassen** vordefiniert
- **Dataset-Struktur** für Training/Validation/Test


## Getting Started

### Android App Entwicklung
```bash
git checkout vibecode-ertch
# Projekt in Android Studio öffnen
# TensorFlow Lite Modell hinzufügen
# Build & Run
```

### KI Training
```bash
git checkout tensorflow-yolo-training
# Python 3.10 Environment erstellen
# pip install -r requirements.txt
# Dataset vorbereiten
# python train_butterfly_yolo.py
```

### tag:HomeFragment level:debug tag:HomeFragment level:debug 
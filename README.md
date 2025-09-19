# YoloFly 🦋

Android App für automatische Schmetterlings-Erkennung mit YOLO und TensorFlow Lite.

## Branches

### 📱 Android App Development
- **vibecode-ertch** - Vollständige Android Kotlin App Implementation (Working Branch)

### 🤖 KI Training 
- **tensorflow-yolo-training** - TensorFlow/YOLO Modell-Training (Training Branch)

### 🏠 Repository Management
- **main** - Hauptbranch mit Übersicht

## Features

### Android App (vibecode-ertch Branch)
- 🎥 **Live Kamera-Erkennung** mit CameraX
- 📸 **Auto-Capture** (30 Fotos bei Schmetterlings-Detektion)
- 🖱️ **Manueller Modus** nach Auto-Phase
- 🏆 **Top-9 Galerie** mit höchsten Konfidenz-Werten
- 🤖 **TensorFlow Lite** YOLO Integration
- 🎨 **Material Design** UI

### KI Training (tensorflow-yolo-training Branch)
- 🧠 **YOLO Training Pipeline** für Schmetterlings-Erkennung
- 📊 **Modell-Evaluation** und Metriken
- 📱 **TensorFlow Lite Export** für Android Integration
- 🏷️ **10 Schmetterlings-Klassen** vordefiniert
- 📁 **Dataset-Struktur** für Training/Validation/Test

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

## Workflow
1. **Daten sammeln** → Schmetterlings-Bilder annotieren
2. **Modell trainieren** → `tensorflow-yolo-training` Branch
3. **TFLite exportieren** → Für Android Integration
4. **App entwickeln** → `vibecode-ertch` Branch
5. **Testing & Deployment** → Ready for Production

Ready for both AI Training and Android Development! 🚀
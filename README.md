# TensorFlow / YOLO Training Branch 🤖

Dieser Branch ist ausschließlich für das Training von YOLO-Modellen für Schmetterlings-Erkennung vorgesehen.

## Setup

### 1. Python Environment
```bash
python3.10 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

### 2. YOLO Training Dependencies
```bash
pip install ultralytics==8.2.90
pip install torch==2.2.2 torchvision==0.17.2
pip install opencv-python==4.9.0.80
pip install matplotlib pandas numpy
```

### 3. Dataset Struktur
```
dataset/
├── train/
│   ├── images/
│   └── labels/
├── val/
│   ├── images/
│   └── labels/
└── test/
    ├── images/
    └── labels/
```

## Training Scripts

### Basis YOLO Training
```python
from ultralytics import YOLO

# Load pre-trained model
model = YOLO('yolov8n.pt')

# Train the model
results = model.train(
    data='butterfly_dataset.yaml',
    epochs=100,
    imgsz=640,
    batch=16,
    name='butterfly_detection'
)
```

### Zu TensorFlow Lite Export
```python
# Export trained model to TensorFlow Lite
model.export(format='tflite', int8=True, imgsz=224)
```

## Schmetterlings-Klassen
1. Admiral
2. Bläuling
3. Schwalbenschwanz
4. Weißling
5. Schachbrettfalter
6. Tagpfauenauge
7. Kleiner Fuchs
8. C-Falter
9. Distelfalter
10. Zitronenfalter

## Workflow

1. **Dataset vorbereiten** → Bilder annotieren
2. **YOLO trainieren** → `train_butterfly_yolo.py`
3. **Modell evaluieren** → Validation & Test
4. **TFLite konvertieren** → Für Android App
5. **Integration** → In `vibecode-ertch` Branch

---
⚠️ **Hinweis**: Dieser Branch enthält nur Training-Code, keine Android App!
#!/usr/bin/env python3
"""
YOLO Schmetterlings-Training Script
Trainiert ein YOLO-Modell für Butterfly Detection und exportiert es zu TensorFlow Lite
"""

from ultralytics import YOLO
import os
import yaml

def create_dataset_yaml():
    """Erstellt dataset.yaml für YOLO Training"""
    dataset_config = {
        'path': './dataset',
        'train': 'train/images',
        'val': 'val/images',
        'test': 'test/images',
        'nc': 10,  # number of classes
        'names': [
            'Admiral',
            'Bläuling', 
            'Schwalbenschwanz',
            'Weißling',
            'Schachbrettfalter',
            'Tagpfauenauge',
            'Kleiner Fuchs',
            'C-Falter',
            'Distelfalter',
            'Zitronenfalter'
        ]
    }
    
    with open('butterfly_dataset.yaml', 'w') as f:
        yaml.dump(dataset_config, f, default_flow_style=False, allow_unicode=True)
    
    print("✅ Dataset YAML erstellt: butterfly_dataset.yaml")

def train_model():
    """Trainiert das YOLO-Modell"""
    print("🚀 Starte YOLO Training...")
    
    # Load pre-trained YOLO model
    model = YOLO('yolov8n.pt')  # nano model für schnelleres Training
    
    # Train the model
    results = model.train(
        data='butterfly_dataset.yaml',
        epochs=100,
        imgsz=640,
        batch=16,
        name='butterfly_detection',
        patience=10,
        save=True,
        device='cpu'  # Change to 'cuda' if GPU available
    )
    
    print("✅ Training abgeschlossen!")
    return model

def export_to_tflite(model):
    """Exportiert das trainierte Modell zu TensorFlow Lite"""
    print("📱 Exportiere zu TensorFlow Lite...")
    
    # Export to TensorFlow Lite with quantization
    model.export(
        format='tflite',
        int8=True,  # INT8 quantization for mobile
        imgsz=224,  # Android app expects 224x224
        dynamic=False
    )
    
    print("✅ TensorFlow Lite Export erfolgreich!")
    print("📁 Modell gespeichert als: runs/detect/butterfly_detection/weights/best.tflite")

def main():
    """Hauptfunktion für Training Pipeline"""
    print("🦋 Butterfly YOLO Training Pipeline")
    print("=" * 50)
    
    # Check if dataset exists
    if not os.path.exists('dataset'):
        print("⚠️  Dataset Ordner nicht gefunden!")
        print("📁 Erstelle Dataset-Struktur...")
        os.makedirs('dataset/train/images', exist_ok=True)
        os.makedirs('dataset/train/labels', exist_ok=True)
        os.makedirs('dataset/val/images', exist_ok=True)
        os.makedirs('dataset/val/labels', exist_ok=True)
        os.makedirs('dataset/test/images', exist_ok=True)
        os.makedirs('dataset/test/labels', exist_ok=True)
        print("✅ Dataset-Struktur erstellt")
        print("📋 Bitte Bilder und Labels hinzufügen vor dem Training!")
        return
    
    # Create dataset configuration
    create_dataset_yaml()
    
    # Train model
    model = train_model()
    
    # Export to TensorFlow Lite
    export_to_tflite(model)
    
    print("\n🎉 Training Pipeline abgeschlossen!")
    print("📱 TensorFlow Lite Modell ready für Android Integration")

if __name__ == "__main__":
    main()
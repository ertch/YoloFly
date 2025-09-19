#!/usr/bin/env python3
"""
Modell Evaluation Script
Evaluiert das trainierte YOLO-Modell und erstellt Metriken
"""

from ultralytics import YOLO
import matplotlib.pyplot as plt
import cv2
import os

def evaluate_model(model_path="runs/detect/butterfly_detection/weights/best.pt"):
    """Evaluiert das trainierte Modell"""
    print("📊 Starte Modell-Evaluation...")
    
    # Load trained model
    model = YOLO(model_path)
    
    # Validate on test set
    results = model.val(data='butterfly_dataset.yaml', split='test')
    
    print(f"📈 mAP@0.5: {results.box.map50:.3f}")
    print(f"📈 mAP@0.5:0.95: {results.box.map:.3f}")
    
    return results

def test_single_image(model_path, image_path):
    """Testet das Modell mit einem einzelnen Bild"""
    model = YOLO(model_path)
    
    # Run inference
    results = model(image_path)
    
    # Plot results
    for r in results:
        im_array = r.plot()  # plot a BGR numpy array of predictions
        im = cv2.cvtColor(im_array, cv2.COLOR_BGR2RGB)
        plt.imshow(im)
        plt.axis('off')
        plt.title(f'Butterfly Detection - {os.path.basename(image_path)}')
        plt.show()

def create_confusion_matrix(model_path):
    """Erstellt Confusion Matrix"""
    model = YOLO(model_path)
    
    # Validate and create confusion matrix
    results = model.val(data='butterfly_dataset.yaml', plots=True)
    
    print("📊 Confusion Matrix erstellt in runs/detect/val/")

if __name__ == "__main__":
    print("🦋 Butterfly Model Evaluation")
    print("=" * 40)
    
    model_path = "runs/detect/butterfly_detection/weights/best.pt"
    
    if os.path.exists(model_path):
        # Evaluate model
        evaluate_model(model_path)
        
        # Create confusion matrix
        create_confusion_matrix(model_path)
        
        print("✅ Evaluation abgeschlossen!")
    else:
        print(f"❌ Modell nicht gefunden: {model_path}")
        print("🏃 Bitte zuerst train_butterfly_yolo.py ausführen!")
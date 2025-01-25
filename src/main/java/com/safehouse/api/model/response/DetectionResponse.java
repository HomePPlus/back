package com.safehouse.api.model.response;

import java.util.List;

public class DetectionResponse {
    private List<Detection> detections;

    public List<Detection> getDetections() {
        return detections;
    }

    public void setDetections(List<Detection> detections) {
        this.detections = detections;
    }

    public static class Detection {
        private String model;
        private String label;
        private double confidence;
        private List<Double> bbox;

        // Getters and setters
        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public List<Double> getBbox() {
            return bbox;
        }

        public void setBbox(List<Double> bbox) {
            this.bbox = bbox;
        }
    }
}

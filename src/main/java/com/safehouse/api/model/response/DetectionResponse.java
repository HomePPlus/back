package com.safehouse.api.model.response;

import java.util.List;

/**
 * 결함 탐지 결과를 포함하는 응답 클래스.
 */
public class DetectionResponse {
    private double totalScore; // 총 결함 점수
    private List<Detection> detections; // 감지된 결함 목록
    private String image; // 처리된 이미지 (Base64 인코딩)

    // 총 점수 게터
    public double getTotalScore() {
        return totalScore;
    }

    // 총 점수 세터
    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    // 감지 목록 게터
    public List<Detection> getDetections() {
        return detections;
    }

    // 감지 목록 세터
    public void setDetections(List<Detection> detections) {
        this.detections = detections;
    }

    // 이미지 게터
    public String getImage() {
        return image;
    }

    // 이미지 세터
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * 감지된 결함의 정보를 포함하는 내부 클래스.
     */
    public static class Detection {
        private String model; // 사용된 YOLO 모델명
        private String label; // 결함 유형 (예: crack_3)
        private double confidence; // 신뢰도 (0~1)
        private List<Integer> bbox; // 경계 상자 좌표 (x1, y1, x2, y2)
        private int score; // 결함 심각도 점수

        // 모델 게터
        public String getModel() {
            return model;
        }

        // 모델 세터
        public void setModel(String model) {
            this.model = model;
        }

        // 라벨 게터
        public String getLabel() {
            return label;
        }

        // 라벨 세터
        public void setLabel(String label) {
            this.label = label;
        }

        // 신뢰도 게터
        public double getConfidence() {
            return confidence;
        }

        // 신뢰도 세터
        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        // 경계 상자 게터
        public List<Integer> getBbox() {
            return bbox;
        }

        // 경계 상자 세터
        public void setBbox(List<Integer> bbox) {
            this.bbox = bbox;
        }

        // 점수 게터
        public int getScore() {
            return score;
        }

        // 점수 세터
        public void setScore(int score) {
            this.score = score;
        }
    }
}

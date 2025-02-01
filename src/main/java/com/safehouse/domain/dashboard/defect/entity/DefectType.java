package com.safehouse.domain.dashboard.defect.entity;

public enum DefectType {
    CRACK("균열"),
    LEAK_WHITENING("백태/누수"),
    STEEL_DAMAGE("강재 손상"),
    PAINT_DAMAGE("도장 손상"),
    PEELING("박리"),
    REBAR_EXPOSURE("철근 노출"),
    UNKNOWN("모름");

    private final String displayName;

    DefectType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String mapDefectType(String originalType) {
        try {
            // 1. 공백 제거 및 대문자 변환
            String normalized = originalType
                    .replaceAll("\\s+", "")   // 모든 공백 제거
                    .toUpperCase();

            // 2. 예외 처리
            return switch (normalized) {
                case "백태", "누수" -> LEAK_WHITENING.getDisplayName();
                case "모름" -> UNKNOWN.getDisplayName();
                case "균열" -> CRACK.getDisplayName();
                case "강재손상" -> STEEL_DAMAGE.getDisplayName();
                case "도장손상" -> PAINT_DAMAGE.getDisplayName();
                case "박리" -> PEELING.getDisplayName();
                case "철근노출" -> REBAR_EXPOSURE.getDisplayName();
                default -> throw new IllegalArgumentException();
            };
        } catch (IllegalArgumentException e) {
            return UNKNOWN.getDisplayName();
        }
    }

}


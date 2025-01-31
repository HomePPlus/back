package com.safehouse.domain.inspection.entity;

public enum InspectionType {
    REGULAR("정기"),
    REPORT("신고");

    private final String description;

    InspectionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



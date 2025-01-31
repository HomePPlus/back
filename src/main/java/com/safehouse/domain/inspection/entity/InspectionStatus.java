package com.safehouse.domain.inspection.entity;

import com.safehouse.common.exception.CustomException;

public enum InspectionStatus {
    SCHEDULED("예정됨"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료됨"),
    CANCELLED("취소됨");

    private final String description;

    InspectionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static void validateTransition(InspectionStatus current, InspectionStatus next) {
        if (current == COMPLETED || current == CANCELLED) {
            throw new IllegalStateException(
                    String.format("현재 상태 '%s'에서 변경할 수 없습니다", current.description)
            );
        }
        if (current == SCHEDULED && !(next == IN_PROGRESS || next == CANCELLED)) {
            throw new IllegalStateException(
                    String.format("'%s' → '%s' 변경 불가", current.description, next.description)
            );
        }
        if (current == IN_PROGRESS && next != COMPLETED) {
            throw new IllegalStateException(
                    String.format("'%s' → '%s' 변경 불가", current.description, next.description)
            );
        }
    }
}

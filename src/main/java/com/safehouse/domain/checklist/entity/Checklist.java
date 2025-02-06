package com.safehouse.domain.checklist.entity;

import com.safehouse.common.enums.ChecklistStatus;
import com.safehouse.domain.basetime.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "checklists")
public class Checklist extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 필수 입력 정보
    @Column(nullable = false)
    private String inspectionId;

    @Column(nullable = false)
    private String inspectionDate;

    @Column(nullable = false)
    private String inspectorName;

    @Column(nullable = false)
    private String inspectorContact;

    @Column(nullable = false)
    private String address;

    @ElementCollection
    @CollectionTable(name = "checklist_defect_types",
            joinColumns = @JoinColumn(name = "checklist_id"))
    private List<String> defectTypes;

    // 콘크리트 균열
    private String concreteCrackType;
    private String concreteCrackLengthCm;
    private String concreteCrackWidthMm;
    private String concreteCrackDepthMm;
    private String concreteCrackLeakage;
    private String concreteCrackMovement;
    private String concreteCrackChange;
    private String concreteCrackCondition;
    private String concreteCrackEmergency;
    private String concreteCrackEmergencyAction;
    private String concreteCrackRepairPlan;

    // 누수/백태
    private String leakEfloLeakageRange;
    private String leakEfloLeakageCause;
    private String leakEfloEflorescence;
    private String leakEfloImpact;
    private String leakEfloEmergency;
    private String leakEfloEmergencyAction;
    private String leakEfloRepairPlan;

    // 강재 손상
    private String steelDamageRange;
    private String steelDamageSeverity;
    private String steelDamageCause;
    private String steelDamageStabilityImpact;
    private String steelDamageEmergency;
    private String steelDamageEmergencyAction;
    private String steelDamageRepairPlan;

    // 박리
    private String delaminationRange;
    private String delaminationCause;
    private String delaminationStabilityImpact;
    private String delaminationEmergency;
    private String delaminationEmergencyAction;
    private String delaminationRepairPlan;

    // 철근 노출
    private String rebarExposureRange;
    private String rebarExposureCondition;
    private String rebarExposureCause;
    private String rebarExposureStabilityImpact;
    private String rebarExposureEmergency;
    private String rebarExposureEmergencyAction;
    private String rebarExposureRepairPlan;

    // 도장 손상
    private String paintDamageRange;
    private String paintDamageCause;
    private String paintDamageCondition;
    private String paintDamageEmergency;
    private String paintDamageEmergencyAction;
    private String paintDamageRepairPlan;

    // 필수 입력 정보
    @Column(nullable = false)
    private String overallResult;

    @Column(nullable = false)
    private String monitoringRequired;

    private String nextInspectionDate;
    private String reportUrl;
    private ChecklistStatus status;

    @Builder
    public Checklist(String inspectionId, String inspectionDate,
                     String inspectorName, String inspectorContact,
                     String address, List<String> defectTypes,
                     String concreteCrackType, String concreteCrackLengthCm,
                     String concreteCrackWidthMm, String concreteCrackDepthMm,
                     String concreteCrackLeakage, String concreteCrackMovement,
                     String concreteCrackChange, String concreteCrackCondition,
                     String concreteCrackEmergency, String concreteCrackEmergencyAction,
                     String concreteCrackRepairPlan, String leakEfloLeakageRange,
                     String leakEfloLeakageCause, String leakEfloEflorescence,
                     String leakEfloImpact, String leakEfloEmergency,
                     String leakEfloEmergencyAction, String leakEfloRepairPlan,
                     String steelDamageRange, String steelDamageSeverity,
                     String steelDamageCause, String steelDamageStabilityImpact,
                     String steelDamageEmergency, String steelDamageEmergencyAction,
                     String steelDamageRepairPlan, String delaminationRange,
                     String delaminationCause, String delaminationStabilityImpact,
                     String delaminationEmergency, String delaminationEmergencyAction,
                     String delaminationRepairPlan, String rebarExposureRange,
                     String rebarExposureCondition, String rebarExposureCause,
                     String rebarExposureStabilityImpact, String rebarExposureEmergency,
                     String rebarExposureEmergencyAction, String rebarExposureRepairPlan,
                     String paintDamageRange, String paintDamageCause,
                     String paintDamageCondition, String paintDamageEmergency,
                     String paintDamageEmergencyAction, String paintDamageRepairPlan,
                     String overallResult, String monitoringRequired,
                     String nextInspectionDate) {
        this.inspectionId = inspectionId;
        this.inspectionDate = inspectionDate;
        this.inspectorName = inspectorName;
        this.inspectorContact = inspectorContact;
        this.address = address;
        this.defectTypes = defectTypes;
        this.concreteCrackType = concreteCrackType;
        this.concreteCrackLengthCm = concreteCrackLengthCm;
        this.concreteCrackWidthMm = concreteCrackWidthMm;
        this.concreteCrackDepthMm = concreteCrackDepthMm;
        this.concreteCrackLeakage = concreteCrackLeakage;
        this.concreteCrackMovement = concreteCrackMovement;
        this.concreteCrackChange = concreteCrackChange;
        this.concreteCrackCondition = concreteCrackCondition;
        this.concreteCrackEmergency = concreteCrackEmergency;
        this.concreteCrackEmergencyAction = concreteCrackEmergencyAction;
        this.concreteCrackRepairPlan = concreteCrackRepairPlan;
        this.leakEfloLeakageRange = leakEfloLeakageRange;
        this.leakEfloLeakageCause = leakEfloLeakageCause;
        this.leakEfloEflorescence = leakEfloEflorescence;
        this.leakEfloImpact = leakEfloImpact;
        this.leakEfloEmergency = leakEfloEmergency;
        this.leakEfloEmergencyAction = leakEfloEmergencyAction;
        this.leakEfloRepairPlan = leakEfloRepairPlan;
        this.steelDamageRange = steelDamageRange;
        this.steelDamageSeverity = steelDamageSeverity;
        this.steelDamageCause = steelDamageCause;
        this.steelDamageStabilityImpact = steelDamageStabilityImpact;
        this.steelDamageEmergency = steelDamageEmergency;
        this.steelDamageEmergencyAction = steelDamageEmergencyAction;
        this.steelDamageRepairPlan = steelDamageRepairPlan;
        this.delaminationRange = delaminationRange;
        this.delaminationCause = delaminationCause;
        this.delaminationStabilityImpact = delaminationStabilityImpact;
        this.delaminationEmergency = delaminationEmergency;
        this.delaminationEmergencyAction = delaminationEmergencyAction;
        this.delaminationRepairPlan = delaminationRepairPlan;
        this.rebarExposureRange = rebarExposureRange;
        this.rebarExposureCondition = rebarExposureCondition;
        this.rebarExposureCause = rebarExposureCause;
        this.rebarExposureStabilityImpact = rebarExposureStabilityImpact;
        this.rebarExposureEmergency = rebarExposureEmergency;
        this.rebarExposureEmergencyAction = rebarExposureEmergencyAction;
        this.rebarExposureRepairPlan = rebarExposureRepairPlan;
        this.paintDamageRange = paintDamageRange;
        this.paintDamageCause = paintDamageCause;
        this.paintDamageCondition = paintDamageCondition;
        this.paintDamageEmergency = paintDamageEmergency;
        this.paintDamageEmergencyAction = paintDamageEmergencyAction;
        this.paintDamageRepairPlan = paintDamageRepairPlan;
        this.overallResult = overallResult;
        this.monitoringRequired = monitoringRequired;
        this.nextInspectionDate = nextInspectionDate;
        this.status = ChecklistStatus.PENDING;
    }

    public void updateReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
        this.status = ChecklistStatus.COMPLETED;
    }
}



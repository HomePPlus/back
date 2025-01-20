package com.safehouse.api.inspections.schedules.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class RegularScheduleRequestDto {
    @NotNull
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextDate;

    @NotBlank
    private String description;
}
package com.abettaworld.abettaxp.dto;

import com.abettaworld.abettaxp.enums.ExperimentStatus;
import com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExperimentDto {

    @JsonInclude(NON_NULL)
    private String id;

    @NotBlank(message = "The experiment's name cannot be null or empty")
    private String name;

    private ExperimentStatus status;

    @NotBlank(message = "The control group's description name cannot be null or empty")
    private String controlDescription;

    @NotBlank(message = "The experiment group's description cannot be null or empty")
    private String treatmentDescription;

    @JsonInclude(NON_EMPTY)
    private List<MetricDto> experimentMetrics = new ArrayList<>();

    private List<MetricDto> controlMetrics = new ArrayList<>();

    private List<MetricDto> treatmentMetrics = new ArrayList<>();

    private List<RecommendationDto> recommendations = new ArrayList<>();

    public ExperimentDto(Experiment experiment) {
        this.id = experiment.getId();
        this.name = experiment.getName();
        this.status = ExperimentStatus.valueOf(experiment.getStatus().name());
        this.controlDescription = experiment.getDescriptionControl();
        this.treatmentDescription = experiment.getDescriptionTreatment();
        this.controlMetrics = experiment.getMetricsControlList().stream()
                .map(MetricDto::new)
                .collect(Collectors.toList());
        this.treatmentMetrics = experiment.getMetricsTreatmentList().stream()
                .map(MetricDto::new)
                .collect(Collectors.toList());
        this.recommendations = experiment.getRecommendationsList().stream()
                .map(RecommendationDto::new)
                .collect(Collectors.toList());
    }
}

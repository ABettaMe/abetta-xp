package com.abettaworld.abettaxp.service;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.dto.MetricDto.MetricValueDto;

import java.util.List;
import java.util.UUID;

public interface ExperimentService {

    ExperimentDto createExperiment(String userId, ExperimentDto experimentRequest);

    ExperimentDto getExperimentById(UUID experimentId);

    List<ExperimentDto> getExperimentsByUserId(String userId);

    ExperimentDto addExperimentControlMetricRecord(UUID experimentId,
                                                   String metricName,
                                                   MetricValueDto metricValueRequest);

    ExperimentDto addExperimentTreatmentMetricRecord(UUID experimentId,
                                                   String metricName,
                                                   MetricValueDto metricValueRequest);
}

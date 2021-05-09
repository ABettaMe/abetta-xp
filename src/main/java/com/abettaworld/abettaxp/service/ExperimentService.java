package com.abettaworld.abettaxp.service;

import com.abettaworld.abettaxp.dto.ExperimentDto;

import java.util.List;
import java.util.UUID;

public interface ExperimentService {

    ExperimentDto createExperiment(String userId, ExperimentDto experimentRequest);

    ExperimentDto getExperimentById(UUID experimentId);

    List<ExperimentDto> getExperimentsByUserId(String userId);
}

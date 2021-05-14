package com.abettaworld.abettaxp.controller;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.dto.MetricDto.MetricValueDto;
import com.abettaworld.abettaxp.service.ExperimentService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentDto createExperiment(@RequestHeader(value = "userId", required = false) String userId,
                                          @Validated @RequestBody ExperimentDto experimentRequest) {
        return experimentService.createExperiment(userId, experimentRequest);
    }

    @GetMapping
    public List<ExperimentDto> getExperiments(@RequestHeader(value = "userId", required = true) String userId) {
        return experimentService.getExperimentsByUserId(userId);
    }

    @GetMapping("/{experimentId}")
    public ExperimentDto getExperimentById(@NotNull @PathVariable("experimentId") UUID experimentId,
                                           @RequestHeader(value = "userId", required = false) String userId,
                                           @RequestHeader(value = "userEmail", required = false) String userEmail,
                                           @RequestHeader(value = "userFirstName", required = false) String userFirstName,
                                           @RequestHeader(value = "userLastName", required = false) String userLastName,
                                           @RequestHeader(value = "userPictureUrl", required = false) String userPictureUrl) {
        return experimentService.getExperimentById(experimentId);
    }

    @PostMapping("/{experimentId}/control-metrics/{metricName}/records")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentDto addExperimentControlMetricRecord(@NotNull @PathVariable("experimentId") UUID experimentId,
                                                          @NotNull @PathVariable("metricName") String metricName,
                                                          @RequestBody MetricValueDto metricValueRequest) {
        return experimentService.addExperimentControlMetricRecord(experimentId, metricName, metricValueRequest);
    }

    @PostMapping("/{experimentId}/treatment-metrics/{metricName}/records")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentDto addExperimentTreatmentMetricRecord(@NotNull @PathVariable("experimentId") UUID experimentId,
                                                            @NotNull @PathVariable("metricName") String metricName,
                                                            @RequestBody MetricValueDto metricValueRequest) {
        return experimentService.addExperimentTreatmentMetricRecord(experimentId, metricName, metricValueRequest);
    }
}

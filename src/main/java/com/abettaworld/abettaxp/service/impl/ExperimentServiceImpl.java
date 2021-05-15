package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.dto.MetricDto;
import com.abettaworld.abettaxp.dto.MetricDto.MetricValueDto;
import com.abettaworld.abettaxp.exception.ResourceNotFoundException;
import com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment;
import com.abettaworld.abettaxp.proto.MetricOuterClass.Metric;
import com.abettaworld.abettaxp.proto.UserOuterClass.ExperimentInfo;
import com.abettaworld.abettaxp.proto.UserOuterClass.User;
import com.abettaworld.abettaxp.pubsub.publisher.MetricValueRecordedPublisher;
import com.abettaworld.abettaxp.service.ExperimentService;
import com.abettaworld.abettaxp.service.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment.Status.NOT_STARTED;
import static com.abettaworld.abettaxp.util.RedisKeyUtils.getExperimentLookupKey;
import static com.abettaworld.abettaxp.util.RedisKeyUtils.getUserLookupKey;
import static java.text.MessageFormat.format;

@Slf4j
@Service
public class ExperimentServiceImpl implements ExperimentService {

    private final RedisService redisService;

    private final MetricValueRecordedPublisher metricValuePublisher;

    @Autowired
    private ObjectMapper objectMapper;

    public ExperimentServiceImpl(RedisService redisService, MetricValueRecordedPublisher metricValuePublisher) {
        this.redisService = redisService;
        this.metricValuePublisher = metricValuePublisher;
    }

    @Override
    public ExperimentDto createExperiment(String userId, ExperimentDto experimentRequest) {
        Instant timeNow = Instant.now();
        List<Metric> experimentMetrics = experimentRequest.getExperimentMetrics().stream()
                .map(MetricDto::toMetric).collect(Collectors.toList());

        Experiment experiment = Experiment.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName(experimentRequest.getName().trim())
                .setStatus(NOT_STARTED)
                .setDescriptionControl(experimentRequest.getControlDescription().trim())
                .setDescriptionTreatment(experimentRequest.getTreatmentDescription().trim())
                .addAllMetricsControl(experimentMetrics)
                .addAllMetricsTreatment(experimentMetrics)
                .setCreated(Timestamp.newBuilder()
                        .setSeconds(timeNow.getEpochSecond()).setNanos(timeNow.getNano()).build()).build();
        redisService.saveResource(getExperimentLookupKey(experiment), experiment);

        updateUserExperiments(userId, experiment);
        return new ExperimentDto(experiment);
    }

    private void updateUserExperiments(String userId, Experiment experiment) {
        User.Builder userBuilder = User.newBuilder().setId(userId);

        Optional<User.Builder> optionalBuilder =
                redisService.getResourceByLookupKey(getUserLookupKey(userId), User.newBuilder());
        optionalBuilder.ifPresent(builder -> {
            userBuilder.mergeFrom(builder.build());
        });

        userBuilder.addExperiments(ExperimentInfo.newBuilder()
                .setId(experiment.getId())
                .setName(experiment.getName()).build());
        User updatedUser = userBuilder.build();

        redisService.saveResource(getUserLookupKey(userId), updatedUser);
    }

    @Override
    public List<ExperimentDto> getExperimentsByUserId(String userId) {
            String[] userExperimentKeys = getExperimentLookupKeysByUserId(userId);
        List<Experiment> experiments = redisService.getResourcesByLookupKeys(userExperimentKeys).stream()
                .map(experiment -> {
                    try {
                        Experiment.Builder builder = Experiment.newBuilder();
                        JsonFormat.parser().ignoringUnknownFields().merge(experiment, builder);
                        return builder.build();
                    } catch (InvalidProtocolBufferException e) {
                        log.error("Something went wrong when parsing the payload.");
                        throw new RuntimeException("Something went wrong when parsing the payload.");
                    }
                })
                .collect(Collectors.toList());
        return experiments.stream()
                .map(ExperimentDto::new)
                .collect(Collectors.toList());
    }

    private String[] getExperimentLookupKeysByUserId(String userId) {
        String[] lookupKeys;
        Optional<User.Builder> optionalBuilder =
                redisService.getResourceByLookupKey(getUserLookupKey(userId), User.newBuilder());
        return optionalBuilder
                .map(presentBuilder -> {
                    return presentBuilder.getExperimentsList().stream()
                            .map(experimentInfo -> getExperimentLookupKey(experimentInfo.getId()))
                            .toArray(String[]::new);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(format("Could not find experiments for user with id {0}",
                                userId)));
    }

    @Override
    public ExperimentDto getExperimentById(UUID experimentId) {
        Optional<Experiment.Builder> experimentBuilder = redisService.getResourceByLookupKey(
                getExperimentLookupKey(experimentId.toString()), Experiment.newBuilder());
        return experimentBuilder
                .map(builder -> {
                    Experiment experiment = builder.build();
                    return new ExperimentDto(experiment);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(format("Could not find experiment with id {0}",
                                experimentId)));
    }

    @Override
    public ExperimentDto addExperimentControlMetricRecord(UUID experimentId,
                                                          String metricName,
                                                          MetricValueDto metricValueRequest) {

        Optional<Experiment.Builder> optionalBuilder = redisService.getResourceByLookupKey(
                getExperimentLookupKey(experimentId.toString()), Experiment.newBuilder());

        return optionalBuilder.map(presentBuilder -> {
            Experiment.Builder experimentBuilder = Experiment.newBuilder().mergeFrom(presentBuilder.build());
            List<Metric> controlMetrics = experimentBuilder.getMetricsControlList();

            int metricIndex = IntStream.range(0, controlMetrics.size())
                    .filter(i -> controlMetrics.get(i).getName().equals(metricName))
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException(format(
                                    "Could not find metric {0} in experiment with id {1}",
                                    metricName, experimentId
                            )));

            Metric metric = controlMetrics.get(metricIndex);
            Metric.Builder metricBuilder = Metric.newBuilder().mergeFrom(metric);
            metricBuilder.addValue(metricValueRequest.toMetricValue());
            experimentBuilder.setMetricsControl(metricIndex, metricBuilder.build());

            Experiment updatedExperiment = experimentBuilder.build();
            redisService.saveResource(getExperimentLookupKey(updatedExperiment.getId()), updatedExperiment);
            metricValuePublisher.publish(experimentId);
            return new ExperimentDto(updatedExperiment);
        }).orElseThrow(() ->
                new ResourceNotFoundException(format("Could not find experiment with id {0}",
                        experimentId)));
    }

    @Override
    public ExperimentDto addExperimentTreatmentMetricRecord(UUID experimentId,
                                                          String metricName,
                                                          MetricValueDto metricValueRequest) {
        Optional<Experiment.Builder> optionalBuilder = redisService.getResourceByLookupKey(
                getExperimentLookupKey(experimentId.toString()), Experiment.newBuilder());

        return optionalBuilder.map(presentBuilder -> {
            Experiment.Builder experimentBuilder = Experiment.newBuilder().mergeFrom(presentBuilder.build());
            List<Metric> treatmentMetrics = experimentBuilder.getMetricsTreatmentList();

            int metricIndex = IntStream.range(0, treatmentMetrics.size())
                    .filter(i -> treatmentMetrics.get(i).getName().equals(metricName))
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException(format(
                                    "Could not find metric {0} in experiment with id {1}",
                                    metricName, experimentId
                            )));

            Metric metric = treatmentMetrics.get(metricIndex);
            Metric.Builder metricBuilder = Metric.newBuilder().mergeFrom(metric);
            metricBuilder.addValue(metricValueRequest.toMetricValue());
            experimentBuilder.setMetricsTreatment(metricIndex, metricBuilder.build());

            Experiment updatedExperiment = experimentBuilder.build();
            redisService.saveResource(getExperimentLookupKey(updatedExperiment.getId()), updatedExperiment);
            metricValuePublisher.publish(experimentId);
            return new ExperimentDto(updatedExperiment);
        }).orElseThrow(() ->
                new ResourceNotFoundException(format("Could not find experiment with id {0}",
                        experimentId)));
    }
}

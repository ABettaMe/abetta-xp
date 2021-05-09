package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.exception.ResourceNotFoundException;
import com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment;
import com.abettaworld.abettaxp.proto.UserOuterClass.User;
import com.abettaworld.abettaxp.service.ExperimentService;
import com.google.protobuf.Timestamp;
import com.redislabs.modules.rejson.JReJSON;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment.Status.NOT_STARTED;
import static com.abettaworld.abettaxp.util.RedisKeyUtils.getExperimentLookupKey;
import static com.abettaworld.abettaxp.util.RedisKeyUtils.getUserLookupKey;
import static java.text.MessageFormat.format;

@Service
public class ExperimentServiceImpl implements ExperimentService {

    private final JReJSON reJsonClient;

    public ExperimentServiceImpl(JReJSON reJsonClient) {
        this.reJsonClient = reJsonClient;
    }

    @Override
    public ExperimentDto createExperiment(String userId, ExperimentDto experimentRequest) {

        Instant timeNow = Instant.now();

        Experiment experiment = Experiment.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName(experimentRequest.getName().trim())
                .setStatus(NOT_STARTED)
                .setDescriptionControl(experimentRequest.getControlDescription().trim())
                .setDescriptionTreatment(experimentRequest.getTreatmentDescription().trim())
                .setCreated(Timestamp.newBuilder()
                        .setSeconds(timeNow.getEpochSecond()).setNanos(timeNow.getNano()).build())
                .build();

        reJsonClient.set(getExperimentLookupKey(experiment), experiment);

        // todo: refactor
        User persistedUser = reJsonClient.get(getUserLookupKey(userId), User.class);
        User updatedUser = User.newBuilder(persistedUser).addExperimentIds(experiment.getId()).build();
        reJsonClient.set(getUserLookupKey(userId), updatedUser);

        return new ExperimentDto(experiment);
    }

    @Override
    public List<ExperimentDto> getExperimentsByUserId(String userId) {
        try {
            String[] userExperimentIds = getExperimentKeysByUserId(userId);
            List<Experiment> experiments = reJsonClient.mget(Experiment.class, userExperimentIds);
            return experiments.stream()
                    .map(ExperimentDto::new)
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            throw new ResourceNotFoundException(
                    format("Could not find experiments for user with id {0}", userId));
        }
    }

    private String[] getExperimentKeysByUserId(String userId) {
        User user = reJsonClient.get(getUserLookupKey(userId), User.class);
        return user.getExperimentIdsList().toArray(String[]::new);
    }

    @Override
    public ExperimentDto getExperimentById(UUID experimentId) {
        try {
            Experiment experiment = reJsonClient.get(getExperimentLookupKey(experimentId.toString()), Experiment.class);
            return new ExperimentDto(experiment);
        } catch (Exception exception) {
            throw new ResourceNotFoundException(format("Could not find experiment with id {0}", experimentId));
        }
    }
}

package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.proto.UserOuterClass.User;
import com.abettaworld.abettaxp.pubsub.publisher.MetricValueRecordedPublisher;
import com.abettaworld.abettaxp.service.ExperimentService;
import com.abettaworld.abettaxp.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("experiments")
@DisplayName("Experiment service unit tests")
@ExtendWith(SpringExtension.class)
public class ExperimentServiceImplTest {

    @MockBean
    private RedisService redisService;

    @MockBean
    private MetricValueRecordedPublisher metricValuePublisher;

    private ExperimentService experimentService;

    @BeforeEach
    public void setUp() {
        this.experimentService = new ExperimentServiceImpl(redisService, metricValuePublisher);
    }

    @Nested
    @DisplayName("Unit tests that assert the behaviour of createExperiment")
    class CreateExperimentUnitTests {

        @Test
        @DisplayName("Valid experiment request, should persist")
        public void createExperimentWithValidRequestSavesToStorage() {
            User testUser = User.newBuilder().setId("user-1").build();
            when(redisService.getResourceByLookupKey(anyString(), any(Class.class))).thenReturn(Optional.of(testUser));
            ExperimentDto experimentRequest = ExperimentDto.builder()
                    .name("test experiment")
                    .controlDescription("test control description")
                    .treatmentDescription("test treatment description")
                    .experimentMetrics(new ArrayList<>()).build();

            experimentService.createExperiment(testUser.getId(), experimentRequest);

            verify(redisService, times(2)).saveResource(anyString(), any());
        }
    }
}
package com.abettaworld.abettaxp.service.impl;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.proto.UserOuterClass.User;
import com.abettaworld.abettaxp.service.ExperimentService;
import com.redislabs.modules.rejson.JReJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    private JReJSON reJsonClient;

    private ExperimentService experimentService;

    @BeforeEach
    public void setUp() {
        this.experimentService = new ExperimentServiceImpl(reJsonClient);
    }

    @Nested
    @DisplayName("Unit tests that assert the behaviour of createExperiment")
    class CreateExperimentUnitTests {

        @Test
        @DisplayName("Valid experiment request, should persist")
        public void createExperimentWithValidRequestSavesToStorage() {
            User testUser = User.newBuilder().setId("user-1").build();
            when(reJsonClient.get(anyString(), any(Class.class))).thenReturn(testUser);
            ExperimentDto experimentRequest = ExperimentDto.builder()
                    .name("test experiment")
                    .controlDescription("test control description")
                    .treatmentDescription("test treatment description").build();

            experimentService.createExperiment(testUser.getId(), experimentRequest);

            verify(reJsonClient, times(2)).set(anyString(), any());
        }
    }
}
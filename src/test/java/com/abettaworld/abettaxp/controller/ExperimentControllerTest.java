package com.abettaworld.abettaxp.controller;

import com.abettaworld.abettaxp.dto.ExperimentDto;
import com.abettaworld.abettaxp.service.ExperimentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("experiments")
@DisplayName("Web layer test to assert the behaviour of the experiment controller")
@WebMvcTest(ExperimentController.class)
@AutoConfigureMockMvc
public class ExperimentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExperimentService experimentService;

    @Test
    @DisplayName("Valid experiment request, should delegate to service")
    public void createExperimentWithValidRequestDelegatesToService() throws Exception {
        ExperimentDto payload = ExperimentDto.builder()
                .name("test experiment")
                .controlDescription("control description")
                .treatmentDescription("treatment description").build();

        this.mockMvc.perform(post("/v1/experiments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Invalid experiment request, should return bad request")
    public void createExperimentWithInvalidRequestReturnsBadRequest() throws Exception {
        ExperimentDto payload = new ExperimentDto();

        this.mockMvc.perform(post("/v1/experiments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));
    }
}
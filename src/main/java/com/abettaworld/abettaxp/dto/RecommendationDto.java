package com.abettaworld.abettaxp.dto;

import com.abettaworld.abettaxp.proto.RecommendationOuterClass.Recommendation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationDto {

    private String id;

    private Double controlAverage;

    private Double treatmentAverage;

    @JsonProperty("pValue")
    private Double pValue;

    public RecommendationDto(Recommendation recommendation) {
        this.id = recommendation.getId();
        this.controlAverage = recommendation.getAvgControl();
        this.treatmentAverage = recommendation.getAvgTreatment();
        this.pValue = recommendation.getPValue();
    }
}

package com.abettaworld.abettaxp.dto;

import com.abettaworld.abettaxp.proto.MetricOuterClass.Metric;
import com.abettaworld.abettaxp.proto.MetricOuterClass.MetricValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricDto {

    @NotBlank(message = "The metric name cannot be null or empty")
    private String name;

    private String unitValue;

    private List<MetricValueDto> metricValues;

    public MetricDto(Metric metric) {
        this.name = metric.getName();
        this.unitValue = metric.getUnitValue();
        this.metricValues = metric.getValueList().stream()
                .map(MetricValueDto::new)
                .collect(Collectors.toList());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class MetricValueDto {

        private Double value;

        private Instant dateRecorded;

        public MetricValueDto(MetricValue metricValue) {
            this.value = metricValue.getValue();
            this.dateRecorded = Instant.ofEpochSecond(
                    metricValue.getDate().getSeconds(),
                    metricValue.getDate().getNanos()
            );
        }
    }
}

package com.abettaworld.abettaxp.util;

import com.abettaworld.abettaxp.proto.ExperimentOuterClass.Experiment;
import com.abettaworld.abettaxp.proto.MetricOuterClass.Metric;
import com.abettaworld.abettaxp.proto.UserOuterClass.User;
import lombok.experimental.UtilityClass;

import static java.text.MessageFormat.format;

@UtilityClass
public class RedisKeyUtils {

    private static final String USERS_KS = "users";
    private static final String EXPERIMENTS_KS = "experiments";
    private static final String METRICS_KS = "metrics";

    public static String getUserLookupKey(User user) {
        return format("{0}:{1}", USERS_KS, user.getId());
    }

    public static String getUserLookupKey(String userId) {
        return format("{0}:{1}", USERS_KS, userId);
    }

    public static String getExperimentLookupKey(Experiment experiment) {
        return getExperimentLookupKey(experiment.getId());
    }

    public static String getExperimentLookupKey(String experimentId) {
        return format("{0}:{1}", EXPERIMENTS_KS, experimentId);
    }

    public static String getMetricLookupKey(Metric metric) {
        return format("{0}:{1}", METRICS_KS, metric.getName());
    }
}

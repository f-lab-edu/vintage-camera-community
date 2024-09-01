package com.zerozone.vintage.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomMetrics {
    private final MeterRegistry meterRegistry;

    public void incrementCommentAdded() {
        meterRegistry.counter("vintage.comments.added").increment();
    }

    public void incrementApiRequest() {
        meterRegistry.counter("vintage.api.requests").increment();
    }
}
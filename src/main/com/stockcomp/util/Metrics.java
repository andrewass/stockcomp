package com.stockcomp.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class Metrics {

    public final Counter SIGN_UP_USER;

    private Metrics(MeterRegistry meterRegistry) {
        SIGN_UP_USER = meterRegistry.counter("signed.up.user");
    }

}

package com.stockcomp.util

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class Metrics private constructor(meterRegistry: MeterRegistry) {

    val SIGN_UP_USER: Counter = meterRegistry.counter("signed.up.user")

}
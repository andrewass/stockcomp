package com.stockcomp.contest

enum class ContestStatus(
    val decode: String,
) {
    AWAITING_START("Awaiting Start"),
    RUNNING("Running"),
    STOPPED("Stopped"),
    AWAITING_COMPLETION("Awaiting Completion"),
    COMPLETED("Completed"),
    ;

    companion object {
        private val map = entries.associateBy(ContestStatus::decode)

        fun fromDecode(decode: String) = map[decode]
    }
}

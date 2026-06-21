package com.stockcomp.common

fun <T : Comparable<T>> competitionRanksForSortedValues(values: List<T>): List<Int> {
    var previousValue: T? = null
    var currentRank = 0

    return values.mapIndexed { index, value ->
        if (previousValue == null || value != previousValue) {
            currentRank = index + 1
            previousValue = value
        }
        currentRank
    }
}

package com.stockcomp.contest.dto

data class ContestPageDto(
    val contests: List<ContestDto>,
    val totalEntriesCount: Long
)

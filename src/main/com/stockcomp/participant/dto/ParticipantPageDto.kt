package com.stockcomp.participant.dto

data class ParticipantPageDto(
    val participants: List<ParticipantDto>,
    val totalEntriesCount: Long
)

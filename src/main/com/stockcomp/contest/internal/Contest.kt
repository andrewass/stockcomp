package com.stockcomp.contest.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val contestId: Long? = null,

    var startTime: LocalDateTime,

    var endTime: LocalDateTime,

    val contestName: String,

    @Enumerated(EnumType.STRING)
    var contestStatus: ContestStatus = ContestStatus.AWAITING_START,

    ) : BaseEntity() {

    override fun equals(other: Any?): Boolean =
        other is Contest && other.contestName == contestName

    override fun hashCode(): Int {
        return contestName.toInt()
    }
}
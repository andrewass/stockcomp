package com.stockcomp.domain.contest

import com.stockcomp.domain.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val startTime: LocalDateTime,

    val contestNumber: Int,

    var isRunning: Boolean = true,

    var isCompleted: Boolean = false,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val participants: MutableList<Participant> = mutableListOf()

) : BaseEntity() {

    fun startContest() {
        isRunning = true
    }

    fun stopContest() {
        isRunning = false
    }

    fun setCompleted() {
        isCompleted = true
    }
}
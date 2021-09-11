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

    var running: Boolean = false,

    var completed: Boolean = false,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val participants: MutableList<Participant> = mutableListOf()

) : BaseEntity() {

    fun startContest() {
        running = true
    }

    fun stopContest() {
        running = false
    }

    fun completeContest(){
        running = false
        completed = true
    }
}
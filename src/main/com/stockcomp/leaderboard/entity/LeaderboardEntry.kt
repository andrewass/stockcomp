package com.stockcomp.leaderboard.entity

import com.stockcomp.contest.entity.Contest
import com.stockcomp.domain.BaseEntity
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.entity.User
import javax.persistence.*

@Entity
@Table(name = "T_LEADERBOARD_ENTRY")
class LeaderboardEntry(

    @Id
    @Column(name = "LEADERBOARD_ENTRY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var contestCount: Int = 0,

    var ranking: Int = 0,

    var score: Int = 0,

    @OneToMany(mappedBy = "leaderboardEntry", cascade = [CascadeType.ALL])
    val medals: List<Medal> = mutableListOf(),

    @OneToOne
    @JoinColumn(name = "LAST_CONTEST_ID")
    var lastContest: Contest? = null,

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    ) : BaseEntity() {

    fun updateValues(participant: Participant, contest: Contest) {
        val participantScore = participant.rank / contest.participantCount
        score += participantScore
        contestCount += 1
        lastContest = contest
        updateMedals(participant, contest)
    }

    private fun updateMedals(participant: Participant, contest: Contest) {
        val position = getParticipantPercentagePosition(participant, contest)
        getMedalBasedOnPositionPercentage(position)
            ?.also {
                addMedal(
                    Medal(
                        contest = contest,
                        leaderboardEntry = this,
                        medalValue = it,
                        position = participant.rank
                    )
                )
            }
    }

    private fun getMedalBasedOnPositionPercentage(position: Double): MedalValue? {
        return when (position) {
            0.05 -> MedalValue.GOLD
            0.10 -> MedalValue.SILVER
            0.15 -> MedalValue.BRONZE
            else -> null
        }
    }

    private fun getParticipantPercentagePosition(participant: Participant, contest: Contest): Double =
        ((participant.rank - 1) / contest.participantCount).toDouble()


    private fun addMedal(medal: Medal) {
        medals as MutableList
        medals.add(medal)
    }
}
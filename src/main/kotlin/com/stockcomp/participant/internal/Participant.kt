package com.stockcomp.participant.internal

import com.stockcomp.common.BaseEntity
import com.stockcomp.participant.internal.investment.Investment
import com.stockcomp.participant.internal.investmentorder.InvestmentOrder
import jakarta.persistence.*

@Entity
@Table(name = "T_PARTICIPANT")
class Participant(
    @Id
    @Column(name = "PARTICIPANT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val participantId: Long? = null,
    @Column(name = "USER_ID", nullable = false)
    val userId: Long,
    @Column(name = "CONTEST_ID", nullable = false)
    val contestId: Long,
) : BaseEntity() {
    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL], orphanRemoval = true)
    val investmentOrders: MutableList<InvestmentOrder> = mutableListOf()

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL], orphanRemoval = true)
    val investments: MutableList<Investment> = mutableListOf()

    var totalValue: Double = 20000.00

    var totalInvestmentValue: Double = 0.00

    var remainingFunds: Double = 20000.00

    @Column(name = "PARTICIPANT_RANK")
    var rank: Int? = null

    fun getActiveInvestmentOrders(): List<InvestmentOrder> = investmentOrders.filter { it.isActive() }

    fun getCompletedInvestmentOrders(): List<InvestmentOrder> = investmentOrders.filter { it.isCompleted() }

    fun getCompletedInvestmentOrdersForSymbol(symbol: String): List<InvestmentOrder> =
        getCompletedInvestmentOrders().filter { it.symbol == symbol }

    fun getActiveInvestmentOrdersForSymbol(symbol: String): List<InvestmentOrder> =
        getActiveInvestmentOrders().filter { it.symbol == symbol }

    fun getInvestmentsForSymbol(symbol: String): List<Investment> = investments.filter { it.symbol == symbol }

    fun updateParticipantWhenBuying(
        amount: Int,
        symbol: String,
        currentPrice: Double,
    ) {
        val investment = getOrCreateInvestment(symbol)
        investment.updateWhenBuying(amount, currentPrice)
        remainingFunds -= currentPrice * amount
        if (remainingFunds < 0) {
            throw IllegalStateException(
                "Remaining funds for $participantId is $remainingFunds. This value should never be negative",
            )
        }
    }

    fun updateParticipantWhenSelling(
        amount: Int,
        symbol: String,
        currentPrice: Double,
    ) {
        val investment = getInvestment(symbol)
        investment.updateWhenSelling(amount)
        remainingFunds += currentPrice * amount
    }

    fun getInvestmentAmount(symbol: String): Int = investments.firstOrNull { it.symbol == symbol }?.amount ?: 0

    private fun getOrCreateInvestment(symbol: String): Investment =
        investments.firstOrNull { it.symbol == symbol }
            ?: Investment(symbol = symbol, participant = this)
                .also { investments.add(it) }

    private fun getInvestment(symbol: String): Investment = investments.first { it.symbol == symbol }

    fun updateInvestmentValues() {
        val updatedTotalInvestmentsValue = investments.sumOf { it.totalValue }
        totalInvestmentValue = updatedTotalInvestmentsValue
        totalValue = remainingFunds + updatedTotalInvestmentsValue
    }

    fun addInvestmentOrder(investmentOrder: InvestmentOrder) {
        investmentOrders.add(investmentOrder)
    }

    fun removeInvestmentOrder(orderId: Long) {
        investmentOrders.removeIf { it.orderId == orderId }
    }

    fun removeInvestment(investment: Investment) {
        investments.remove(investment)
    }
}

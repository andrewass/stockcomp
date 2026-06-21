package com.stockcomp.participant.internal

import com.stockcomp.common.BaseEntity
import com.stockcomp.participant.internal.investment.Investment
import com.stockcomp.participant.internal.investmentorder.InvestmentOrder
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal

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
    companion object {
        private val INITIAL_FUNDS = BigDecimal("20000.00")
    }

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val investmentOrders: MutableList<InvestmentOrder> = mutableListOf()

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val investments: MutableList<Investment> = mutableListOf()

    @Column(name = "TOTAL_VALUE", nullable = false, precision = 19, scale = 4)
    private var totalValue: BigDecimal = INITIAL_FUNDS

    @Column(name = "TOTAL_INVESTMENT_VALUE", nullable = false, precision = 19, scale = 4)
    private var totalInvestmentValue: BigDecimal = BigDecimal.ZERO

    @Column(name = "REMAINING_FUNDS", nullable = false, precision = 19, scale = 4)
    private var remainingFunds: BigDecimal = INITIAL_FUNDS

    @Column(name = "PARTICIPANT_RANK")
    private var rank: Int? = null

    fun investmentOrders(): List<InvestmentOrder> = investmentOrders.toList()

    fun investments(): List<Investment> = investments.toList()

    fun totalValue(): BigDecimal = totalValue

    fun totalInvestmentValue(): BigDecimal = totalInvestmentValue

    fun remainingFunds(): BigDecimal = remainingFunds

    fun rank(): Int? = rank

    fun assignRank(rank: Int) {
        require(rank > 0) { "Participant rank must be positive" }
        this.rank = rank
    }

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
        currentPrice: BigDecimal,
    ) {
        require(amount > 0) { "Amount must be positive when buying for participant $participantId" }
        require(currentPrice > BigDecimal.ZERO) { "Current price must be positive when buying for participant $participantId" }

        val investment = getOrCreateInvestment(symbol)
        investment.updateWhenBuying(amount, currentPrice)
        remainingFunds = remainingFunds.subtract(currentPrice.multiply(BigDecimal.valueOf(amount.toLong())))
        if (remainingFunds < BigDecimal.ZERO) {
            throw IllegalStateException(
                "Remaining funds for $participantId is $remainingFunds. This value should never be negative",
            )
        }
    }

    fun updateParticipantWhenSelling(
        amount: Int,
        symbol: String,
        currentPrice: BigDecimal,
    ) {
        require(amount > 0) { "Amount must be positive when selling for participant $participantId" }
        require(currentPrice > BigDecimal.ZERO) { "Current price must be positive when selling for participant $participantId" }

        val investment = getInvestment(symbol)
        investment.updateWhenSelling(amount)
        if (investment.amount == 0) {
            removeInvestment(investment)
        }
        remainingFunds = remainingFunds.add(currentPrice.multiply(BigDecimal.valueOf(amount.toLong())))
    }

    fun getInvestmentAmount(symbol: String): Int = investments.firstOrNull { it.symbol == symbol }?.amount ?: 0

    private fun getOrCreateInvestment(symbol: String): Investment =
        investments.firstOrNull { it.symbol == symbol }
            ?: Investment(symbol = symbol, participant = this)
                .also { investments.add(it) }

    private fun getInvestment(symbol: String): Investment =
        investments.firstOrNull { it.symbol == symbol }
            ?: throw IllegalStateException(
                "Participant $participantId does not own an investment for symbol $symbol",
            )

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

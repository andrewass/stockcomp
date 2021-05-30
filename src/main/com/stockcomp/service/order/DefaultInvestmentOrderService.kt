package com.stockcomp.service.order

import com.stockcomp.repository.jpa.InvestmentOrderRepository
import com.stockcomp.response.InvestmentOrderDto
import org.springframework.stereotype.Service

@Service
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository
) : InvestmentOrderService {

    override fun getAllCompletedOrdersForParticipant(): List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }

    override fun getAllCompletedOrdersForSymbolForParticipant(): List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }

    override fun getAllActiveOrders(): List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }

    override fun getAllActiveOrdersForSymbol(): List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }
}
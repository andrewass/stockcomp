package com.stockcomp.service.order

interface OrderProcessingService {
    fun startOrderProcessing()

    fun stopOrderProcessing()
}
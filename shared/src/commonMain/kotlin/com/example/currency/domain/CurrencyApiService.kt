package com.example.currency.domain

import com.example.currency.domain.model.Currency
import com.example.currency.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRate(): RequestState<List<Currency>>
}
package com.example.currency.domain

import com.example.currency.domain.model.Currency
import com.example.currency.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface MongoRepository {
    fun configureRealm()
    suspend fun insertCurrencyData(currency: Currency)
    fun readCurrencyData(): Flow<RequestState<List<Currency>>>
    suspend fun cleanUp()


}
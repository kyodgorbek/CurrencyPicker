package com.example.currency.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.currency.domain.CurrencyApiService
import com.example.currency.domain.MongoRepository
import com.example.currency.domain.PreferencesRepository
import com.example.currency.domain.model.Currency
import com.example.currency.domain.model.RateStatus
import com.example.currency.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


sealed class HomeUiEvent{
    data object RefreshRates:HomeUiEvent()
    data object SwitchCurrencies:HomeUiEvent()
}
class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val mongoDb: MongoRepository,
    private val api:CurrencyApiService
):ScreenModel {
    private var _rateStatus: MutableState<RateStatus> =
        mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies
    var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency
    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()



        }

    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
            is HomeUiEvent.SwitchCurrencies ->{
                screenModelScope.launch {
                    switchCurrencies()
                }
            }
        }
    }



    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value = RequestState.Error(message = "Couldn't find the selected currency.")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value = RequestState.Error(message = "Couldn't find the selected currency.")
                }
            }
        }
    }
    suspend fun fetchNewRates() {
        try {
            val localCache = mongoDb.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HomeViewModel: DATABASE IS FULL")
                    //  _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData())
                    if (!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())) {
                        println("HomeViewModel: DATA NOT FRESH")
                        cacheTheData()
                    } else {
                        println("HomeViewModel: DATA IS FRESH")
                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }


    private suspend fun cacheTheData(){
        val fetchData = api.getLatestExchangeRate()
        if (fetchData.isSuccess()) {
            mongoDb.cleanUp()
            fetchData.getSuccessData().forEach {
                println("HomeViewModel: Adding {${it.code}}")
                mongoDb.insertCurrencyData(it)
            }
            println("HomeViewModel: Updating _allCurrencies")
            _allCurrencies.addAll(fetchData.getSuccessData())
        } else if (fetchData.isError()) {
            println("HomeViewModel: FETCHING FAILED ${fetchData.getErrorMessage()}")
        }
    }
    private suspend fun getRateStatus() {
        _rateStatus.value = if (preferences.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.Stale
    }
    private fun switchCurrencies() {
       val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}
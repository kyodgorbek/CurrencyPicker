package com.example.currency

import com.example.currency.domain.CurrencyApiService
import com.example.currency.domain.MongoRepository
import com.example.currency.domain.PreferencesRepository
import com.example.currency.domain.model.Currency
import com.example.currency.domain.model.RateStatus
import com.example.currency.domain.model.RequestState
import com.example.currency.presentation.screen.HomeUiEvent
import com.example.currency.presentation.screen.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val preferences: PreferencesRepository = mockk(relaxed = true)
    private val mongoDb: MongoRepository = mockk(relaxed = true)
    private val api: CurrencyApiService = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(preferences, mongoDb, api)
    }

    @AfterTest
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `fetchNewRates fetches data when local data is stale`() = runTest(testDispatcher) {
        val usd = Currency().apply { code = "USD"; value = 1.0 }
        val eur = Currency().apply { code = "EUR"; value = 0.85 }
        val mockData = listOf(usd, eur)

        // Setup mocks
        coEvery { mongoDb.readCurrencyData() } returns flowOf(RequestState.Success(mockData))
        coEvery { preferences.isDataFresh(any()) } returns false
        coEvery { api.getLatestExchangeRate() } returns RequestState.Success(mockData)

        // Act: Directly call fetchNewRates if possible or trigger the event
        viewModel.fetchNewRates()

        // Assert
        coVerify(exactly = 1) { api.getLatestExchangeRate() }
        assertEquals(RateStatus.Stale, viewModel.rateStatus.value)
    }

    @Test
    fun `switchCurrencies swaps source and target currencies`() = runTest(testDispatcher) {
        val initialSourceCurrency = Currency().apply {
            code = "USD"
            value = 1.0
        }
        val initialTargetCurrency = Currency().apply {
            code = "EUR"
            value = 0.85
        }

        viewModel._sourceCurrency.value = RequestState.Success(initialSourceCurrency)
        viewModel._targetCurrency.value = RequestState.Success(initialTargetCurrency)

        viewModel.sendEvent(HomeUiEvent.SwitchCurrencies)

        assertEquals("USD", viewModel.sourceCurrency.value.getSuccessData().code)
        assertEquals("EUR", viewModel.targetCurrency.value.getSuccessData().code)
    }

}

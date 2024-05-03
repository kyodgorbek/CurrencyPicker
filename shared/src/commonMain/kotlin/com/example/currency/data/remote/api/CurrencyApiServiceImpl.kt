package com.example.currency.data.remote.api

import com.example.currency.domain.CurrencyApiService
import com.example.currency.domain.model.ApiResponse
import com.example.currency.domain.model.Currency
import com.example.currency.domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiServiceImpl : CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_7fIujvQxlDmWLEjp5mo4VrZDvHZ1dMCqm5kkdhPv"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest) {
            headers {
                append("apikey", API_KEY)
            }
        }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    println("HTTP Client Log: $message")
                }
            }
            level = LogLevel.ALL
        }
    }

    override suspend fun getLatestExchangeRate(): RequestState<List<Currency>> {
        println("Starting request to $ENDPOINT")
        return try {
            val response = httpClient.get(ENDPOINT)
            println("Received response with status: ${response.status}")
            if (response.status.value == 200) {
                val responseBody = response.body<String>()
                println("Response Body: $responseBody")
                val apiResponse = Json.decodeFromString<ApiResponse>(responseBody)
                println("Successfully parsed API response")
                RequestState.Success(data = apiResponse.data.values.toList())
            } else {
                println("Received HTTP error: ${response.status}")
                RequestState.Error(message = "HTTP Error code ${response.status}")
            }
        } catch (e: Exception) {
            println("Exception occurred during HTTP request: ${e.message}")
            RequestState.Error(message = e.message ?: "Unknown error")
        }
    }
}
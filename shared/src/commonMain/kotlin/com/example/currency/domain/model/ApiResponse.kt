package com.example.currency.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ApiResponse(
    val metadata: MetaData,
    val data: Map<String, Currency>
)
@Serializable
data class MetaData(
    @SerialName("last_updated_at")
    val lastUpdatedAt: String
)
@Serializable
data class Currency(

    var code: String,
    var value: Double
)

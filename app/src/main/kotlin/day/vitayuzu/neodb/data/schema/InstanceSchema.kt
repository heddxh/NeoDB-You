package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceSchema(
    val uri: String,
    val title: String,
    val version: String,
    val stats: InstanceStats,
)

@Serializable
data class InstanceStats(
    @SerialName("user_count") val userCount: Int,
)

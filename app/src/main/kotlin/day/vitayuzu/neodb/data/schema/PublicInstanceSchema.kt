package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Schema for public NeoDB instances fetched from api.neodb.app.
 */
@Serializable
data class PublicInstanceSchema(
    val domain: String,
    val version: String,
    val title: String,
    val description: String,
    val languages: List<String> = emptyList(),
    val region: String = "",
    val categories: List<String> = emptyList(),
    @SerialName("proxied_thumbnail") val proxiedThumbnail: String = "",
    val blurhash: String = "",
    @SerialName("total_users") val totalUsers: Int = 0,
    @SerialName("last_week_users") val lastWeekUsers: Int = 0,
    @SerialName("approval_required") val approvalRequired: Boolean = false,
    val language: String = "",
    val category: String = "",
)

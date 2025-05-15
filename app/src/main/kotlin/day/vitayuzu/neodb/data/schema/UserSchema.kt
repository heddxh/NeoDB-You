package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSchema(
    val url: String,
    @SerialName("external_accounts") val externalAccounts: List<ExternalAccountData>,
    @SerialName("display_name") val displayName: String,
    val avatar: String,
    val username: String,
    val roles: List<String> = emptyList(),
)

@Serializable
data class ExternalAccountData(
    val platform: String,
    val handle: String,
    val url: String?,
)

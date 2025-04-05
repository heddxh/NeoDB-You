package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OauthClient(
    @SerialName("client_id") val clientId: String,
    @SerialName("client_secret") val clientSecret: String,
    val name: String,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("vapid_key") val vapidKey: String,
    val website: String
)

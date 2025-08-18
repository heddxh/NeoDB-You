package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TokenSchema(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    val scope: String,
)

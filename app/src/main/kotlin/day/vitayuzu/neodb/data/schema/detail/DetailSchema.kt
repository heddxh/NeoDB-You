package day.vitayuzu.neodb.data.schema.detail

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Base class with common fields for all kinds of detail schema like [EditionSchema]
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface DetailSchema {
    val id: String
    val type: String
    val uuid: String
    val url: String
    val category: EntryType
    val title: String
    val description: String?
    val rating: Float?
    val tags: List<String>?

    @SerialName("api_url") val apiUrl: String

    @SerialName("parent_uuid") val parentUuid: String?

    @SerialName("display_title") val displayTitle: String

    @SerialName("external_resources") val externalResources: List<ExternalResource>?

    @SerialName("localized_title") val localizedTitle: List<LocalizedData>?

    @SerialName("localized_description") val localizedDescription: List<LocalizedData>?

    @SerialName("cover_image_url") val coverImageUrl: String?

    @SerialName("rating_count") val ratingCount: Int?

    @SerialName("rating_distribution") val ratingDistribution: List<Int>?
}

@Serializable
data class LocalizedData(
    val lang: String = "",
    val text: String = "",
)

@Serializable
data class ExternalResource(val url: String)

// search result returns:
// "actor": [
//  {
//    "name": "Dan Resin",
//    "role": ""
//  }
// ]
// detail endpoint returns:
// "actor": ["Dan Resin", "Richard B. Shull"]
object ActorSerializer : JsonTransformingSerializer<List<String>>(
    ListSerializer(String.serializer()),
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonArray) return element

        return buildJsonArray {
            element.forEach { item ->
                when (item) {
                    is JsonPrimitive -> {
                        item.contentOrNull?.let { add(JsonPrimitive(it)) }
                    }

                    is JsonObject -> {
                        item["name"]?.jsonPrimitive?.contentOrNull?.let {
                            add(JsonPrimitive(it))
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

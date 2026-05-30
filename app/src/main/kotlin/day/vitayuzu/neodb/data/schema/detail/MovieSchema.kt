package day.vitayuzu.neodb.data.schema.detail

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
@SerialName("Movie")
data class MovieSchema(
    // Common fields
    override val id: String,
    override val type: String,
    override val uuid: String,
    override val url: String,
    @SerialName("api_url") override val apiUrl: String,
    override val category: EntryType,
    @SerialName("parent_uuid") override val parentUuid: String?,
    @SerialName("display_title") override val displayTitle: String,
    @SerialName("external_resources") override val externalResources: List<ExternalResource>?,
    override val title: String,
    override val description: String?,
    @SerialName("localized_title") override val localizedTitle: List<LocalizedData>?,
    @SerialName("localized_description") override val localizedDescription: List<LocalizedData>?,
    @SerialName("cover_image_url") override val coverImageUrl: String?,
    override val rating: Float?,
    @SerialName("rating_count") override val ratingCount: Int?,
    @SerialName("rating_distribution") override val ratingDistribution: List<Int>?,
    override val tags: List<String>?,
    // Specific fields
    @SerialName("orig_title") val origTitle: String?,
    val director: List<String> = emptyList(),
    val playwright: List<String> = emptyList(),
    @Serializable(with = MovieActorSerializer::class)
    val actor: List<String> = emptyList(),
    val genre: List<String> = emptyList(),
    val language: List<String> = emptyList(),
    val area: List<String> = emptyList(),
    val year: Int?,
    val site: String?,
    val duration: String?,
    val imdb: String?,
) : DetailSchema

// search result returns:
// "actor": [
//  {
//    "name": "Dan Resin",
//    "role": ""
//  }
// ]
// detail endpoint returns:
// "actor": ["Dan Resin", "Richard B. Shull"]
private object MovieActorSerializer : JsonTransformingSerializer<List<String>>(
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

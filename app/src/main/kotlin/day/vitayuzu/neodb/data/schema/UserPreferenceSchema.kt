package day.vitayuzu.neodb.data.schema

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserPreferenceSchema(
    @SerialName("default_crosspost")
    val crossPost: Boolean,
    @SerialName("default_visibility")
    val visibility: Int,
    @SerialName("hidden_categories")
    val hiddenSearchCategories: List<EntryType>,
    val language: String,
)

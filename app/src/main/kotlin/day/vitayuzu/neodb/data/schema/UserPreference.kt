package day.vitayuzu.neodb.data.schema

import day.vitayuzu.neodb.util.EntryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class UserPreference(
    @SerialName("default_crosspost")
    val crossPost: Boolean,
    @SerialName("default_visibility")
    val visibility: Int,
    @SerialName("hidden_categories")
    val hiddenSearchCategories: List<EntryType>,
    val language: String,
) {
    companion object {
        val Default: UserPreference
            get() = UserPreference(
                crossPost = true,
                visibility = 0,
                hiddenSearchCategories = emptyList(),
                language = Locale.getDefault().toLanguageTag(),
            )
    }
}

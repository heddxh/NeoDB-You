package day.vitayuzu.neodb.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release
@Serializable
data class GithubLatestReleaseSchema(
    @SerialName("html_url") val htmlUrl: String,
    @SerialName("tag_name") val tagName: String,
    val body: String,
) {
    companion object {
        val TEST = GithubLatestReleaseSchema(
            htmlUrl = "https://github.com/heddxh/NeoDB-You/releases/tag/v1.0.0",
            tagName = "v9.9",
            body = "",
        )
    }
}

package day.vitayuzu.neodb.data

import day.vitayuzu.neodb.data.model.ShelfType
import day.vitayuzu.neodb.data.schema.PagedMarkSchema
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path

/**
 * Network Api for NeoDB, powered by [Ktorfit](https://github.com/Foso/Ktorfit).
 *
 * It is the main api interface for the app.
 * @
 */
interface NeoDbApi {
    @GET("me/shelf/{type}")
    @Headers("Authorization: Bearer PKFmBt5hcPdjia2zQQoJwUZkAlKWbvUAlipTRvfLaMMXu6a96p1qAxHyDw")
    suspend fun fetchMyShelf(@Path("type") type: ShelfType): PagedMarkSchema
}
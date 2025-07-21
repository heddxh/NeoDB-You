package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.EntrySchema
import day.vitayuzu.neodb.data.schema.TrendingItemSchema
import day.vitayuzu.neodb.data.schema.detail.DetailSchema
import day.vitayuzu.neodb.util.EntryType

/**
 * Entry represents a book/movie/tv etc, used for a card.
 * Should be a subset of [EntrySchema]
 * @param title The name
 * @param category One of [EntryType]
 * @param url NeoDB url
 * @param des Description
 * @param coverUrl Cover image url
 * @param rating Displayed ratings
 */
data class Entry(
    val title: String,
    val category: EntryType,
    val url: String,
    val des: String,
    val coverUrl: String?,
    val rating: Float?,
    val uuid: String,
) {
    constructor(schema: EntrySchema) : this(
        title = schema.displayTitle,
        category = EntryType.valueOf(schema.category),
        url = schema.url,
        des = schema.description,
        coverUrl = schema.coverImageUrl,
        rating = schema.rating,
        uuid = schema.uuid,
    )

    // TODO: merge into EntrySchema
    constructor(schema: TrendingItemSchema) : this(
        title = schema.displayTitle,
        category = EntryType.valueOf(schema.category),
        url = schema.url,
        des = schema.description,
        coverUrl = schema.coverImageUrl,
        rating = schema.rating,
        uuid = schema.uuid,
    )

    constructor(schema: DetailSchema) : this(
        title = schema.displayTitle,
        category = schema.category,
        url = schema.url,
        des = schema.description.toString(),
        coverUrl = schema.coverImageUrl,
        rating = schema.rating,
        uuid = schema.uuid,
    )

    @Suppress("ktlint:standard:max-line-length")
    companion object {
        val TEST = Entry(
            title = "幽灵公主",
            category = EntryType.movie,
            url = "https://neodb.social/movie/3ErINWu9y8qqJzA3uSJxaK",
            des = "为了拯救危难中的村民，阿斯达卡的右手中了凶煞神的诅咒。达卡只好离开亲人往西方流浪以寻找解除诅咒的方法。旅途中他遇到了由幻姬大人带领的穷苦村民在麒麟兽的森林里开采铁矿，提炼矿石。 白狼神莫娜和她养大的人类女孩“幽灵公主”桑对幻姬恨之入骨，因为她们觉得幻姬带领众人破坏了森林。想帮助人类的阿斯达卡被桑深深吸引，他理解她，但为了帮助穷人又不得不和她作战。一次战斗中，阿斯达卡被麒麟兽所救，他的立场更加摇摆不定。 这时，以疙瘩和尚为首的一群人来杀麒麟兽，幻姬以火枪击毙了麒麟，麒麟的头被疙瘩和尚抢走。愤怒的麒麟的灵魂为夺回自己的头，大肆破坏着森林。阿斯达卡和桑联手决定帮麒麟夺回头颅。",
            coverUrl = "https://neodb.social/m/movie/2021/09/134af6fb94-6a16-437a-a52d-19b06470ed4c.jpg",
            rating = 8.9f,
            uuid = "3ErINWu9y8qqJzA3uSJxaK",
        )
    }
}

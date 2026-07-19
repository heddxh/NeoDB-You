package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.EntrySchema
import day.vitayuzu.neodb.data.schema.MarkSchema
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.datetime.LocalDate
import java.util.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MarkTest {

    private lateinit var originalTimeZone: TimeZone

    @BeforeTest
    fun fixTimeZone() {
        originalTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
    }

    @AfterTest
    fun restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone)
    }

    @Test
    fun `created time is converted to a date in the local time zone`() {
        // 2023-01-01T20:00Z is already 2023-01-02 in UTC+8.
        val mark = Mark(markSchema(createdTime = "2023-01-01T20:00:00Z"), preferredLang = "en")
        assertEquals(LocalDate(2023, 1, 2), mark.date)
    }

    @Test
    fun `shelf type is parsed from its raw api name`() {
        val mark = Mark(markSchema(shelfType = "progress"), preferredLang = "en")
        assertEquals(ShelfType.progress, mark.shelfType)
    }

    @Test
    fun `odd rating renders full stars and a half star`() {
        val mark = Mark(markSchema(ratingGrade = 7), preferredLang = "en")
        assertEquals(3, mark.fullStars)
        assertTrue(mark.hasHalfStar)
    }

    @Test
    fun `even rating renders only full stars`() {
        val mark = Mark(markSchema(ratingGrade = 10), preferredLang = "en")
        assertEquals(5, mark.fullStars)
        assertFalse(mark.hasHalfStar)
    }

    @Test
    fun `missing rating renders no stars`() {
        val mark = Mark(markSchema(ratingGrade = null), preferredLang = "en")
        assertEquals(0, mark.fullStars)
        assertFalse(mark.hasHalfStar)
    }

    private fun markSchema(
        createdTime: String = "2023-06-15T12:00:00Z",
        shelfType: String = "complete",
        ratingGrade: Int? = null,
    ) = MarkSchema(
        createdTime = createdTime,
        commentText = null,
        entrySchema = EntrySchema(category = "book", coverImageUrl = null),
        visibility = 0,
        postId = null,
        ratingGrade = ratingGrade,
        shelfType = shelfType,
        tags = null,
    )
}

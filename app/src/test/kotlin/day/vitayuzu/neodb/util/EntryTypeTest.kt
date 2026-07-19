package day.vitayuzu.neodb.util

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class EntryTypeTest {

    @Test
    fun `detail api path maps music to album`() {
        assertEquals("album", EntryType.music.detailApiPath)
        assertEquals("book", EntryType.book.detailApiPath)
        assertEquals("tv", EntryType.tv.detailApiPath)
    }

    // Enum names are used directly as API path segments, so they must stay lowercase.
    @Test
    fun `entry and shelf type names are lowercase`() {
        val names = EntryType.entries.map { it.name } + ShelfType.entries.map { it.name }
        names.forEach { assertEquals(it.lowercase(), it) }
    }

    @Test
    fun `entry type serializes to its plain name`() {
        assertEquals("\"book\"", Json.encodeToString(EntryType.serializer(), EntryType.book))
    }
}

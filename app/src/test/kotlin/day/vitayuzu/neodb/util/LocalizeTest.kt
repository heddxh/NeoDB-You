package day.vitayuzu.neodb.util

import day.vitayuzu.neodb.data.schema.detail.LocalizedData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LocalizeTest {

    @Test
    fun `exact language tag match wins`() {
        val list = listOf(
            LocalizedData("en", "English title"),
            LocalizedData("zh-Hans", "简中标题"),
        )
        assertEquals("简中标题", list.display("zh-Hans"))
    }

    @Test
    fun `same language matches when tags differ`() {
        val list = listOf(
            LocalizedData("en", "English title"),
            LocalizedData("zh", "中文标题"),
        )
        assertEquals("中文标题", list.display("zh-Hans"))
    }

    @Test
    fun `falls back to english when preferred language is missing`() {
        val list = listOf(
            LocalizedData("ja", "日本語タイトル"),
            LocalizedData("en", "English title"),
        )
        assertEquals("English title", list.display("de"))
    }

    @Test
    fun `falls back to first candidate when nothing matches`() {
        val list = listOf(
            LocalizedData("ja", "日本語タイトル"),
            LocalizedData("fr", "Titre français"),
        )
        assertEquals("日本語タイトル", list.display("zh"))
    }

    @Test
    fun `blank text is ignored`() {
        val list = listOf(
            LocalizedData("en", "   "),
            LocalizedData("ja", "日本語タイトル"),
        )
        assertEquals("日本語タイトル", list.display("en"))
    }

    @Test
    fun `invalid language tags are ignored`() {
        val list = listOf(LocalizedData("", "mystery"))
        assertNull(list.display("en"))
    }

    @Test
    fun `empty list yields null`() {
        assertNull(emptyList<LocalizedData>().display("en"))
    }

    @Test
    fun `text is trimmed`() {
        val list = listOf(LocalizedData("en", "  English title  "))
        assertEquals("English title", list.display("en"))
    }
}

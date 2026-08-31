package day.vitayuzu.neodb.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import day.vitayuzu.neodb.data.AppSettings
import day.vitayuzu.neodb.data.ContentLanguageSource
import day.vitayuzu.neodb.ui.model.Detail
import day.vitayuzu.neodb.ui.model.Entry
import day.vitayuzu.neodb.ui.model.Mark
import day.vitayuzu.neodb.ui.model.Post
import day.vitayuzu.neodb.ui.page.detail.DetailContent
import day.vitayuzu.neodb.ui.page.home.TrendingSection
import day.vitayuzu.neodb.ui.page.library.HeatMapBlockType
import day.vitayuzu.neodb.ui.page.library.HeatMapDayData
import day.vitayuzu.neodb.ui.page.library.HeatMapWeekUiState
import day.vitayuzu.neodb.ui.page.library.LibraryContent
import day.vitayuzu.neodb.ui.page.library.LibraryUiState
import day.vitayuzu.neodb.ui.page.onboarding.WelcomePage
import day.vitayuzu.neodb.ui.page.search.SearchPageContent
import day.vitayuzu.neodb.ui.page.settings.SettingsCard
import day.vitayuzu.neodb.ui.theme.NeoDBYouTheme
import day.vitayuzu.neodb.util.EntryType
import day.vitayuzu.neodb.util.ShelfType
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val SCREEN_WIDTH = 360
private const val SCREEN_HEIGHT = 800

@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun OnboardingScreenshot() {
    TestTheme {
        WelcomePage(onNext = {}, onSkip = {})
    }
}

@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun HomeScreenshot() {
    TestTheme {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TrendingSection(sampleEntries(EntryType.movie), EntryType.movie)
            TrendingSection(sampleEntries(EntryType.book), EntryType.book)
            TrendingSection(sampleEntries(EntryType.music), EntryType.music)
        }
    }
}

@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun SearchScreenshot() {
    TestTheme {
        SearchPageContent(
            instanceName = "neodb.social",
            result = sampleEntries(EntryType.movie),
        )
    }
}

@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun LibraryScreenshot() {
    val movie = sampleEntry(EntryType.movie, 0)
    val book = sampleEntry(EntryType.book, 1)
    val marks = listOf(
        sampleMark(movie, "A timeless story about hope and friendship."),
        sampleMark(book, "Beautifully written and worth revisiting."),
    )
    val heatMap = listOf(
        HeatMapWeekUiState(
            index = 0,
            blocks = listOf(
                HeatMapDayData(0, 0, HeatMapBlockType.Single(EntryType.book)),
                HeatMapDayData(
                    0,
                    3,
                    HeatMapBlockType.Double(EntryType.movie, EntryType.tv),
                ),
            ),
        ),
        HeatMapWeekUiState(index = 1, blocks = emptyList()),
    )

    TestTheme {
        LibraryContent(
            uiState = LibraryUiState(
                displayedMarks = marks,
                selectedEntryTypes = persistentSetOf(),
                selectedShelfType = ShelfType.complete,
                heatMap = heatMap,
            ),
        )
    }
}

@OptIn(ExperimentalTime::class)
@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun DetailScreenshot() {
    val entry = sampleEntry(EntryType.movie, 0)
    val detail = Detail(
        type = entry.category,
        title = entry.title,
        coverUrl = null,
        rating = entry.rating,
        info = "Drama / Crime / 1994 / 142 min",
        des = "Two imprisoned men form a friendship over many years and find hope together.",
    )
    val posts = listOf(
        Post(
            avatar = null,
            username = "Alex",
            rating = 10,
            date = Instant.parse("2024-03-12T12:00:00Z"),
            content = "An unforgettable film with a deeply satisfying story.",
        ),
        Post(
            avatar = null,
            username = "Morgan",
            rating = 8,
            date = Instant.parse("2024-01-20T12:00:00Z"),
            content = "Excellent performances and beautiful cinematography.",
        ),
    )

    TestTheme {
        DetailContent(
            detail = detail,
            mark = sampleMark(entry, "One of my all-time favorites."),
            postList = posts,
        )
    }
}

@PreviewTest
@Preview(widthDp = SCREEN_WIDTH, heightDp = SCREEN_HEIGHT, locale = "en")
@Composable
fun SettingsScreenshot() {
    TestTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
        ) {
            SettingsCard(
                settings = AppSettings(
                    homeTrendingTypes = EntryType.entries.take(6),
                    libraryShelfType = ShelfType.complete,
                    contentLanguageSource = ContentLanguageSource.Server,
                    customContentLanguage = "en",
                    checkUpdate = true,
                ),
            )
        }
    }
}

@Composable
private fun TestTheme(content: @Composable () -> Unit) {
    NeoDBYouTheme(dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxSize(), content = content)
    }
}

private fun sampleEntries(type: EntryType): List<Entry> = List(6) { sampleEntry(type, it) }

private fun sampleEntry(type: EntryType, index: Int): Entry = Entry(
    title = when (type) {
        EntryType.book -> "The Left Hand of Darkness"
        EntryType.movie -> "The Shawshank Redemption"
        EntryType.music -> "Kind of Blue"
        else -> "Sample ${type.name}"
    },
    category = type,
    url = "https://neodb.social/${type.name}/$index",
    des = "A stable local description used by screenshot tests.",
    coverUrl = null,
    rating = 8.8f - index / 10f,
    uuid = "${type.name}-$index",
)

private fun sampleMark(entry: Entry, comment: String): Mark = Mark(
    entry = entry,
    shelfType = ShelfType.complete,
    date = LocalDate(2024, 1, 15),
    rating = 9,
    comment = comment,
)

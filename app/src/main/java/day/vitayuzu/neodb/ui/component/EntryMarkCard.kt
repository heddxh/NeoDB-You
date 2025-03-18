package day.vitayuzu.neodb.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import day.vitayuzu.neodb.R
import day.vitayuzu.neodb.data.model.Entry
import day.vitayuzu.neodb.data.model.EntryType
import day.vitayuzu.neodb.data.model.Mark
import day.vitayuzu.neodb.data.model.ShelfType
import day.vitayuzu.neodb.ui.theme.ratingColor
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalCoilApi::class)
@Composable
fun EntryMarkCard(entry: Entry, mark: Mark?, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.background
        ), modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier // First child that emit UI, so accept pass-in modifier.
                .height(160.dp)
                .padding(8.dp)
        ) {
            val previewHandler = AsyncImagePreviewHandler {
                ColorImage(Color.LightGray.toArgb())
            }
            CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
                AsyncImage(
                    model = entry.coverUrl,
                    contentDescription = "Cover image of ${entry.title}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(114.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = entry.title,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(entry.category.toR()),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.alpha(0.5f)
                        )
                    }
                    if (entry.rating != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            StarIcon()
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                entry.rating.toString(),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.des,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (mark != null) UserMark(mark)
            }
        }
    }
}

@Composable
fun UserMark(mark: Mark, modifier: Modifier = Modifier) {
    OutlinedCard(shape = MaterialTheme.shapes.small, modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 20.5.dp)
            ) {
                if (mark.rating != null) {
                    RatingStars(mark.fullStars, mark.hasHalfStar)
                }
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        mark.date.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alignByBaseline()
                    )
                    Text(
                        stringResource(mark.shelfType.toR()),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alignByBaseline()
                    )
                }
            }
            if (mark.comment != null) Text(
                text = mark.comment,
                style = MaterialTheme.typography.bodySmall,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RatingStars(full: Int, half: Boolean, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(full) {
            StarIcon()
        }
        if (half) {
            StarIcon(isHalf = true)
        }
    }
}

@Composable
fun StarIcon(modifier: Modifier = Modifier, isHalf: Boolean = false) {
    if (isHalf) {
        Icon(
            painterResource(R.drawable.star_half),
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier
                .size(16.dp)
                .offset(y = (-0.5).dp) // Visually center
        )
    } else {
        Icon(
            Icons.Filled.Star,
            tint = MaterialTheme.colorScheme.ratingColor,
            contentDescription = null,
            modifier = modifier
                .size(16.dp)
                .offset(y = (-0.5).dp) // Visually center
        )
    }
}

@Preview
@Composable
fun UserMarkPreview(
//    @PreviewParameter(UserMarkPreviewDataProvider::class) mark: Mark
) {
    UserMark(dumpMark)
}

@Preview
@Composable
fun EntryCardPreview(
    @PreviewParameter(EntryCardPreviewDataProvider::class) entry: Entry
) {
    EntryMarkCard(entry = entry, mark = dumpMark)
}

private class EntryCardPreviewDataProvider : PreviewParameterProvider<Entry> {
    private val entryDumpFull = Entry(
        title = "十三机兵防卫圈",
        category = EntryType.Movie,
        coverUrl = "https://www.figma.com/file/pnexnznliZacGHbV45utnX/image/2cab87f3e7d82d7be03f16df26f580392d4632a7",
        des = "《十三机兵防卫圈》是制作《胧村正》和《圣骑士物语》两大作品的 Vanillaware 小组操刀制作的全新原创  IP，游戏继承了工作室一贯的水彩画风，精美的 2D  手绘风格让人着迷。游戏仍然是横版玩法，加入了机械等未来朋克元素，相信会给喜欢该工作室的玩家带来全新的游戏体验。",
        url = "https://www.baidu.com",
        rating = 4.5f,
    )

    private val entryDumpNull = Entry(
        title = "Hello titlefoooooooooBarrrrrrrrsakisakisaki",
        category = EntryType.Movie,
        coverUrl = "https://www.figma.com/file/pnexnznliZacGHbV45utnX/image/2cab87f3e7d82d7be03f16df26f580392d4632a7",
        des = "《十三机兵防卫圈》是制作《胧村正》和《圣骑士物语》两大作品的 Vanillaware 小组操刀制作的全新原创  IP，游戏继承了工作室一贯的水彩画风，精美的 2D  手绘风格让人着迷。游戏仍然是横版玩法，加入了机械等未来朋克元素，相信会给喜欢该工作室的玩家带来全新的游戏体验。",
        url = "https://www.baidu.com",
        rating = null
    )
    override val values = sequenceOf(
        entryDumpFull, entryDumpNull
    )
}

private val dumpMark = Mark(
    Entry(
        title = "十三机兵防卫圈",
        category = EntryType.Movie,
        coverUrl = "https://www.figma.com/file/pnexnznliZacGHbV45utnX/image/2cab87f3e7d82d7be03f16df26f580392d4632a7",
        des = "《十三机兵防卫圈》是制作《胧村正》和《圣骑士物语》两大作品的 Vanillaware 小组操刀制作的全新原创  IP，游戏继承了工作室一贯的水彩画风，精美的 2D  手绘风格让人着迷。游戏仍然是横版玩法，加入了机械等未来朋克元素，相信会给喜欢该工作室的玩家带来全新的游戏体验。",
        url = "https://www.baidu.com",
        rating = 4.5f,
    ),
    ShelfType.Completed,
    Instant.parse("2025-01-31T12:33:37.976Z").toLocalDateTime(TimeZone.currentSystemDefault()).date,
    9,
    "结尾致谢有墨田区观光协会，特别想去隅田川边逛一逛。\r\n\r\n搜到的一个解读：https://forum.gamer.com.tw/C.php?bsn=76911&snA=6",
)
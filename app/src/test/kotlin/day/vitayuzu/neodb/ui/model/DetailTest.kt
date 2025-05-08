@file:Suppress("ktlint:standard:property-naming")

package day.vitayuzu.neodb.ui.model

import day.vitayuzu.neodb.data.schema.detail.AlbumSchema
import day.vitayuzu.neodb.data.schema.detail.EditionSchema
import day.vitayuzu.neodb.data.schema.detail.GameSchema
import day.vitayuzu.neodb.data.schema.detail.MovieSchema
import day.vitayuzu.neodb.data.schema.detail.PerformanceSchema
import day.vitayuzu.neodb.data.schema.detail.PodcastSchema
import day.vitayuzu.neodb.data.schema.detail.TVSeasonSchema
import day.vitayuzu.neodb.data.schema.detail.TVShowSchema
import kotlinx.serialization.json.Json
import org.junit.Test

val decode = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
}

class DetailTest {

    @Test fun editionToDetail() {
        val editionSchema: EditionSchema = decode.decodeFromString(bookJson)
        val detail = editionSchema.toDetail()
        assert(detail.info == "椎名 うみ / 講談社 / 2017 / 青野くんに触りたいから死にたい")
    }

    @Test fun gameToDetail() {
        val gameSchema: GameSchema = decode.decodeFromString(gameJson)
        val detail = gameSchema.toDetail()
        println(detail.info)
        assert(
            detail.info ==
                "Squaresoft / Square Visual Works / 角色扮演 / PC / iPhone / iPad / Android / PS4 / 1997-01-31",
        )
    }

    @Test fun movieToDetail() {
        val movieSchema: MovieSchema = decode.decodeFromString(movieJson)
        val detail = movieSchema.toDetail()
        println(detail.info)
        assert(
            detail.info ==
                "宫崎骏 / 松田洋治 / 石田百合子 / 田中裕子 / 小林薰 / Animation / Fantasy / Adventure / 日本 / 1997 / 134分钟",
        )
    }

    @Test fun tvShowToDetail() {
        val tvShowSchema: TVShowSchema = decode.decodeFromString(tvShowJson)
        val detail = tvShowSchema.toDetail()
        assert(detail.info == "小泉今日子 / 小林聪美 / 冈光子 / 杉本哲太 / 桥爪功 / 剧情 / 2024")
    }

    @Test fun tvSeasonToDetail() {
        val tvSeasonSchema: TVSeasonSchema = decode.decodeFromString(tvSeasonJson)
        val detail = tvSeasonSchema.toDetail()
        assert(detail.info == "吉田纪子 / 藤野千夜 / 小泉今日子 / 小林聪美 / 丘光子 / 剧情 / 日本 / 2024")
    }

    @Test fun albumToDetail() {
        val albumSchema: AlbumSchema = decode.decodeFromString(albumJson)
        val detail = albumSchema.toDetail()
        assert(
            detail.info ==
                "Poppin'Party / 2018 BanG Dream! Project/Craft Egg Inc./bushiroad / 2018 BUSHIROAD MUSIC Inc. / 2018-10-03",
        )
    }

    @Test fun podcastToDetail() {
        val podcastSchema: PodcastSchema = decode.decodeFromString(podcastJson)
        val detail = podcastSchema.toDetail()
        assert(detail.info == "Arts / Design / The Type")
    }

    @Test fun performanceToDetail() {
        val performanceSchema: PerformanceSchema = decode.decodeFromString(performanceJson)
        val detail = performanceSchema.toDetail()
        assert(
            detail.info ==
                "音乐剧 / Harry Kupfer / Michael Kunze / Pia Douwes / Uwe Kröger / Ethan Freeman / Viktor Gernot / Andreas Bieber",
        )
    }
}

const val bookJson = """
{
  "id": "https://neodb.social/book/3Ua13L0YrhQ0nlGUG1B1aD",
  "type": "Edition",
  "uuid": "3Ua13L0YrhQ0nlGUG1B1aD",
  "url": "/book/3Ua13L0YrhQ0nlGUG1B1aD",
  "api_url": "/api/book/3Ua13L0YrhQ0nlGUG1B1aD",
  "category": "book",
  "parent_uuid": null,
  "display_title": "青野くんに触りたいから死にたい",
  "external_resources": [
    {
      "url": "https://book.douban.com/subject/27077219/"
    }
  ],
  "title": "青野くんに触りたいから死にたい",
  "description": "君に触れるなら、死んでもいいよ。これがわたしの愛なんだ。アフタヌーン公式サイト「モアイ」掲載の1話が300000PV突破! 話題の「青野くん」がついに単行本化! 天然少女・優里ちゃんと、その彼氏・青野くん。ごく普通のお付き合いをしていたふたりだが、ある日突然、青野くんが「いなくなって」しまう……。絶対に結ばれないし、触れ合えないふたりの、でたらめで切実すぎるラブ・ストーリー。",
  "localized_title": [
    {
      "lang": "ja",
      "text": "青野くんに触りたいから死にたい"
    }
  ],
  "localized_description": [
    {
      "lang": "ja",
      "text": "君に触れるなら、死んでもいいよ。これがわたしの愛なんだ。アフタヌーン公式サイト「モアイ」掲載の1話が300000PV突破! 話題の「青野くん」がついに単行本化! 天然少女・優里ちゃんと、その彼氏・青野くん。ごく普通のお付き合いをしていたふたりだが、ある日突然、青野くんが「いなくなって」しまう……。絶対に結ばれないし、触れ合えないふたりの、でたらめで切実すぎるラブ・ストーリー。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/book/2021/12/17d8dd0b4e-ad5e-4bee-86c7-72d5602d7801.jpg",
  "rating": 9,
  "rating_count": 6,
  "rating_distribution": [
    0,
    0,
    0,
    50,
    50
  ],
  "tags": [
    "manga",
    "日本",
    "日本漫画",
    "椎名うみ",
    "漫画"
  ],
  "brief": "君に触れるなら、死んでもいいよ。これがわたしの愛なんだ。アフタヌーン公式サイト「モアイ」掲載の1話が300000PV突破! 話題の「青野くん」がついに単行本化! 天然少女・優里ちゃんと、その彼氏・青野くん。ごく普通のお付き合いをしていたふたりだが、ある日突然、青野くんが「いなくなって」しまう……。絶対に結ばれないし、触れ合えないふたりの、でたらめで切実すぎるラブ・ストーリー。",
  "subtitle": null,
  "orig_title": "",
  "author": [
    "椎名 うみ"
  ],
  "translator": [],
  "language": [],
  "pub_house": "講談社",
  "pub_year": 2017,
  "pub_month": 6,
  "binding": "コミック",
  "price": "",
  "pages": 200,
  "series": "青野くんに触りたいから死にたい",
  "imprint": null,
  "isbn": "9784063882728"
}"""

const val gameJson = """
{
  "id": "https://neodb.social/game/4ux4RRO5BcpJMLPFEAVObe",
  "type": "Game",
  "uuid": "4ux4RRO5BcpJMLPFEAVObe",
  "url": "/game/4ux4RRO5BcpJMLPFEAVObe",
  "api_url": "/api/game/4ux4RRO5BcpJMLPFEAVObe",
  "category": "game",
  "parent_uuid": null,
  "display_title": "最终幻想7",
  "external_resources": [
    {
      "url": "https://www.douban.com/game/10801294/"
    }
  ],
  "title": "最终幻想7",
  "description": "《最终幻想7》（英文名：Final Fantasy VII，日文名：ファイナルファンタジー VII）是同名电视游戏的第七作。史克威尔于1996年1月宣布此作改发售于PlayStation，为当时的主机战争投下了一个震撼弹。1997年1月31日推出了正式版本，在发售同年，也被翻译成多国语言版本，其中最著名的当属同年出版的国际版。《最终幻想7》以其感人的剧情、唯美的音乐、人性化的人设、出色的系统，成为当年乃至整个最终幻想系列中一大难以逾越的经典。\r\n     此游戏可说是长篇巨著，要从头玩到尾要80个小时。游戏中创造了大量豪华的图象 ，过场动画约1个小时，画面经过3D加速处理，播放速度在每秒钟30帧以上，十分流畅。游 戏第一个采用雅马哈的全新XG音效。游戏中穿插着不少迷你游戏，这些游戏中的游戏也很吸引人。",
  "localized_title": [
    {
      "lang": "zh-cn",
      "text": "最终幻想7"
    },
    {
      "lang": "en",
      "text": "FF7"
    },
    {
      "lang": "de",
      "text": "FINAL FANTASY VII"
    }
  ],
  "localized_description": [
    {
      "lang": "zh-cn",
      "text": "《最终幻想7》（英文名：Final Fantasy VII，日文名：ファイナルファンタジー VII）是同名电视游戏的第七作。史克威尔于1996年1月宣布此作改发售于PlayStation，为当时的主机战争投下了一个震撼弹。1997年1月31日推出了正式版本，在发售同年，也被翻译成多国语言版本，其中最著名的当属同年出版的国际版。《最终幻想7》以其感人的剧情、唯美的音乐、人性化的人设、出色的系统，成为当年乃至整个最终幻想系列中一大难以逾越的经典。\r\n     此游戏可说是长篇巨著，要从头玩到尾要80个小时。游戏中创造了大量豪华的图象 ，过场动画约1个小时，画面经过3D加速处理，播放速度在每秒钟30帧以上，十分流畅。游 戏第一个采用雅马哈的全新XG音效。游戏中穿插着不少迷你游戏，这些游戏中的游戏也很吸引人。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/item/doubangame/2024/07/16/6a556213-10a2-4c9d-8090-176d172fcb69.jpg",
  "rating": 9.5,
  "rating_count": 22,
  "rating_distribution": [
    0,
    0,
    0,
    22,
    77
  ],
  "tags": [
    "1990s",
    "rpg",
    "日本",
    "最终幻想"
  ],
  "brief": "《最终幻想7》（英文名：Final Fantasy VII，日文名：ファイナルファンタジー VII）是同名电视游戏的第七作。史克威尔于1996年1月宣布此作改发售于PlayStation，为当时的主机战争投下了一个震撼弹。1997年1月31日推出了正式版本，在发售同年，也被翻译成多国语言版本，其中最著名的当属同年出版的国际版。《最终幻想7》以其感人的剧情、唯美的音乐、人性化的人设、出色的系统，成为当年乃至整个最终幻想系列中一大难以逾越的经典。\r\n     此游戏可说是长篇巨著，要从头玩到尾要80个小时。游戏中创造了大量豪华的图象 ，过场动画约1个小时，画面经过3D加速处理，播放速度在每秒钟30帧以上，十分流畅。游 戏第一个采用雅马哈的全新XG音效。游戏中穿插着不少迷你游戏，这些游戏中的游戏也很吸引人。",
  "genre": [
    "角色扮演"
  ],
  "developer": [
    "Squaresoft",
    "Square Visual Works"
  ],
  "publisher": [
    "Squaresoft"
  ],
  "platform": [
    "PC",
    "iPhone",
    "iPad",
    "Android",
    "PS4",
    "Xbox One",
    "Nintendo Switch",
    "PS"
  ],
  "release_type": null,
  "release_date": "1997-01-31",
  "official_site": ""
}
"""

const val movieJson = """
{
  "id": "https://neodb.social/movie/3ErINWu9y8qqJzA3uSJxaK",
  "type": "Movie",
  "uuid": "3ErINWu9y8qqJzA3uSJxaK",
  "url": "/movie/3ErINWu9y8qqJzA3uSJxaK",
  "api_url": "/api/movie/3ErINWu9y8qqJzA3uSJxaK",
  "category": "movie",
  "parent_uuid": null,
  "display_title": "幽灵公主",
  "external_resources": [
    {
      "url": "https://eggplant.place/movie/2SgxPIOEyfoSl5NP1WuZzE"
    },
    {
      "url": "https://www.imdb.com/title/tt0119698/"
    },
    {
      "url": "https://www.themoviedb.org/movie/128"
    },
    {
      "url": "https://minreol.dk/movie/39rGx3EXoJjKeplNDw82HU"
    },
    {
      "url": "https://bgm.tv/subject/310"
    },
    {
      "url": "https://movie.douban.com/subject/1297359/"
    }
  ],
  "title": "幽灵公主",
  "description": "为了拯救危难中的村民，阿斯达卡的右手中了凶煞神的诅咒。达卡只好离开亲人往西方流浪以寻找解除诅咒的方法。旅途中他遇到了由幻姬大人带领的穷苦村民在麒麟兽的森林里开采铁矿，提炼矿石。 白狼神莫娜和她养大的人类女孩“幽灵公主”桑对幻姬恨之入骨，因为她们觉得幻姬带领众人破坏了森林。想帮助人类的阿斯达卡被桑深深吸引，他理解她，但为了帮助穷人又不得不和她作战。一次战斗中，阿斯达卡被麒麟兽所救，他的立场更加摇摆不定。 这时，以疙瘩和尚为首的一群人来杀麒麟兽，幻姬以火枪击毙了麒麟，麒麟的头被疙瘩和尚抢走。愤怒的麒麟的灵魂为夺回自己的头，大肆破坏着森林。阿斯达卡和桑联手决定帮麒麟夺回头颅。",
  "localized_title": [
    {
      "lang": "zh-cn",
      "text": "幽灵公主"
    },
    {
      "lang": "ja",
      "text": "もののけ姫"
    },
    {
      "lang": "zh-cn",
      "text": "幽灵少女"
    },
    {
      "lang": "zh-cn",
      "text": "物之怪姬"
    },
    {
      "lang": "en",
      "text": "Mononoke-hime"
    },
    {
      "lang": "en",
      "text": "Princess Mononoke"
    },
    {
      "lang": "zh-hk",
      "text": "幽靈公主"
    },
    {
      "lang": "zh-tw",
      "text": "魔法公主"
    },
    {
      "lang": "fr",
      "text": "Princesse Mononoké"
    },
    {
      "lang": "de",
      "text": "Prinzessin Mononoke"
    },
    {
      "lang": "nn",
      "text": "もののけ姫"
    },
    {
      "lang": "nb",
      "text": "Prinsesse Mononoke"
    },
    {
      "lang": "no",
      "text": "Prinsesse Mononoke"
    },
    {
      "lang": "sv",
      "text": "Prinsessan Mononoke"
    },
    {
      "lang": "da",
      "text": "Prinsesse Mononoke"
    },
    {
      "lang": "zh-cn",
      "text": "魔法公主"
    },
    {
      "lang": "zh-cn",
      "text": "山神的女儿"
    },
    {
      "lang": "sl",
      "text": "Mononoke Hime"
    }
  ],
  "localized_description": [
    {
      "lang": "zh-cn",
      "text": "为了拯救危难中的村民，阿斯达卡的右手中了凶煞神的诅咒。达卡只好离开亲人往西方流浪以寻找解除诅咒的方法。旅途中他遇到了由幻姬大人带领的穷苦村民在麒麟兽的森林里开采铁矿，提炼矿石。 白狼神莫娜和她养大的人类女孩“幽灵公主”桑对幻姬恨之入骨，因为她们觉得幻姬带领众人破坏了森林。想帮助人类的阿斯达卡被桑深深吸引，他理解她，但为了帮助穷人又不得不和她作战。一次战斗中，阿斯达卡被麒麟兽所救，他的立场更加摇摆不定。 这时，以疙瘩和尚为首的一群人来杀麒麟兽，幻姬以火枪击毙了麒麟，麒麟的头被疙瘩和尚抢走。愤怒的麒麟的灵魂为夺回自己的头，大肆破坏着森林。阿斯达卡和桑联手决定帮麒麟夺回头颅。"
    },
    {
      "lang": "fr",
      "text": "Parti en quête d'un remède, un prince affecté par un mal fatal se retrouve dans une contrée où se livre une bataille entre une ville minière et les animaux de la forêt."
    },
    {
      "lang": "de",
      "text": "Japan im frühen Mittelalter. Der junge Krieger Ashitaka tötet in Notwehr einen dämonischen Eber und wird darauf mit einem Fluch belegt. Auf der Suche nach Heilung durchstreift Ashitaka das Land und stößt schließlich nahe einer befestigten Hüttensiedlung, die sich tief in das einst idyllische Land um einen Heiligen Berg frisst, auf die von Wölfen großgezogene Kriegerin San, genannt Mononoke. Obwohl San einen Krieg für die Natur wider ihre Zerstörer führt, verliebt sie sich in Ashitaka."
    },
    {
      "lang": "nb",
      "text": "Mens han beskytter sin landsby mot en demon, blir den modige unge krigeren Ashitaka rammet av en dødelig forbannelse. For å redde livet må han reise til skogene i vest. Når han ankommer dit blir han involvert i en voldsom kamp der menneskene prøver å overta skogen. Den ambisiøse Lady Eboshi og hennes lojale klansmenn tar alle våpen i bruk mot skogsgudene og en modig ung kvinne, Prinsesse Mononoke."
    },
    {
      "lang": "no",
      "text": "Mens han beskytter sin landsby mot en demon, blir den modige unge krigeren Ashitaka rammet av en dødelig forbannelse. For å redde livet må han reise til skogene i vest. Når han ankommer dit blir han involvert i en voldsom kamp der menneskene prøver å overta skogen. Den ambisiøse Lady Eboshi og hennes lojale klansmenn tar alle våpen i bruk mot skogsgudene og en modig ung kvinne, Prinsesse Mononoke."
    },
    {
      "lang": "sv",
      "text": "Efter att ha räddat sin hemby från en attack av en demon ser den unge Ashitaka hur något som liknar svarta tentakler virvlar längs hans arm. Under kampen har han själv blivit drabbad av samma förbannelse som drabbat den vildsvinsgud som utförde attacken. Om han inte lyckas lösa förbannelsen kommer den snart att ta över hela hans väsen, och för varje gång han känner ilska eller hat, kommer den växa i styrka."
    },
    {
      "lang": "en",
      "text": "Ashitaka, a prince of the disappearing Emishi people, is cursed by a demonized boar god and must journey to the west to find a cure. Along the way, he encounters San, a young human woman fighting to protect the forest, and Lady Eboshi, who is trying to destroy it. Ashitaka must find a way to bring balance to this conflict."
    },
    {
      "lang": "da",
      "text": "Ashitaka bliver bidt af en dæmon og får en grim infektion. Derfor begiver han sig afsted for at finde guden Shishigami, der efter sigende kan helbrede ham. Undervejs oplever han den åbne konflikt mellem skovens gamle guder. Han er også vidne til minelandsbyens udnyttelser af jordens ressourcer. Ashitaka prøver at mægle, men uden større held."
    },
    {
      "lang": "zh-cn",
      "text": "大和王权时期蝦夷族末代王子阿席达卡，在保卫他的村庄时被一恶魔诅咒受不治创伤。村里的巫婆劝他前往西方，寻找恶魔的来源和解除诅咒的方法。在他的旅途中，他來到山獸神森林，认识了被称为“魔法公主”的、由犬神抚养大的小桑。\r\n\r\n在這個過程中，阿席達卡发现恶魔本来是一个山猪神，但体内中了一颗铁弹后，对人类憎恨无比，变成了恶魔。铁弹是工業之城達達拉的統治者幻姬领导的穷人在铁城里制造的，为了保护自己不被贪心的封建贵族吞并，幻姬使用火器和铁弹作为武器，而为了制造火器和铁弹，她又必须从大自然中开发木头和铁矿石，由此与原始森林中的兽神交恶。\r\n\r\n阿席达卡试图在双方调停，但却越来越深地被牵入了这场冲突。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/movie/2021/09/134af6fb94-6a16-437a-a52d-19b06470ed4c.jpg",
  "rating": 8.9,
  "rating_count": 1366,
  "rating_distribution": [
    0,
    0,
    7,
    34,
    57
  ],
  "tags": [
    "1990s",
    "1997",
    "アニメ",
    "人性",
    "冒险",
    "动漫",
    "动画",
    "动画片",
    "动画电影",
    "吉卜力",
    "奇幻",
    "宫崎峻",
    "宫崎骏",
    "宮崎駿",
    "幽灵公主",
    "日本",
    "日本动画",
    "日本电影",
    "电影",
    "经典"
  ],
  "brief": "为了拯救危难中的村民，阿斯达卡的右手中了凶煞神的诅咒。达卡只好离开亲人往西方流浪以寻找解除诅咒的方法。旅途中他遇到了由幻姬大人带领的穷苦村民在麒麟兽的森林里开采铁矿，提炼矿石。 白狼神莫娜和她养大的人类女孩“幽灵公主”桑对幻姬恨之入骨，因为她们觉得幻姬带领众人破坏了森林。想帮助人类的阿斯达卡被桑深深吸引，他理解她，但为了帮助穷人又不得不和她作战。一次战斗中，阿斯达卡被麒麟兽所救，他的立场更加摇摆不定。 这时，以疙瘩和尚为首的一群人来杀麒麟兽，幻姬以火枪击毙了麒麟，麒麟的头被疙瘩和尚抢走。愤怒的麒麟的灵魂为夺回自己的头，大肆破坏着森林。阿斯达卡和桑联手决定帮麒麟夺回头颅。",
  "orig_title": "もののけ姫",
  "director": [
    "宫崎骏"
  ],
  "playwright": [
    "宫崎骏"
  ],
  "actor": [
    "松田洋治",
    "石田百合子",
    "田中裕子",
    "小林薰",
    "西村雅彦",
    "上条恒彦",
    "岛本须美",
    "渡边哲",
    "佐藤允",
    "名古屋章",
    "美轮明宏",
    "森光子",
    "森繁久弥"
  ],
  "genre": [
    "Animation",
    "Fantasy",
    "Adventure"
  ],
  "language": [
    "日语"
  ],
  "area": [
    "日本"
  ],
  "year": 1997,
  "site": "http://www.ntv.co.jp/ghibli/",
  "duration": "134分钟",
  "imdb": "tt0119698"
}
"""

const val tvShowJson = """
{
  "id": "https://neodb.social/tv/1KaJRdpEB6kRPqqKcSDfMs",
  "type": "TVShow",
  "uuid": "1KaJRdpEB6kRPqqKcSDfMs",
  "url": "/tv/1KaJRdpEB6kRPqqKcSDfMs",
  "api_url": "/api/tv/1KaJRdpEB6kRPqqKcSDfMs",
  "category": "tv",
  "parent_uuid": null,
  "display_title": "住宅区的两人",
  "external_resources": [
    {
      "url": "https://www.themoviedb.org/tv/255316"
    }
  ],
  "title": "住宅区的两人",
  "description": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。",
  "localized_title": [
    {
      "lang": "ko",
      "text": "団地のふたり"
    },
    {
      "lang": "ja",
      "text": "団地のふたり"
    },
    {
      "lang": "pt",
      "text": "団地のふたり"
    },
    {
      "lang": "de",
      "text": "団地のふたり"
    },
    {
      "lang": "fr",
      "text": "団地のふたり"
    },
    {
      "lang": "es",
      "text": "団地のふたり"
    },
    {
      "lang": "en",
      "text": "団地のふたり"
    },
    {
      "lang": "zh-hk",
      "text": "団地のふたり"
    },
    {
      "lang": "zh-tw",
      "text": "住宅社區的兩人"
    },
    {
      "lang": "zh-cn",
      "text": "住宅区的两人"
    }
  ],
  "localized_description": [
    {
      "lang": "ja",
      "text": "団地で生まれた幼なじみのノエチと奈津子。\n\n結婚したり羽振り良く仕事したり、若い頃は色々あったけれど、わけあって昭和な団地に戻ってきた。\n\n小さな恥も誇りも、本気だった初恋のゆくえもお互いよく知っているから、今さらなにかを取り繕う必要もない。\n\n一緒にご飯を食べてバカなことを言い合いながら、日々へこんだ心をぷーぷー膨らませている。\n\n古くなった団地では、５０代でも十分若手。\n\n子どもの頃から知っているおじちゃん・おばちゃんの家の網戸を張り替えてあげたり、昭和な品をネットで売ってあげたり。\n\n時代遅れの「ガラクタ」でも、どこかにいる誰かにとっては、きっと「宝物」。\n\n運よく高値で売れたら、その日のご飯はちょっとだけ贅沢にする。\n\n一方、新たに越してくる住人たちもそれぞれにワケありで。\n\n助け合いながら、変わらないようで変わっていくコミュニティがそこにある。\n\nまったり、さらり、時々ほろり。\n\n幸せってなんだろう。\n\n今日もなんとか生きていく。"
    },
    {
      "lang": "zh-tw",
      "text": "本劇改編自同名小說，講述了曾被稱為神童的優等生太田野枝（小泉今日子 飾）是兼職大學講師，看起來粗枝大葉，實際上認真嚴肅，離過一次婚；櫻井夏子（小林聰美 飾）是插畫家，然而最近剛失業，靠在跳蚤市場上下載應用程序謀生，做事一絲不苟，擅長烹饪，單身。50多歲、在住宅區出生、住在老家、單身，這樣的兩位發小，即使心有不甯也相安無事，溫暖幽默的友情日常故事。"
    },
    {
      "lang": "zh-cn",
      "text": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/item/tmdb_tv/2024/09/21/5102fc1f-80b0-4c75-9900-b82463eb02d9.jpg",
  "rating": 8.9,
  "rating_count": 81,
  "rating_distribution": [
    0,
    0,
    4,
    29,
    65
  ],
  "tags": [],
  "brief": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。",
  "season_count": 1,
  "orig_title": "団地のふたり",
  "director": [],
  "playwright": [],
  "actor": [
    "小泉今日子",
    "小林聪美",
    "冈光子",
    "杉本哲太",
    "桥爪功",
    "安田章子",
    "名取裕子",
    "冢本高史",
    "大井怜緒"
  ],
  "genre": [
    "剧情"
  ],
  "language": [
    "日本語"
  ],
  "area": [],
  "year": 2024,
  "site": null,
  "episode_count": null,
  "season_uuids": [
    "4h8uV5POiMkGrdRZRIpJtV"
  ],
  "imdb": "tt33310310"
}
"""

const val tvSeasonJson = """
{
  "id": "https://neodb.social/tv/season/4h8uV5POiMkGrdRZRIpJtV",
  "type": "TVSeason",
  "uuid": "4h8uV5POiMkGrdRZRIpJtV",
  "url": "/tv/season/4h8uV5POiMkGrdRZRIpJtV",
  "api_url": "/api/tv/season/4h8uV5POiMkGrdRZRIpJtV",
  "category": "tv",
  "parent_uuid": "1KaJRdpEB6kRPqqKcSDfMs",
  "display_title": "住宅区的两人",
  "external_resources": [
    {
      "url": "https://movie.douban.com/subject/36902276/"
    },
    {
      "url": "https://www.themoviedb.org/tv/255316/season/1"
    }
  ],
  "title": "住宅区的两人",
  "description": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。",
  "localized_title": [
    {
      "lang": "ko",
      "text": "시즌 1"
    },
    {
      "lang": "ja",
      "text": "Season 1"
    },
    {
      "lang": "pt",
      "text": "Temporada 1"
    },
    {
      "lang": "de",
      "text": "Staffel 1"
    },
    {
      "lang": "fr",
      "text": "Saison 1"
    },
    {
      "lang": "es",
      "text": "Temporada 1"
    },
    {
      "lang": "en",
      "text": "Season 1"
    },
    {
      "lang": "zh-hk",
      "text": "第 1 季"
    },
    {
      "lang": "zh-tw",
      "text": "第 1 季"
    },
    {
      "lang": "zh-cn",
      "text": "第 1 季"
    },
    {
      "lang": "ja",
      "text": "団地のふたり"
    },
    {
      "lang": "zh-cn",
      "text": "住宅区的两人"
    }
  ],
  "localized_description": [
    {
      "lang": "zh-cn",
      "text": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/item/tv/2024/11/27/e73d284c-0c87-4ee0-a9c2-44d4d36e5c45.jpeg",
  "rating": 8.9,
  "rating_count": 81,
  "rating_distribution": [
    0,
    0,
    4,
    29,
    65
  ],
  "tags": [
    "2020s",
    "2024",
    "2024观影",
    "24秋",
    "nhk",
    "tv",
    "剧情",
    "友情",
    "喜剧",
    "女性",
    "慢生活",
    "日剧",
    "日本",
    "日本电视剧",
    "治愈",
    "治愈系",
    "温情",
    "电视剧"
  ],
  "brief": "本剧改编自同名小说，讲述了曾被称为神童的优等生太田野枝（小泉今日子 饰）是兼职大学讲师，看起来粗枝大叶，实际上认真严肃，离过一次婚；樱井夏子（小林聪美 饰）是插画家，然而最近刚失业，靠在跳蚤市场上下载应用程序谋生，做事一丝不苟，擅长烹饪，单身。50多岁、在住宅区出生、住在老家、单身，这样的两位发小，即使心有不宁也相安无事，温暖幽默的友情日常故事。",
  "season_number": 1,
  "orig_title": "団地のふたり",
  "director": [
    "松本佳奈",
    "金泽友也"
  ],
  "playwright": [
    "吉田纪子",
    "藤野千夜"
  ],
  "actor": [
    "小泉今日子",
    "小林聪美",
    "丘光子",
    "由纪纱织",
    "名取裕子",
    "杉本哲太",
    "塚本高史",
    "柳原晴郎",
    "桥爪功"
  ],
  "genre": [
    "剧情"
  ],
  "language": [
    ""
  ],
  "area": [
    "日本"
  ],
  "year": 2024,
  "site": "https://www.nhk.jp/p/ts/GZN1ZGJ52M",
  "episode_count": 10,
  "episode_uuids": [
    "5WYOJAHO5TXK9rugtyxu0F",
    "0xCch3Pr02Rfcy0O3OakhG",
    "3QDVTfX8M5rsSOCe5WpuZq",
    "1amNMjcZtRt1YNvHl1KHRc",
    "3uuHM0stdXtUQITtzgTZAY",
    "4PFoz6W5CdD8oDXty3ale2",
    "5ezYuVeMy9uIWuCcc5xiOR",
    "04WmXjojtn213NUdznGFY5",
    "5aVMxnVchGeJBoMaGoUTHU",
    "1ZTs3qGjpX0GxgeoiLBnkq"
  ],
  "imdb": "tt33310310"
}
"""

const val albumJson = """
{
  "id": "https://neodb.social/album/6uEFKVQ0v1L0Fxgf1Zd88q",
  "type": "Album",
  "uuid": "6uEFKVQ0v1L0Fxgf1Zd88q",
  "url": "/album/6uEFKVQ0v1L0Fxgf1Zd88q",
  "api_url": "/api/album/6uEFKVQ0v1L0Fxgf1Zd88q",
  "category": "music",
  "parent_uuid": null,
  "display_title": "ガールズコード",
  "external_resources": [
    {
      "url": "https://open.spotify.com/album/3EVErvMkuQWEFxu8VTrApW"
    }
  ],
  "title": "ガールズコード",
  "description": "",
  "localized_title": [
    {
      "lang": "ja",
      "text": "ガールズコード"
    }
  ],
  "localized_description": [],
  "cover_image_url": "https://neodb.social/m/item/spotify_album/2025/05/08/5951208f-2496-4cd7-a9b5-6edfca5f6fd5.jpg",
  "rating": null,
  "rating_count": 0,
  "rating_distribution": [
    0,
    0,
    0,
    0,
    0
  ],
  "tags": [],
  "brief": "",
  "genre": [],
  "artist": [
    "Poppin'Party"
  ],
  "company": [
    "2018 BanG Dream! Project/Craft Egg Inc./bushiroad",
    "2018 BUSHIROAD MUSIC Inc."
  ],
  "duration": 571699,
  "release_date": "2018-10-03",
  "track_list": "1. ガールズコード\n2. 切ないSandglass",
  "barcode": "4580122203030"
}
"""

const val podcastJson = """
{
  "id": "https://neodb.social/podcast/1fhas0vjxIUmmtxXDGS48y",
  "type": "Podcast",
  "uuid": "1fhas0vjxIUmmtxXDGS48y",
  "url": "/podcast/1fhas0vjxIUmmtxXDGS48y",
  "api_url": "/api/podcast/1fhas0vjxIUmmtxXDGS48y",
  "category": "podcast",
  "parent_uuid": null,
  "display_title": "字谈字畅",
  "external_resources": [
    {
      "url": "https://www.thetype.com/typechat/feed/"
    }
  ],
  "title": "字谈字畅",
  "description": "《字谈字畅》是第一档用华语制作的字体排印主题播客节目，由 Eric Liu 与钱争予搭档主播。The Type 出品。",
  "localized_title": [
    {
      "lang": "zh-cn",
      "text": "字谈字畅"
    }
  ],
  "localized_description": [
    {
      "lang": "zh-cn",
      "text": "《字谈字畅》是第一档用华语制作的字体排印主题播客节目，由 Eric Liu 与钱争予搭档主播。The Type 出品。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/item/rss/2023/02/02/fec6f175-92c9-4119-a71c-285cb468545e.jpg",
  "rating": null,
  "rating_count": 4,
  "rating_distribution": [
    0,
    0,
    0,
    0,
    0
  ],
  "tags": [],
  "brief": "《字谈字畅》是第一档用华语制作的字体排印主题播客节目，由 Eric Liu 与钱争予搭档主播。The Type 出品。",
  "genre": [
    "Arts",
    "Design"
  ],
  "host": [
    "The Type"
  ],
  "language": [],
  "official_site": "https://www.thetype.com/typechat/",
  "hosts": [
    "The Type"
  ]
}
"""

const val performanceJson = """
{
  "type": "Performance",
  "title": "伊丽莎白",
  "description": "ACT1\nElisabeth（昵称Sisi）是出生在巴伐利亚的公主，少年时的她幻想着和父亲一样能过着无拘无束的生活。然而在母亲Ludovika和姨母Shophie给自己的姐姐Helene和表哥奥地利皇帝Franz Joseph安排的相亲见面上年轻的皇帝为美丽的Elisabeth所倾倒，年幼的Elisabeth就这样放弃了自己原来生活的梦想，嫁入奥地利皇室，成为了奥地利年轻的皇后。就在她与Franz Joseph结婚的当天，倾心于Elisabeth已久的死神怒不可遏，他来到婚礼舞会上告诉Elisabeth，最后一支舞我要与你共跳！然而在婚礼的第二天Elisabeth才发现她所处的皇宫不过是一个牢笼，繁琐教条的宫廷礼仪，丈夫对婆婆的一味屈服，她不愿屈从于别人对她的摆布，因为她，只属于她自己。\n在婚姻生活的过程中Elisabeth学会了用自己的美貌和对匈牙利的政治影响与丈夫谈条件，然而就在夫妇俩带着大女儿Sophie出访匈牙利的过程中，死神又悄然而至，他带走了Elisabeth的女儿并告诉她，王朝的毁灭将无法挽回。随着皇太子Rudolf的降生和婆婆Sophie对孩子们教育干涉的增加，Elisabeth再度向丈夫施压，要求拥有决定孩子教育方式的权利，对妻子百依百顺的皇帝无法拒绝Elisabeth的请求答应她一切都能由她做主。\nACT2\n刺杀Elisabeth的无政府主义这Lucheni作为故事的叙述人告诉大家，Elisabeth其实并没有我们想象中的那么完美，她争取到孩子们的抚养权旨在向婆婆Sophie证明自己更强大。我们崇拜她热切关注着她，而这一切不过都是Kitsch！\n在匈牙利的加冕无疑成为了Elisabeth人生中政治上的最大成就。她傲然告诉死神，如果我想跳舞，我将按照自己独特的方式，我将不会呼唤你，此时的Elisabeth正处在人生的巅峰。年幼的Rudolf在深夜呼唤自己的母亲然而只有死神在床边安慰着他，而在Rudolf长大成人后死神更是怂恿他挑起反叛。Elisabeth因为丈夫对自己的背叛在死神面前向他宣告，这一切不可能摧毁她，相反，她要变得更自由更强大。Elisabeth竭力远离奥地利皇室的一切，甚至在Rudolf政变失败绝望地向她求助时也冷心回绝了自己的儿子。皇太子Rudolf举枪自杀。这时Elisabeth才发现自己的过错，她的自私，她的冷漠，造成了儿子的悲剧。悲痛欲绝的Elisabeth第一次屈服于死神，请求他帮她脱离世俗的苦海，而此时的死神却冷冷地告诉她，也要她尝尝被拒绝的滋味。Franz Joseph希望Elisabeth能回到自己身边，而她却告诉他，他们本是暗夜中的两条船，有着各自不同的航线，别再执迷，本就是虚梦一场。哈布斯堡王朝胜景不再，王朝的毁灭在所难免，死神决定给Elisabeth自由的机会，他将一把锥子交给了Lucheni，而后者在日内瓦刺杀了Elisabeth——这位奥地利历史上著名的皇后。Elisabth投入了死神的怀抱,回顾自己的一生“欢笑过，哭泣过，经历绝望而获新生。回顾往生全无遗憾，忠于自我永不背叛”。死神最终用一吻给了Elisabeth永恒的自由。",
  "localized_title": [
    {
      "lang": "zh-cn",
      "text": "伊丽莎白"
    },
    {
      "lang": "en",
      "text": "Elisabeth"
    }
  ],
  "localized_description": [
    {
      "lang": "zh-cn",
      "text": "ACT1\nElisabeth（昵称Sisi）是出生在巴伐利亚的公主，少年时的她幻想着和父亲一样能过着无拘无束的生活。然而在母亲Ludovika和姨母Shophie给自己的姐姐Helene和表哥奥地利皇帝Franz Joseph安排的相亲见面上年轻的皇帝为美丽的Elisabeth所倾倒，年幼的Elisabeth就这样放弃了自己原来生活的梦想，嫁入奥地利皇室，成为了奥地利年轻的皇后。就在她与Franz Joseph结婚的当天，倾心于Elisabeth已久的死神怒不可遏，他来到婚礼舞会上告诉Elisabeth，最后一支舞我要与你共跳！然而在婚礼的第二天Elisabeth才发现她所处的皇宫不过是一个牢笼，繁琐教条的宫廷礼仪，丈夫对婆婆的一味屈服，她不愿屈从于别人对她的摆布，因为她，只属于她自己。\n在婚姻生活的过程中Elisabeth学会了用自己的美貌和对匈牙利的政治影响与丈夫谈条件，然而就在夫妇俩带着大女儿Sophie出访匈牙利的过程中，死神又悄然而至，他带走了Elisabeth的女儿并告诉她，王朝的毁灭将无法挽回。随着皇太子Rudolf的降生和婆婆Sophie对孩子们教育干涉的增加，Elisabeth再度向丈夫施压，要求拥有决定孩子教育方式的权利，对妻子百依百顺的皇帝无法拒绝Elisabeth的请求答应她一切都能由她做主。\nACT2\n刺杀Elisabeth的无政府主义这Lucheni作为故事的叙述人告诉大家，Elisabeth其实并没有我们想象中的那么完美，她争取到孩子们的抚养权旨在向婆婆Sophie证明自己更强大。我们崇拜她热切关注着她，而这一切不过都是Kitsch！\n在匈牙利的加冕无疑成为了Elisabeth人生中政治上的最大成就。她傲然告诉死神，如果我想跳舞，我将按照自己独特的方式，我将不会呼唤你，此时的Elisabeth正处在人生的巅峰。年幼的Rudolf在深夜呼唤自己的母亲然而只有死神在床边安慰着他，而在Rudolf长大成人后死神更是怂恿他挑起反叛。Elisabeth因为丈夫对自己的背叛在死神面前向他宣告，这一切不可能摧毁她，相反，她要变得更自由更强大。Elisabeth竭力远离奥地利皇室的一切，甚至在Rudolf政变失败绝望地向她求助时也冷心回绝了自己的儿子。皇太子Rudolf举枪自杀。这时Elisabeth才发现自己的过错，她的自私，她的冷漠，造成了儿子的悲剧。悲痛欲绝的Elisabeth第一次屈服于死神，请求他帮她脱离世俗的苦海，而此时的死神却冷冷地告诉她，也要她尝尝被拒绝的滋味。Franz Joseph希望Elisabeth能回到自己身边，而她却告诉他，他们本是暗夜中的两条船，有着各自不同的航线，别再执迷，本就是虚梦一场。哈布斯堡王朝胜景不再，王朝的毁灭在所难免，死神决定给Elisabeth自由的机会，他将一把锥子交给了Lucheni，而后者在日内瓦刺杀了Elisabeth——这位奥地利历史上著名的皇后。Elisabth投入了死神的怀抱,回顾自己的一生“欢笑过，哭泣过，经历绝望而获新生。回顾往生全无遗憾，忠于自我永不背叛”。死神最终用一吻给了Elisabeth永恒的自由。"
    }
  ],
  "cover_image_url": "https://neodb.social/m/item/doubandrama/2023/06/08/6241e6e2-03d6-418c-b97d-30d82f8f9bc7.jpg",
  "rating": 9.7,
  "rating_count": 41,
  "rating_distribution": [
    0,
    0,
    2,
    12,
    85
  ],
  "tags": [
    "musical",
    "上海文化广场",
    "历史",
    "奥地利",
    "女性",
    "德国",
    "德语",
    "舞台剧",
    "音乐剧"
  ],
  "brief": "ACT1\nElisabeth（昵称Sisi）是出生在巴伐利亚的公主，少年时的她幻想着和父亲一样能过着无拘无束的生活。然而在母亲Ludovika和姨母Shophie给自己的姐姐Helene和表哥奥地利皇帝Franz Joseph安排的相亲见面上年轻的皇帝为美丽的Elisabeth所倾倒，年幼的Elisabeth就这样放弃了自己原来生活的梦想，嫁入奥地利皇室，成为了奥地利年轻的皇后。就在她与Franz Joseph结婚的当天，倾心于Elisabeth已久的死神怒不可遏，他来到婚礼舞会上告诉Elisabeth，最后一支舞我要与你共跳！然而在婚礼的第二天Elisabeth才发现她所处的皇宫不过是一个牢笼，繁琐教条的宫廷礼仪，丈夫对婆婆的一味屈服，她不愿屈从于别人对她的摆布，因为她，只属于她自己。\n在婚姻生活的过程中Elisabeth学会了用自己的美貌和对匈牙利的政治影响与丈夫谈条件，然而就在夫妇俩带着大女儿Sophie出访匈牙利的过程中，死神又悄然而至，他带走了Elisabeth的女儿并告诉她，王朝的毁灭将无法挽回。随着皇太子Rudolf的降生和婆婆Sophie对孩子们教育干涉的增加，Elisabeth再度向丈夫施压，要求拥有决定孩子教育方式的权利，对妻子百依百顺的皇帝无法拒绝Elisabeth的请求答应她一切都能由她做主。\nACT2\n刺杀Elisabeth的无政府主义这Lucheni作为故事的叙述人告诉大家，Elisabeth其实并没有我们想象中的那么完美，她争取到孩子们的抚养权旨在向婆婆Sophie证明自己更强大。我们崇拜她热切关注着她，而这一切不过都是Kitsch！\n在匈牙利的加冕无疑成为了Elisabeth人生中政治上的最大成就。她傲然告诉死神，如果我想跳舞，我将按照自己独特的方式，我将不会呼唤你，此时的Elisabeth正处在人生的巅峰。年幼的Rudolf在深夜呼唤自己的母亲然而只有死神在床边安慰着他，而在Rudolf长大成人后死神更是怂恿他挑起反叛。Elisabeth因为丈夫对自己的背叛在死神面前向他宣告，这一切不可能摧毁她，相反，她要变得更自由更强大。Elisabeth竭力远离奥地利皇室的一切，甚至在Rudolf政变失败绝望地向她求助时也冷心回绝了自己的儿子。皇太子Rudolf举枪自杀。这时Elisabeth才发现自己的过错，她的自私，她的冷漠，造成了儿子的悲剧。悲痛欲绝的Elisabeth第一次屈服于死神，请求他帮她脱离世俗的苦海，而此时的死神却冷冷地告诉她，也要她尝尝被拒绝的滋味。Franz Joseph希望Elisabeth能回到自己身边，而她却告诉他，他们本是暗夜中的两条船，有着各自不同的航线，别再执迷，本就是虚梦一场。哈布斯堡王朝胜景不再，王朝的毁灭在所难免，死神决定给Elisabeth自由的机会，他将一把锥子交给了Lucheni，而后者在日内瓦刺杀了Elisabeth——这位奥地利历史上著名的皇后。Elisabth投入了死神的怀抱,回顾自己的一生“欢笑过，哭泣过，经历绝望而获新生。回顾往生全无遗憾，忠于自我永不背叛”。死神最终用一吻给了Elisabeth永恒的自由。",
  "id": "https://neodb.social/performance/5xvzvPvZV8NtvNo3jFG4Ea",
  "uuid": "5xvzvPvZV8NtvNo3jFG4Ea",
  "url": "/performance/5xvzvPvZV8NtvNo3jFG4Ea",
  "api_url": "/api/performance/5xvzvPvZV8NtvNo3jFG4Ea",
  "category": "performance",
  "parent_uuid": null,
  "display_title": "伊丽莎白",
  "external_resources": [
    {
      "url": "https://www.douban.com/location/drama/1825726/"
    }
  ],
  "orig_title": "Elisabeth",
  "genre": [
    "音乐剧"
  ],
  "language": [],
  "opening_date": null,
  "closing_date": null,
  "director": [
    "Harry Kupfer",
    "Eddy Habbema",
    "刘路",
    "江珊",
    "엄홍현"
  ],
  "playwright": [
    "Michael Kunze",
    "刘路",
    "ミヒャエル・クンツェ",
    "章翔"
  ],
  "orig_creator": [],
  "composer": [],
  "choreographer": [],
  "performer": [],
  "actor": [
    {
      "name": "Pia Douwes",
      "role": ""
    },
    {
      "name": "Uwe Kröger",
      "role": ""
    },
    {
      "name": "Ethan Freeman",
      "role": ""
    },
    {
      "name": "Viktor Gernot",
      "role": ""
    },
    {
      "name": "Andreas Bieber",
      "role": ""
    }
  ],
  "crew": [],
  "official_site": null
}
"""

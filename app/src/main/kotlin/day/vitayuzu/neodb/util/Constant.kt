package day.vitayuzu.neodb.util

const val BASE_URL = "neodb.social"
const val AUTH_CALLBACK = "day.vitayuzu.neodb://auth"
const val APP_NAME = "NeoDB You"
const val WEBSITE = "https://github.com/heddxh/NeoDB-You"
const val USER_PREFERENCES = "user_preferences"

/**
 * Extract from [settings.py](https://raw.githubusercontent.com/neodb-social/neodb/main/neodb/boofilsic/settings.py)
 *
 * NOTE: Subtract primary language subtag for post; Pass it as Accept-Language to get localized metadata
 */
val Supported_Languages = listOf(
    "da",
    "de",
    "en",
    "es",
    "fr",
    "it",
    "pt",
    "pt-br",
    "zh-hans",
    "zh-hant",
)

package day.vitayuzu.neodb.util

import java.util.Locale

/**
 * Best matching tag in [Supported_Languages] for this locale (RFC 4647 lookup),
 * falling back to script/region inference for Chinese and "en" otherwise.
 */
fun Locale.toSupportedTag(): String {
    val matched = Locale.lookupTag(
        Locale.LanguageRange.parse(toLanguageTag()),
        Supported_Languages,
    )
    return matched ?: when {
        language != "zh" -> "en"
        script == "Hant" || country in setOf("TW", "HK", "MO") -> "zh-hant"
        else -> "zh-hans"
    }
}

package day.vitayuzu.neodb.util

import androidx.core.os.LocaleListCompat
import day.vitayuzu.neodb.data.schema.detail.LocalizedData
import java.util.Locale

// NOTE: multiple items with same lang are returned. We simply choose the first one.
fun List<LocalizedData>.display(preferredLang: String): String? {
    val preferred = Locale.forLanguageTag(preferredLang)

    val candidates = this.mapNotNull { (lang, text) ->
        val text = text.trim()
        if (text.isBlank()) return@mapNotNull null
        val locale = Locale.forLanguageTag(lang)
        if (locale.language.isBlank()) return@mapNotNull null // bad language tag

        locale to text
    }

    candidates.firstOrNull { [locale, _] ->
        locale == preferred
    }?.let { return it.second }

    candidates.firstOrNull { [locale, _] ->
        LocaleListCompat.matchesLanguageAndScript(locale, preferred)
    }?.let { return it.second }

    candidates.firstOrNull { [locale, _] ->
        locale.language == preferred.language
    }?.let { return it.second }

    candidates.firstOrNull { [locale, _] ->
        locale.language == "en"
    }?.let { return it.second }

    return candidates.firstOrNull()?.second
}

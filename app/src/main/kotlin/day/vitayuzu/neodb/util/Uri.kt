package day.vitayuzu.neodb.util

import android.net.Uri

/**
 * Normalize or add scheme part of [Uri]
 */
fun Uri.toHttpUri(): Uri {
    this.scheme?.let {
        return this.normalizeScheme()
    }
    return this.buildUpon().scheme("https").build()
}

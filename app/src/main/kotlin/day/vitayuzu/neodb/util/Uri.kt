package day.vitayuzu.neodb.util

import android.net.Uri

fun buildInstanceUri(instanceHost: String, encodedPath: String): Uri = Uri
    .Builder()
    .scheme("https")
    .encodedAuthority(instanceHost)
    .encodedPath("/${encodedPath.trimStart('/')}")
    .build()

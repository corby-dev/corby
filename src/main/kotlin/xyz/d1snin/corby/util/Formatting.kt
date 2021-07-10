/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
 */

package xyz.d1snin.corby.util

import java.util.concurrent.TimeUnit

fun Long.formatTimeMillis(): String {
    return "%02dh %02dm %02ds".format(
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)
    )
}

fun formatWithKey(pair: Pair<String, String>, italic: Boolean = false): String {
    return "**${pair.first}:** ${if (italic) "*${pair.second}*" else pair.second}"
}

fun formatJson(pair: Pair<String, String>): String {
    return "{${pair.first}: '${pair.second}'}"
}
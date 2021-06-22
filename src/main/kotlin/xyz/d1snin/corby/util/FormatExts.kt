package xyz.d1snin.corby.util

import java.util.concurrent.TimeUnit

fun Long.formatTimeMillis(): String {
    return "%02dh %02dm %02ds".format(
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)
    )
}

fun formatWithKey(key: String, value: String, italic: Boolean = false): String {
    return "**$key:** ${if (italic) "*$value*" else value}"
}
package xyz.d1snin.corby.util

fun runSafe(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        // TODO processing exception
        e.printStackTrace()
    }
}
/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.util

fun runSafe(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        // TODO processing exception
        e.printStackTrace()
    }
}
/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.util

import kotlin.reflect.KClass

fun runSafe(ignore: KClass<out java.lang.Exception>? = null, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        if (ignore != null && !ignore.isInstance(e)) {
            // TODO processing exception
            e.printStackTrace()
        }
    }
}
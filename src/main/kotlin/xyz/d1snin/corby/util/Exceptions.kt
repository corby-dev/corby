/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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
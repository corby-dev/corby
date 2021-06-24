/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.util

import xyz.d1snin.corby.Corby.log

data class LaunchFlags(val usage: String, val onArg: () -> Unit) {
    internal fun execute() = onArg()

    companion object {
        private lateinit var flags: Set<LaunchFlags>

        internal fun init(args: Array<String>, vararg flag: LaunchFlags) {
            flags = flag.toSet()

            if (args.isEmpty()) {
                log("No flags provided.")
                return
            }

            args.forEach {
                val fl = getFlagByUsage(it)
                requireNotNull(fl) { "Could not resolve this flag: $it" }

                log("Flag detected: $it")
                fl.execute()
            }
        }

        private fun getFlagByUsage(usage: String): LaunchFlags? = flags.firstOrNull {
            "-${it.usage}" == usage
        }
    }
}

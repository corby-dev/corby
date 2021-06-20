package xyz.d1snin.corby.model

import xyz.d1snin.corby.Corby.log

data class LaunchFlag(val usage: String, val onArg: () -> Unit) {
    internal fun execute() = onArg()

    companion object {
        private lateinit var flags: Set<LaunchFlag>

        internal fun init(args: Array<String>, vararg flag: LaunchFlag) {
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

        private fun getFlagByUsage(usage: String): LaunchFlag? = flags.filter {
            "-${it.usage}" == usage
        }.takeIf {
            it.size == 1
        }?.first()
    }
}

/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

import xyz.d1snin.corby.commands.CommandProvider
import kotlin.properties.Delegates

data class Statement(
    val arguments: List<Argument>,
    val block: CommandProvider.() -> Unit
) {
    var length by Delegates.notNull<Int>()
}

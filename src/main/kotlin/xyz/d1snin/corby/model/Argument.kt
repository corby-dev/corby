/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

class Argument { // you can call the default constructor, it will be something like ";command <Value>", and will take something like ";command this is value with variable length"

    var usage: String? = null
    var type: String = "<Value>"
    var isValueRequired: Boolean = true
    var isVariableLength: Boolean = false

    lateinit var value: String

    companion object {
        inline fun argument(block: Argument.() -> Unit): Argument {
            return Argument().apply(block)
        }
    }
}

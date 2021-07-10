/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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

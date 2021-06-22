/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

data class Argument(
    val usage: String?,
    val type: String,
    val isValueRequired: Boolean = false,
    val isVariableLength: Boolean = false,
) {
    lateinit var value: String
}

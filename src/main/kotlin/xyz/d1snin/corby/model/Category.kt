/*
 * BSD 3-Clause License, Copyright (c) 2021, Corby and contributors, All rights reserved.
 */

package xyz.d1snin.corby.model

enum class Category(
    val categoryName: String,
    val adminCategory: Boolean = false
) {
    MISC("Misc"),
    FUN("Fun"),
    SETTINGS("Settings"),
    ADMIN("Admin", true)
}
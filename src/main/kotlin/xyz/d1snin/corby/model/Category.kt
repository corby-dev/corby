/*
 *                       HOT POTATO LICENSE
 *                  Version 1, September 2017
 *   All rights reserved by the last person to commit a change to this
 * repository, except for the right to commit changes to this repository,
 * which is hereby granted to all of earth's citizens for the purpose of
 *            committing changes to this repository.
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
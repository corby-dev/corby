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
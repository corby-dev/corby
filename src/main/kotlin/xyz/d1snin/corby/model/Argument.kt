package xyz.d1snin.corby.model

data class Argument(
    val usage: String?,
    val type: String,
    val isValueRequired: Boolean,
    val isVariableLength: Boolean,
) {
    lateinit var value: String
}

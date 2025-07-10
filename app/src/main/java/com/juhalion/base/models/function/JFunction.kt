package com.juhalion.base.models.function

sealed class JFunction(open val id: Int) {
    data class FunctionItem(override val id: Int, val title: String) : JFunction(id)
    data class HeaderItem(override val id: Int, val title: String) : JFunction(id)
}
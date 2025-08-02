package com.juhalion.base.models.function

sealed class JFunction(open val id: Int, open val type: Int) {
    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_FUNCTION = 2
        const val TYPE_SWITCH = 3
    }

    data class FunctionItem(override val id: Int, override val type: Int = TYPE_FUNCTION, val title: String) : JFunction(id, type)
    data class HeaderItem(override val id: Int, override val type: Int = TYPE_HEADER, val title: String) : JFunction(id, type)
    data class SwitchItem(override val id: Int, override val type: Int = TYPE_SWITCH, val title: String, var isChecked: Boolean) : JFunction(id, type)
}

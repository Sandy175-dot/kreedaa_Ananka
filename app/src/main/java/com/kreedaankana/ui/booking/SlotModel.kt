package com.kreedaankana.ui.booking

data class SlotModel(
    val time: String,
    var isBooked: Boolean = false,
    var isSelected: Boolean = false
)

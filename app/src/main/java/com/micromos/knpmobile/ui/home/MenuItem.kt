package com.micromos.knpmobile.ui.home

enum class MenuItem (val title: String, val id: String) {
    ProductCoilIn("상차관리","PD0001E"),
    ProductCoilOut("출고관리","PD0002E"),
    ProductStockCheck("재고조사", "PD0003E"),
    ProductChangePos("적재위치변경","PD0004E")
}
package com.micromos.knpmobile.ui.home

enum class MenuItem (val title: String, val id: String) {
    ProductCoilIn("상차관리","pd0001e"),
    ProductCoilOut("출고관리","pd0002e"),
    ProductStockCheck("재고조사", "pd0003e"),
    ProductChangePos("적재위치변경","pd0004e"),
    ProductMaterialCheck("소재확인", "pd0005e")
}
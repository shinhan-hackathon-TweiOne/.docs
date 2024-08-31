package com.example.shinhantime

import android.net.Uri

data class Item(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Double
)

data class Category(
    val id: Int,
    val name: String,
    val items: List<Item>
)

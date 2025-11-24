package com.example.googleplay

data class AppModel(
    val name: String,
    val category: String,
    val rating: Float,
    val color: Int
)

data class CategoryModel(
    val title: String,
    val apps: List<AppModel>
)

data class SuggestedAppModel(
    val name: String,
    val category: String,
    val subcategory: String,
    val rating: Float,
    val color: Int
)

sealed class CategoryItem {
    data class NormalCategory(val categoryModel: CategoryModel) : CategoryItem()
    data class SuggestedCategory(val columns: List<List<SuggestedAppModel>>) : CategoryItem()
}


package com.example.domain.models

import com.example.data.models.categories.CategoryModel
import com.example.ui.home.model.CategoryUIModel

fun CategoryModel.toUIModel(): CategoryUIModel {
    return CategoryUIModel(
        id = id, name = name, icon = icon
    )
}
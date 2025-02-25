package com.example.data.repository.categories

import com.example.data.models.Resource
import com.example.data.models.categories.CategoryModel
import com.example.ui.home.model.CategoryUIModel
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {

    fun getCategories(): Flow<Resource<List<CategoryUIModel>>>
}
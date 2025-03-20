package com.example.data.repository.special_sections

import com.example.data.models.SpecialSectionModel
import kotlinx.coroutines.flow.Flow

interface SpecialSectionsRepository {
    fun recommendProductsSection(): Flow<SpecialSectionModel?>
}
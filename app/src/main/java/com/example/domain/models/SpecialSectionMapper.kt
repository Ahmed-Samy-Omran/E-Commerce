package com.example.domain.models

import com.example.data.models.SpecialSectionModel
import com.example.ui.home.model.SpecialSectionUIModel


fun SpecialSectionModel.toSpecialSectionUIModel(): SpecialSectionUIModel {
    return SpecialSectionUIModel(
        id = id,
        title = title,
        description = description,
        type = type,
        image = image,
        enabled = enabled
    )
}

fun SpecialSectionUIModel.toSpecialSectionModel(): SpecialSectionModel {
    return SpecialSectionModel(
        id = id,
        title = title,
        description = description,
        type = type,
        image = image,
        enabled = enabled
    )
}
package com.example.domain.models

import com.example.data.models.auth.CountryModel
import com.example.ui.auth.models.CountryUIModel

fun CountryModel.toUIModel(): CountryUIModel {
    return CountryUIModel(
        id = id,
        name = name,
        code = code,
        currency = currency,
        image = image,
        currencySymbol = currencySymbol
    )
}
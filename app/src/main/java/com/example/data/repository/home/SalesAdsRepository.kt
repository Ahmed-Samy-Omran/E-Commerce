package com.example.data.repository.home

import com.example.data.models.Resource
import com.example.data.models.sale_ads.SalesAdModel
import com.example.ui.home.model.SalesAdUIModel
import kotlinx.coroutines.flow.Flow

interface SalesAdsRepository {
    fun getSalesAds():Flow<Resource<List<SalesAdModel>>>
}
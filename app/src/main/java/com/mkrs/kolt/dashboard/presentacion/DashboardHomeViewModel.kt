package com.mkrs.kolt.dashboard.presentacion

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.presentacion
 * Date: 06 / 05 / 2024
 *****/
class DashboardHomeViewModel(): ViewModel() {
    private val mutableDashBoarHomeUiState= MutableLiveData<DashboardHomeUIState>()
    val dashboardHomeUIState:LiveData<DashboardHomeUIState>
        get() = mutableDashBoarHomeUiState
}
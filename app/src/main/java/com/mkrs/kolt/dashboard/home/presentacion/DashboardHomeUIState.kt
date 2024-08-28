package com.mkrs.kolt.dashboard.home.presentacion

/****
 * Project: Kolt
 * Dev: Oscar Balderas Vazquez
 * From: com.mkrs.kolt.dashboard.presentacion
 * Date: 06 / 05 / 2024
 *****/
sealed class DashboardHomeUIState{
    object Loading: DashboardHomeUIState()
    object NoState: DashboardHomeUIState()
    data class Error(val message:String): DashboardHomeUIState()
}
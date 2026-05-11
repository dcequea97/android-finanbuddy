package com.example.finanbuddy.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SheetSaveData(
    val categoria: String,
    val monto: Double,
    val concepto: String,
    val fecha: String,
    val idToken: String
)

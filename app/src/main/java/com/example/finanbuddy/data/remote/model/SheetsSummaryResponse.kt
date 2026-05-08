package com.example.finanbuddy.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class SheetsSummaryResponse(
    val aporteDeudas: Double,
    val asignadoPresupuesto: Double,
    val disponibleGastar: Double,
    val distribucion: List<Distribucion>,
    val gastadoHastaAhora: Double,
    val ingresosTotales: Double,
    val pendienteAsignar: Double
)

@Serializable
data class Distribucion(
    val actual: Double,
    val cat: String,
    val objetivo: Double,
    val pct: String
)
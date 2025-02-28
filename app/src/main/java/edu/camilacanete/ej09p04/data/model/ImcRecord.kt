package edu.camilacanete.ej09p04.data.model


data class ImcRecord (
    val id : Long? = null,
    val date: String,
    val stateResult: String,
    val gender: String,
    val weight : Double,
    val height : Double,
    val imcResult: Double
)
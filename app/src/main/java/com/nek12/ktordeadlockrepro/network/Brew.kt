package com.nek12.ktordeadlockrepro.network


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class Brew(
    val abv: Double = 0.0,
    @SerialName("attenuation_level")
    val attenuationLevel: Double = 0.0,
    val description: String = "",
    val ebc: Double = 0.0,
    val ibu: Double = 0.0,
    val id: Double = 0.0,
    @SerialName("image_url")
    val imageUrl: String = "",
    val name: String = "",
    val ph: Double = 0.0,
    val srm: Double = 0.0,
    val tagline: String = "",
    @SerialName("target_fg")
    val targetFg: Double = 0.0,
    @SerialName("target_og")
    val targetOg: Double = 0.0,
)

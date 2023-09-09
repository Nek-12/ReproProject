@file:UseSerializers(UUIDSerializer::class)

package com.nek12.ktordeadlockrepro.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.http.userAgent
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

class RespawnApi(client: HttpClient) : RestApi(client) {

    suspend fun emailLogin(request: EmailLoginRequest) =
        post<AuthResponse, _>("https://respawn-backend-stg.onrender.com/api/auth/email/login", request) {
            Log.d("Ktor", "request to ${this.url}")
            userAgent("Respawn-Android")
        }
}

object UUIDSerializer : KSerializer<UUID> {

    override val descriptor = PrimitiveSerialDescriptor("Uuid", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID = UUID.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value.toString())
}

@Serializable
data class EmailLoginRequest(
    val email: String,
    val password: String,
    val isFraudulent: Boolean,
    val isRooted: Boolean,
)

@Serializable
data class AuthResponse(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String,
)

@Serializable
data class UserResponse(
    val email: String,
    val avatar: String?,
    val name: String,
    val locale: String?,
    val hasPassword: Boolean,
    val createdAt: Instant,
    val id: UUID,
    val hasReviews: Boolean,
    val hasSyncedEntries: Boolean,
)

@Serializable
@SerialName("UserErrorResponse")
sealed interface UserErrorResponse {

    @Serializable
    @SerialName("EmailNotValid")
    data object EmailNotValid : UserErrorResponse

    @Serializable
    @SerialName("EmailNotVerified")
    data object EmailNotVerified : UserErrorResponse

    @Serializable
    @SerialName("UserHasNoPassword")
    data object UserHasNoPassword : UserErrorResponse

    @Serializable
    @SerialName("DuplicateEntity")
    data object EmailTaken : UserErrorResponse
}

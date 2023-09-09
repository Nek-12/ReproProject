package com.nek12.ktordeadlockrepro.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.DataConversion
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single { provideJson() }
    single { provideHttpClient(get()) }
    singleOf(::BrewApi)
    singleOf(::RespawnApi)
}

@OptIn(ExperimentalSerializationApi::class)
private fun provideJson() = Json {
    explicitNulls = false
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

@Suppress("MagicNumber")
private fun provideHttpClient(json: Json) = HttpClient(CIO) {
    install(Logging) {
        logger = Logger.ANDROID
        level = LogLevel.ALL
        sanitizeHeader { it == HttpHeaders.Authorization }
    }
    install(ContentNegotiation) { json(json) }

    install(HttpRequestRetry) {
        retryOnServerErrors(1)
        constantDelay(1000)
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 8000
        connectTimeoutMillis = 5000
    }

    install(DataConversion)

    addDefaultResponseValidation()

    developmentMode = true
    expectSuccess = true
    followRedirects = true
    engine {
        requestTimeout = 8000
        maxConnectionsCount = 8000
        pipelining = true
        endpoint {
            connectTimeout = 8000
            connectAttempts = 1
        }
    }
}

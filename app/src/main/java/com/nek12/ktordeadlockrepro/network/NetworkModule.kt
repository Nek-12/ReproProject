package com.nek12.ktordeadlockrepro.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DataConversion
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.addDefaultResponseValidation
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.logging.HttpLoggingInterceptor
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
private fun provideHttpClient(json: Json) = HttpClient(OkHttp) {
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

    install(DataConversion)
    install(ContentEncoding) {
        deflate(1f)
        gzip(0.8f)
        identity(0.5f)
    }

    developmentMode = true
    expectSuccess = true
    followRedirects = true
    engine {
        pipelining = true
        addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
    }
}

package com.nek12.ktordeadlockrepro.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import pro.respawn.apiresult.ApiResult

open class RestApi(protected val client: HttpClient) {

    protected suspend inline fun <reified T, reified R> call(
        url: String,
        method: HttpMethod,
        body: R? = null,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = ApiResult {
        // not removing "/" prefix will break ktor url resolution
        client.request(url.removePrefix("/")) {
            // not removing "/" prefix will break ktor url resolution
            this.method = method

            body?.let {
                contentType(ContentType.Application.Json)
                setBody(it)
            }
            builder()
        }.body<T>()
    }

    protected suspend inline fun <reified T> get(
        url: String,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = call<T, Unit>(url, HttpMethod.Get, null, builder)

    protected suspend inline fun <reified T, reified R> post(
        url: String,
        body: R? = null,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = call<T, R>(url, HttpMethod.Post, body, builder)

    protected suspend inline fun <reified T, reified R> put(
        url: String,
        body: R? = null,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = call<T, R>(url, HttpMethod.Put, body, builder)

    protected suspend inline fun <reified T, reified R> delete(
        url: String,
        body: R? = null,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = call<T, R>(url, HttpMethod.Delete, body, builder)

    protected suspend inline fun <reified T, reified R> patch(
        url: String,
        body: R? = null,
        builder: HttpRequestBuilder.() -> Unit = {},
    ) = call<T, R>(url, HttpMethod.Patch, body, builder)
}

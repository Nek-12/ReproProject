package com.nek12.ktordeadlockrepro.network

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter

fun HttpRequestBuilder.paginate(
    page: Int,
    pageSize: Int? = null,
    sort: String? = null,
    sortBy: String? = null,
    nullsLast: Boolean? = null,
) {
    parameter("page", page)
    parameter("size", pageSize)
    parameter("sort", sort)
    parameter("nullsLast", nullsLast)
    parameter("sortBy", sortBy)
}

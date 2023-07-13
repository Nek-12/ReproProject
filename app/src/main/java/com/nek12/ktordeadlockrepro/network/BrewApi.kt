package com.nek12.ktordeadlockrepro.network

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pro.respawn.kmmutils.apiresult.ApiResult

class BrewApi(client: HttpClient) : RestApi(client) {

    suspend fun getBrews(page: Int, pageSize: Int): ApiResult<List<Brew>> = withContext(Dispatchers.IO) {
        get("https://api.punkapi.com/v2/beers") {
            parameter("page", page)
            parameter("per_page", pageSize)
        }
    }
}

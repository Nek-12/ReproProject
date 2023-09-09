package com.nek12.ktordeadlockrepro.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import pro.respawn.apiresult.ApiResult
import pro.respawn.apiresult.fold

abstract class SequentialPagingSource<T : Any> : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }
}

open class ResultSequentialPagingSource<R : Any>(
    private val call: suspend (page: Int, pageSize: Int) -> ApiResult<List<R>>,
) : SequentialPagingSource<R>() {

    override suspend fun load(params: LoadParams<Int>) = call(params.key ?: 1, params.loadSize)
        .also { Log.d("Pager", "Calling load with key= ${params.key}") }
        .fold(
            onSuccess = { list ->
                LoadResult.Page(
                    data = list,
                    prevKey = params.key?.minus(1).takeIf { it != 0 },
                    nextKey = params.key?.plus(1).takeIf { list.isNotEmpty() }
                )
            },
            onError = { LoadResult.Error(it) }
        )
}

fun <R : Any> paged(
    request: suspend (page: Int, pageSize: Int) -> ApiResult<List<R>>,
): () -> ResultSequentialPagingSource<R> = { ResultSequentialPagingSource(request) }

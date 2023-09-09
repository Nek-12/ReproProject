package com.nek12.ktordeadlockrepro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nek12.ktordeadlockrepro.network.BrewApi
import com.nek12.ktordeadlockrepro.network.EmailLoginRequest
import com.nek12.ktordeadlockrepro.network.RespawnApi
import com.nek12.ktordeadlockrepro.network.UserErrorResponse
import com.nek12.ktordeadlockrepro.network.paged
import com.nek12.ktordeadlockrepro.ui.theme.KtorDeadlockReproTheme
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.serialization.ContentConvertException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.koin.android.ext.android.inject
import pro.respawn.apiresult.ApiResult
import pro.respawn.apiresult.errorOnNull
import pro.respawn.apiresult.map
import pro.respawn.apiresult.mapError
import pro.respawn.apiresult.onError
import pro.respawn.apiresult.onSuccess
import pro.respawn.apiresult.or
import pro.respawn.apiresult.orElse
import pro.respawn.apiresult.orNull
import pro.respawn.apiresult.recover
import pro.respawn.apiresult.tryRecover

class MainActivity : ComponentActivity() {

    private val api by inject<RespawnApi>()
    private val request = EmailLoginRequest(
        email = "mudqu@fexpost.com",
        password = "nwsSm_K8IJQYPOU",
        isFraudulent = false,
        isRooted = false,
    )
    private var error: Exception? by mutableStateOf(null)
    private var body: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorDeadlockReproTheme {
                val scope = rememberCoroutineScope()

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Button(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            scope.launch {
                                api.emailLogin(request)
                                    .map { "Success" }
                                    .tryRecover<ClientRequestException, _> {
                                        Log.i("Result", "Entered parsing block")
                                        val parsed = it.response.body<UserErrorResponse?>()
                                        Log.i("Result", "Left parsing block")
                                        parsed.toString()
                                    }
                                    .onSuccess {
                                        error = null
                                        body = it
                                        Log.i("Success", "e=$error : b=$body")
                                    }
                                    .onError {
                                        error = it
                                        Log.i("Error", "e=$error : b=$body")
                                    }
                            }
                        }
                    ) {
                        Text("Sign in with test creds")
                    }

                    error?.let {
                        Text("Error = $it")
                    }
                    body?.let {
                        Text("Body = $it")
                    }
                }
            }
        }
    }
}

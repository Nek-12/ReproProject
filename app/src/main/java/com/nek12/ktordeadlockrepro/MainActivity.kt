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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nek12.ktordeadlockrepro.network.EmailLoginRequest
import com.nek12.ktordeadlockrepro.network.RespawnApi
import com.nek12.ktordeadlockrepro.network.UserErrorResponse
import com.nek12.ktordeadlockrepro.ui.theme.KtorDeadlockReproTheme
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import pro.respawn.apiresult.map
import pro.respawn.apiresult.onError
import pro.respawn.apiresult.onSuccess
import pro.respawn.apiresult.tryRecover

class MainActivity : ComponentActivity() {

    private val api by inject<RespawnApi>()
    private val request = EmailLoginRequest(
        email = "mudqu@fexpost.com",
        password = "nwsSm_K8IJQYPOU",
        isFraudulent = false,
        isRooted = false,
    )
    private val json by inject<Json>()
    private var error: Exception? by mutableStateOf(null)
    private var body: String? by mutableStateOf(null)
    private var loading by mutableStateOf(false)

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
                    Text("First few attempts may fail with timeout because of server spindown")
                    Button(
                        modifier = Modifier.padding(12.dp),
                        onClick = {
                            scope.launch {
                                loading = true
                                api.emailLogin(request)
                                    .map { "Success" }
                                    .tryRecover<ClientRequestException, _> { e ->
                                        Log.i("Result", "Entered parsing block, status = ${e.response.status}")
                                        val parsed =
                                            e.response.bodyAsText().let {
                                                Log.i("decoding", it) // never invoked?
                                                json.decodeFromString<UserErrorResponse>(it)
                                            }
                                        Log.i("Result", "Left parsing block") // never invoked?
                                        parsed.toString()
                                    }
                                    .also { Log.d("ApiResult", it.toString()) } // never invoked?
                                    .onSuccess {
                                        error = null
                                        body = it
                                        Log.i("Success", "e=$error : b=$body")
                                    }
                                    .onError {
                                        error = it
                                        Log.i("Error", "e=$error : b=$body")
                                    }
                            }.invokeOnCompletion {
                                loading = false
                                Log.i("Coroutine", "Coroutine finished with e=$it") // finishes with completion:
                                //Coroutine finished with e=kotlinx.coroutines.JobCancellationException:
                                // Parent job is Completed; ?
                            }
                        }
                    ) {
                        Text("Sign in with test creds")
                    }

                    if (loading) {
                        CircularProgressIndicator()
                    }

                    Text("Error = $error")
                    Text("Body = $body")
                }
            }
        }
    }
}

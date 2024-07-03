package com.example.passwormanager

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.passwormanager.home.pesentation.HomeScreen
import com.example.passwormanager.ui.theme.PassworManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val biometricAuthenticator = BiometricAuthenticator(this)

        setContent {
            PassworManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isAuthenticated by remember { mutableStateOf(false) }
                    var message by remember { mutableStateOf("") }

                    if (isAuthenticated) {
                        HomeScreen()
                    } else {
                        BiometricAuthScreen(
                            biometricAuthenticator = biometricAuthenticator,
                            onSuccess = {
                                isAuthenticated = true
                                message = "Success"
                            },
                            onError = { _, errorString ->
                                message = errorString
                            },
                            onFailed = {
                                message = "Verification error"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BiometricAuthScreen(
    biometricAuthenticator: BiometricAuthenticator,
    onSuccess: () -> Unit,
    onError: (Int, String) -> Unit,
    onFailed: () -> Unit
) {
    val activity = LocalContext.current as FragmentActivity
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = {
                biometricAuthenticator.promptBiometricAuth(
                    title = "Login",
                    subTitle = "Use your fingerprint",
                    negativeButtonText = "Cancel",
                    fragmentActivity = activity,
                    onSuccess = {
                        message = "Success"
                        onSuccess()
                    },
                    onError = { errorCode, errorString ->
                        message = errorString.toString()
                        onError(errorCode, errorString.toString())
                    },
                    onFailed = {
                        message = "Verification error"
                        onFailed()
                    }
                )
            }
        ) {
            Text(text = "Sign in with fingerprint")
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = message)
    }
}

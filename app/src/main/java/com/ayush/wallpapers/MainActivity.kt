package com.ayush.wallpapers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NetworkWifi1Bar
import androidx.compose.material.icons.filled.SignalWifiConnectedNoInternet4
import androidx.compose.material.icons.filled.SignalWifiStatusbarConnectedNoInternet4
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.ayush.wallpapers.network.ConnectivityObserver
import com.ayush.wallpapers.network.NetworkConnectivityObserver
import com.ayush.wallpapers.screens.NavGraphModel
import com.ayush.wallpapers.screens.NetworkIconStatus
import com.ayush.wallpapers.screens.internetStatusUpdate
import com.ayush.wallpapers.ui.theme.WallpapersTheme


class MainActivity : ComponentActivity() {

    private lateinit var connectivityObserver: ConnectivityObserver

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContent {
            val status by connectivityObserver.observer()
                .collectAsState(initial = ConnectivityObserver.Status.Unavailable)
            val scope = rememberCoroutineScope()

            val snackBarHostState = remember { SnackbarHostState() }

            WallpapersTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = {
                                Text(
                                    text = getString(R.string.my_wallpapers),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    textDecoration = TextDecoration.None,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                actions = {
                                    IconButton(onClick = {
                                        internetStatusUpdate(scope, snackBarHostState, status)
                                    }) {
                                        when (status) {
                                            ConnectivityObserver.Status.Unavailable -> {
                                                NetworkIconStatus(
                                                    imageVector = Icons.Filled.SignalWifiStatusbarConnectedNoInternet4
                                                )
                                            }

                                            ConnectivityObserver.Status.Available -> {
                                                NetworkIconStatus(
                                                    imageVector = Icons.Rounded.NetworkWifi
                                                )

                                            }

                                            ConnectivityObserver.Status.Losing -> {
                                                NetworkIconStatus(
                                                    imageVector = Icons.Default.NetworkWifi1Bar
                                                )

                                            }

                                            ConnectivityObserver.Status.Lost -> {
                                                NetworkIconStatus(
                                                    imageVector = Icons.Default.SignalWifiConnectedNoInternet4
                                                )

                                            }
                                        }
                                    }
                                }
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(hostState = snackBarHostState)
                        }
                    ) {
                        NavGraphModel(it)
                        internetStatusUpdate(scope, snackBarHostState, status)
                    }
                }
            }
        }
    }
}


package com.rcudev.stargazer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rcudev.stargazer.navigation.NavGraph
import com.rcudev.stargazer.navigation.Posts
import com.rcudev.stargazer.navigation.WebView
import com.rcudev.stargazer.navigation.bottomNavDestinations
import kotlinx.coroutines.launch

@Composable
internal fun AppContent() {

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        bottomBar = {
            NavigationBar {
                val entry by navController.currentBackStackEntryAsState()
                var currentDestination = entry?.destination

                bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination?.route?.contains(
                            destination::class.qualifiedName ?: ""
                        ) == true,
                        icon = {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = destination.name
                            )
                        },
                        label = {
                            Text(
                                text = destination.name
                            )
                        },
                        onClick = {
                            navController.navigate(destination) {
                                popUpTo(Posts)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(
                navController = navController,
                showSnackBar = { message ->
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                openWebView = { url ->
                    navController.navigate(WebView(url = url))
                }
            )
        }
    }
}

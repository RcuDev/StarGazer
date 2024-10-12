package com.rcudev.stargazer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rcudev.stargazer.navigation.NavGraph
import com.rcudev.stargazer.navigation.WebView
import com.rcudev.stargazer.ui.components.TopBar
import kotlinx.coroutines.launch

@Composable
internal fun AppContent(
    finishSplash: () -> Unit = {}
) {

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val showBackButton by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route?.contains(
                other = WebView::class.qualifiedName.orEmpty()
            ) == true
        }
    }
    val showSettings = remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        topBar = {
            TopBar(
                showBackButton = showBackButton,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        floatingActionButton = {
            if (!showBackButton) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    content = {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    },
                    onClick = {
                        showSettings.value = !showSettings.value
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavGraph(
                navController = navController,
                showSettings = {
                    showSettings.value
                },
                hideSettings = {
                    showSettings.value = false
                },
                showSnackBar = { message ->
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = message,
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                finishSplash = finishSplash
            )
        }
    }
}

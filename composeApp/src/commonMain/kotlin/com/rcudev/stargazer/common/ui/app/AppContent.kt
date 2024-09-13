package com.rcudev.stargazer.common.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rcudev.stargazer.navigation.Articles
import com.rcudev.stargazer.navigation.NavGraph
import com.rcudev.stargazer.navigation.WebView
import com.rcudev.stargazer.navigation.bottomNavDestinations
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun AppContent(
    vm: AppViewModel = koinInject(),
) {

    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    val newsSites by vm.newsSites.collectAsState()

    var showDropdown by remember { mutableStateOf(false) }

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
                                Icons.Filled.Home,
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
                                popUpTo(Articles)
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

        NavGraph(
            navController = navController,
            innerPadding = innerPadding,
            onFilterClick = {
                showDropdown to newsSites
            },
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
            },
            hideDropDown = {
                showDropdown = false
            }
        )
    }
}

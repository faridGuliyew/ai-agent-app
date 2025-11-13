package com.example.agentapp.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.agentapp.presentation.AppViewModel
import com.example.agentapp.presentation.screens.explore.ExploreScreen
import com.example.agentapp.presentation.screens.home.HomeScreen
import com.example.agentapp.presentation.screens.profile.ProfileScreen
import com.example.agentapp.presentation.agent.AgentWidget

@Composable
fun AppNavigation(
    appViewModel: AppViewModel,
    navController: NavHostController,
    onAIInput: suspend (String) -> String
) {
    Box {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            bottomBar = {
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            navController.navigateAndClearStack(Routes.HOME)
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = null
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.EXPLORE,
                        onClick = {
                            navController.navigateAndClearStack(Routes.EXPLORE)
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Explore") },
                        label = null
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.PROFILE,
                        onClick = {
                            navController.navigateAndClearStack(Routes.PROFILE)
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = null
                    )
                }
            }
        ) { inner ->
            NavHost(
                modifier = Modifier.padding(inner),
                navController = navController,
                startDestination = Routes.HOME,
                enterTransition = {
//                    slideInHorizontally { -it } +
                            fadeIn()
                },
                exitTransition = {
//                    slideOutHorizontally { -it } +
                            fadeOut()
                }
            ) {
                composable(Routes.HOME) { HomeScreen() }
                composable(Routes.EXPLORE) { ExploreScreen() }
                composable(Routes.PROFILE) { ProfileScreen() }
            }
        }

        AgentWidget(
            modifier = Modifier.align(Alignment.BottomCenter),
            onMessageSent = onAIInput
        )
    }

}
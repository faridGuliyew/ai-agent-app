package com.example.agentapp.presentation

import android.os.Bundle
import android.util.Log.e
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.model.agent.InstructionEvent
import com.example.agentapp.domain.model.agent.InstructionModel
import com.example.agentapp.domain.model.agent.supportedRoutes
import com.example.agentapp.domain.repository.AgentRepository
import com.example.agentapp.presentation.navigation.AppNavigation
import com.example.agentapp.presentation.navigation.navigateAndClearStack
import com.example.agentapp.presentation.theme.AgentAppTheme
import com.example.agentapp.utils.InstructionChannel
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val agentRepository: AgentRepository by inject()
        val instructionChannel: InstructionChannel<EventData, String> by inject()

        setContent {
            val navController = rememberNavController()

            val appViewModel: AppViewModel = viewModel()
            LaunchedEffect(Unit) { appViewModel.navController = navController }

            AgentAppTheme {
                AppNavigation(
                    appViewModel = appViewModel,
                    navController = navController,
                    onAIInput = {
                        val instruction = agentRepository.resolveInput(it)
                            .onFailure { println("Failed: ${it.message}") }.getOrNull()
                            ?: return@AppNavigation "Fatal error occurred."

                        instruction.handle(navController, instructionChannel)
                            .joinToString("") { "- $it\n" }.trim()
                    }
                )
            }
        }
    }
}

suspend fun InstructionModel.handle(
    navController: NavHostController,
    instructionChannel: InstructionChannel<EventData, String>
): List<String> {
    if (actions.isEmpty()) return listOf(failReason ?: "Unknown error occurred.")

    return actions.map {
        delay(50)
        run {
            runCatching {
                when (it.event) {
                    InstructionEvent.NAVIGATION -> {
                        navController.navigateAndClearStack((it.data as EventData.Navigation).targetRoute)
                        "Navigation done"
                    }

                    InstructionEvent.MODIFY_NOTE -> {
                        val data = it.data as EventData.ModifyNote
                        // Navigate to first screen that supports given operation
                        navController.navigateAndClearStack(data.operationType.supportedRoutes.first())
                        delay(100)
                        instructionChannel.send(data)
                    }

                    InstructionEvent.UPDATE_FIELD -> {
                        val data = it.data as EventData.UpdateField
                        // Navigate to first screen that supports given operation
                        navController.navigateAndClearStack(data.key.supportedRoutes.first())
                        delay(100)
                        instructionChannel.send(data)
                    }

                    InstructionEvent.VIEW -> {
                        val data = it.data as EventData.View
                        // Navigate to first screen that supports given operation
                        navController.navigateAndClearStack(data.key.supportedRoutes.first())
                        delay(100)
                        instructionChannel.send(data)
                    }
                }
            }.getOrNull() ?: "Failed to perform ${it.event} event"
        }
    }
}
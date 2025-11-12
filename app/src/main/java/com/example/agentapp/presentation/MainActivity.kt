package com.example.agentapp.presentation

import android.os.Bundle
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
import com.example.agentapp.domain.repository.AgentRepository
import com.example.agentapp.presentation.navigation.AppNavigation
import com.example.agentapp.presentation.navigation.navigateAndClearStack
import com.example.agentapp.presentation.theme.AgentAppTheme
import com.example.agentapp.utils.FeedbackChannel
import com.example.agentapp.utils.fieldIdToRoute
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val agentRepository: AgentRepository by inject()
        val feedbackChannel: FeedbackChannel<EventData, String> by inject()

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
                            ?: return@AppNavigation "Error occurred."

                        instruction.handle(
                            navController,
                            appViewModel,
                            feedbackChannel = feedbackChannel
                        ).joinToString("") { "- $it\n" }.trim()
                    }
                )
            }
        }
    }
}

suspend fun InstructionModel.handle(
    navController: NavHostController,
    appViewModel: AppViewModel,
    feedbackChannel: FeedbackChannel<EventData, String>
): List<String> {
    return actions.map {
        delay(50)
        run {
            runCatching {
                when (it.event) {
                    InstructionEvent.NAVIGATION -> {
                        navController.navigateAndClearStack((it.data as EventData.Navigation).targetRoute)
                        "Navigation done"
                    }

                    InstructionEvent.ADD_NOTE -> {
                        val data = it.data as EventData.AddNote
                        appViewModel.createNote(data.title ?: "Untitled", data.content.orEmpty())
                        "Added note"
                    }

                    InstructionEvent.UPDATE_NOTE -> {
                        val data = it.data as EventData.UpdateNote
                        appViewModel.updateNoteByTitle(
                            data.pastTitle,
                            data.newTitle,
                            data.newContent,
                            data.isFavorite
                        )
                        "Note is updated"
                    }

                    InstructionEvent.DELETE_NOTE -> {
                        val data = it.data as EventData.DeleteNote
                        appViewModel.deleteNoteByTitle(data.noteTitle)
                        "Note is deleted"
                    }

                    InstructionEvent.UPDATE_FIELD -> {
                        val data = it.data as EventData.UpdateField
                        val targetRoute = fieldIdToRoute(data.fieldId)
                            ?: return@run "Did not understand field ${data.fieldId}"

                        navController.navigateAndClearStack(targetRoute)
                        delay(100)
                        val feedback = feedbackChannel.send(data)
                        feedback ?: "Failed to perform ${it.event}"
                    }

                    InstructionEvent.VIEW -> {
                        val data = it.data as EventData.View
                        "I do not understand it yet."
                    }
                }
            }.getOrNull() ?: "Failed to perform ${it.event} event"
        }
    }
}
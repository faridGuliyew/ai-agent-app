package com.example.agentapp.presentation

import androidx.lifecycle.ViewModel
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.utils.InstructionChannel

abstract class AgentViewModel (
    private val instructionChannel: InstructionChannel<EventData, String>,
    private val screenRoute: String
) : ViewModel() {

    init {
        addInstructionObserver()
    }

    private fun addInstructionObserver() {
        instructionChannel.addObserver(
            tag = screenRoute,
            block = ::handleAgentEvent
        )
    }

    abstract suspend fun handleAgentEvent(data: EventData) : String?

    override fun onCleared() {
        super.onCleared()
        instructionChannel.removeObserver(screenRoute)
    }
}
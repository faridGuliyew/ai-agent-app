package com.example.agentapp.di

import com.example.agentapp.data.repository.AgentRepositoryImpl
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.repository.AgentRepository
import com.example.agentapp.utils.FeedbackChannel
import org.koin.dsl.module

val appModule = module {
    single<AgentRepository> { AgentRepositoryImpl(client = get()) }
    single<FeedbackChannel<EventData, String>> { FeedbackChannel() }
}
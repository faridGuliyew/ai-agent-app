package com.example.agentapp.di

import com.example.agentapp.data.repository.AgentRepositoryImpl
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.repository.AgentRepository
import com.example.agentapp.presentation.screens.explore.ExploreViewModel
import com.example.agentapp.presentation.screens.home.HomeViewModel
import com.example.agentapp.presentation.screens.profile.ProfileViewModel
import com.example.agentapp.utils.InstructionChannel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single<AgentRepository> { AgentRepositoryImpl(client = get()) }
    single<InstructionChannel<EventData, String>> { InstructionChannel() }

    viewModelOf(::HomeViewModel)
    viewModelOf(::ExploreViewModel)
    viewModelOf(::ProfileViewModel)
}
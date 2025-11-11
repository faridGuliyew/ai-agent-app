package com.example.agentapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.agentapp.domain.repository.AgentRepository
import com.example.agentapp.presentation.theme.AgentAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val agentRepository: AgentRepository by inject()
        val scope = CoroutineScope(Dispatchers.IO)
        var output by mutableStateOf("")

        scope.launch {
            agentRepository.resolveInput("Yo man, pls go to profile screen, thx in advance lol.")
                .onSuccess {
                    output = "Success: $it"
                }.onFailure {
                    output = "Sorry, I did not understand your request fully :/"
                }
        }

        setContent {
            AgentAppTheme {
                Box(modifier = Modifier.systemBarsPadding()) {
                    HomeScreen()
                    Text("Output: $output")
                }
            }
        }
    }
}
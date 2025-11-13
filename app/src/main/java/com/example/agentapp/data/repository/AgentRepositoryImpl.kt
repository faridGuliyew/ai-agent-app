package com.example.agentapp.data.repository

import com.example.agentapp.data.model.request.agent.GenerateRequest
import com.example.agentapp.data.model.response.agent.GenerateResponse
import com.example.agentapp.di.json
import com.example.agentapp.domain.model.agent.InstructionModel
import com.example.agentapp.domain.repository.AgentRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readUTF8Line

class AgentRepositoryImpl(private val client: HttpClient) : AgentRepository {

    companion object {
        const val AGENT_BASE_URL = "http://host:port/api/generate"
        const val AGENT_MODEL = "YOUR_MODEL_HERE"
    }

    override suspend fun resolveInput(input: String): Result<InstructionModel> {
        return runCatching {
            val response = client.post(AGENT_BASE_URL) {
                setBody(GenerateRequest(model = AGENT_MODEL, prompt = input))
            }

            val responseChannel = response.bodyAsChannel()

            var modelOutput = ""
            while (!responseChannel.isClosedForRead) {
                val line = responseChannel.readUTF8Line(Int.MAX_VALUE) ?: break
                if (line.isNotBlank()) {
                    val chunk = json.decodeFromString<GenerateResponse>(line)
//                    print(chunk.response) // stream output
                    modelOutput += chunk.response
                }
            }
            print("modelOutput: $modelOutput")

            json.decodeFromString<InstructionModel>(modelOutput)
        }
    }
}
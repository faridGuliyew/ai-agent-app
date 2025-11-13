package com.example.agentapp.data.serializer

import com.example.agentapp.domain.model.agent.AgentAction
import com.example.agentapp.domain.model.agent.EventData
import com.example.agentapp.domain.model.agent.InstructionEvent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

object AgentActionSerializer : KSerializer<AgentAction> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("AgentAction") {
            element<String>("event")
            element<JsonElement>("data")
        }

    override fun serialize(encoder: Encoder, value: AgentAction) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("AgentActionSerializer only supports JSON")

        val json = buildJsonObject {
            put("event", Json.encodeToJsonElement(value.event))
            put("data", when (value.event) {
                InstructionEvent.NAVIGATION ->
                    Json.encodeToJsonElement(EventData.Navigation.serializer(), value.data as EventData.Navigation)
                InstructionEvent.MODIFY_NOTE -> {
                    Json.encodeToJsonElement(EventData.ModifyNote.serializer(), value.data as EventData.ModifyNote)
                }
                InstructionEvent.UPDATE_FIELD ->
                    Json.encodeToJsonElement(EventData.UpdateField.serializer(), value.data as EventData.UpdateField)
                InstructionEvent.VIEW ->
                    Json.encodeToJsonElement(EventData.View.serializer(), value.data as EventData.View)
            })
        }

        jsonEncoder.encodeJsonElement(json)
    }

    override fun deserialize(decoder: Decoder): AgentAction {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("AgentActionSerializer only supports JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        val event = Json.decodeFromJsonElement<InstructionEvent>(jsonObject["event"]!!)
        val dataElement = jsonObject["data"]!!

        val data : EventData = when (event) {
            InstructionEvent.NAVIGATION ->
                Json.decodeFromJsonElement(EventData.Navigation.serializer(), dataElement)
            InstructionEvent.MODIFY_NOTE -> Json.decodeFromJsonElement(EventData.ModifyNote.serializer(), dataElement)
            InstructionEvent.UPDATE_FIELD -> Json.decodeFromJsonElement(EventData.UpdateField.serializer(), dataElement)
            InstructionEvent.VIEW -> Json.decodeFromJsonElement(EventData.View.serializer(), dataElement)
        }

        return AgentAction(event, data)
    }
}

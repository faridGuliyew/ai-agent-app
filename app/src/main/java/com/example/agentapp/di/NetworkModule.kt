package com.example.agentapp.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

val networkModule = module {
    single {
        HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
            install(DefaultRequest) {
                contentType(ContentType.Application.Json)
            }
//            engine {
//                addInterceptor { chain ->
//                    val request = chain.request()
//                    println("Request: $request")
//                    val response = chain.proceed(request)
//                    println("Response: ${response.body?.bytes()?.decodeToString()}")
//
//                    response
//                }
//            }
        }
    }
}
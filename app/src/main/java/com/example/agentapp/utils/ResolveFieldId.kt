package com.example.agentapp.utils

val profileFields = setOf("BIO", "USERNAME")

fun fieldIdToRoute(fieldId: String): String? {
    return when (fieldId) {
        in profileFields -> "PROFILE"
        else -> null
    }
}
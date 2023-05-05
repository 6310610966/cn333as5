package com.example.phonebook.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//keep static
sealed class Screen {
    object Phones: Screen()
    object SavePhone: Screen()
    object Trash: Screen()
}

object PhoneBookRouter {
    var currentScreen: Screen by mutableStateOf(Screen.Phones)

    fun navigateTo(destination: Screen) {
        currentScreen = destination
    }
}
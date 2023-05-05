package com.example.phonebook.domain.model

const val NEW_PHONE_ID = -1L

data class PhoneModel(
    val id: Long = NEW_PHONE_ID, // This value is used for new phones
    val name: String = "",
    val phoneNumber: String = "",
    val tag: TagModel = TagModel.DEFAULT
)
package com.example.phonebook.domain.model

import com.example.phonebook.database.TagDbModel

//ColorModel and ColorDbModel are same thing but ColorModel consistence with PhoneModel
data class TagModel(
    val id: Long,
    val nameTag: String,
) {
    companion object {
        val DEFAULT = with(TagDbModel.DEFAULT_TAG) { TagModel(id, nameTag) }
    }
}


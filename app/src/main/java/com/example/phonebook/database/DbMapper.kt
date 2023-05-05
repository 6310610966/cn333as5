package com.example.phonebook.database

import com.example.phonebook.domain.model.NEW_PHONE_ID
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel

class DbMapper {
    // Create list of NoteModels by pairing each note with a tag
    fun mapPhones(
        phoneDbModels: List<PhoneDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<PhoneModel> = phoneDbModels.map {
        val tagDbModel = tagDbModels[it.tagId]
            ?: throw RuntimeException("Tag for tagId: ${it.tagId} was not found. Make sure that all tags are passed to this method")

        mapPhone(it, tagDbModel)
    }

    // convert NoteDbModel to NoteModel
    fun mapPhone(phoneDbModel: PhoneDbModel, tagDbModel: TagDbModel): PhoneModel {
        val tag = mapTag(tagDbModel)
        return with(phoneDbModel) { PhoneModel(id, name, phoneNumber, tag) }
    }

    // convert list of TagDdModels to list of TagModels
    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    // convert TagDbModel to TagModel
    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, nameTag) }

    // convert PhoneModel back to PhoneDbModel
    fun mapDbPhone(phone: PhoneModel): PhoneDbModel =
        with(phone) {
            if (id == NEW_PHONE_ID)
                PhoneDbModel(
                    name = name,
                    phoneNumber = phoneNumber,
                    tagId = tag.id,
                    isInTrash = false
                )
            else
                PhoneDbModel(id, name, phoneNumber, tag.id, false)
        }
}
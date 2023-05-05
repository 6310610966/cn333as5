package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "nameTag") val nameTag: String
) {
    companion object {
        val DEFAULT_TAGS = listOf(
            TagDbModel(1, "Mobile"),
            TagDbModel(2, "Home"),
            TagDbModel(3, "Work"),
        )
        val DEFAULT_TAG = DEFAULT_TAGS[0]
    }
}

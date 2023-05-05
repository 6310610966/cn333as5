package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.phonebook.domain.model.PhoneModel

@Entity
data class PhoneDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
) {
    //Data for insert to database
    // static variable
    companion object {
        val DEFAULT_PHONES = listOf(
            PhoneDbModel(1, "Emma", "0966964521", 1, false),
            PhoneDbModel(2, "Hannah", "080495665", 2, false),
            PhoneDbModel(3, "Isabella", "0835192628", 3, false),
        )
    }
}
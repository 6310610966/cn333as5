package com.example.phonebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhoneDao {

    @Query("SELECT * FROM PhoneDbModel")
    fun getAllSync(): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE id IN (:phoneIds)")
    fun getPhonesByIdsSync(phoneIds: List<Long>): List<PhoneDbModel>

    @Query("SELECT * FROM PhoneDbModel WHERE id LIKE :id")
    fun findByIdSync(id: Long): PhoneDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneDbModel: PhoneDbModel)

    @Insert
    fun insertAll(vararg phoneDbModel: PhoneDbModel)

    @Query("DELETE FROM PhoneDbModel WHERE id LIKE :id")
    fun delete(id: Long)

    @Query("DELETE FROM PhoneDbModel WHERE id IN (:phoneIds)")
    fun delete(phoneIds: List<Long>)
}
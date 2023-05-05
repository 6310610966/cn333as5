package com.example.phonebook.database

//connect DB and ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Repository(
    private val phoneDao: PhoneDao,
    private val tagDao: TagDao,
    private val dbMapper: DbMapper
) {

    // Working Phones
    private val phonesNotInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesNotInTrash(): LiveData<List<PhoneModel>> = phonesNotInTrashLiveData

    // Deleted Phones
    private val phonesInTrashLiveData: MutableLiveData<List<PhoneModel>> by lazy {
        MutableLiveData<List<PhoneModel>>()
    }

    fun getAllPhonesInTrash(): LiveData<List<PhoneModel>> = phonesInTrashLiveData

    init {
        initDatabase(this::updatePhonesLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
            val dbTags = tagDao.getAllSync()
            if (dbTags.isNullOrEmpty()) {
                tagDao.insertAll(*tags)
            }

            // Prepopulate phones
            val phones = PhoneDbModel.DEFAULT_PHONES.toTypedArray()
            val dbPhones = phoneDao.getAllSync()
            if (dbPhones.isNullOrEmpty()) {
                phoneDao.insertAll(*phones)
            }

            postInitAction.invoke()
        }
    }

    // get list of working Phones or deleted Phones
    private fun getAllPhonesDependingOnTrashStateSync(inTrash: Boolean): List<PhoneModel> {
        val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().map { it.id to it }.toMap()
        val dbPhones: List<PhoneDbModel> =
            phoneDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapPhones(dbPhones, tagDbModels)
    }

    fun insertPhone(phone: PhoneModel) {
        phoneDao.insert(dbMapper.mapDbPhone(phone))
        updatePhonesLiveData()
    }

    fun deletePhones(phoneIds: List<Long>) {
        phoneDao.delete(phoneIds)
        updatePhonesLiveData()
    }

    fun movePhoneToTrash(phoneId: Long) {
        val dbPhone = phoneDao.findByIdSync(phoneId)
        val newDbPhone = dbPhone.copy(isInTrash = true)
        phoneDao.insert(newDbPhone)
        updatePhonesLiveData()
    }

    fun restorePhonesFromTrash(phoneIds: List<Long>) {
        val dbPhonesInTrash = phoneDao.getPhonesByIdsSync(phoneIds)
        dbPhonesInTrash.forEach {
            val newDbPhone = it.copy(isInTrash = false)
            phoneDao.insert(newDbPhone)
        }
        updatePhonesLiveData()
    }

    fun getAllColors(): LiveData<List<TagModel>> =
        Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }

    private fun updatePhonesLiveData() {
        //phonesNotInTrashLiveData.value = (getAllPhonesDependingOnTrashStateSync(false))
        phonesNotInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(false))
        phonesInTrashLiveData.postValue(getAllPhonesDependingOnTrashStateSync(true))
    }
}
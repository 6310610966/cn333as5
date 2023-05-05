package com.example.phonebook.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phonebook.database.AppDatabase
import com.example.phonebook.database.DbMapper
import com.example.phonebook.database.Repository
import com.example.phonebook.domain.model.PhoneModel
import com.example.phonebook.domain.model.TagModel
import com.example.phonebook.routing.PhoneBookRouter
import com.example.phonebook.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(application: Application) : ViewModel() {
    val phonesNotInTrash: LiveData<List<PhoneModel>> by lazy {
        repository.getAllPhonesNotInTrash()
    }

    private var _phoneEntry = MutableLiveData(PhoneModel())

    val phoneEntry: LiveData<PhoneModel> = _phoneEntry

    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllColors()
    }

    val phonesInTrash by lazy { repository.getAllPhonesInTrash() }

    private var _selectedPhones = MutableLiveData<List<PhoneModel>>(listOf())

    val selectedPhones: LiveData<List<PhoneModel>> = _selectedPhones

    private val repository: Repository

    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.phoneDao(), db.tagDao(), DbMapper())
    }

    fun onCreateNewPhoneClick() {
        _phoneEntry.value = PhoneModel()
        PhoneBookRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneClick(phone: PhoneModel) {
        _phoneEntry.value = phone
        PhoneBookRouter.navigateTo(Screen.SavePhone)
    }

    fun onPhoneCheckedChange(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)
        }
    }

    fun onPhoneSelected(phone: PhoneModel) {
        _selectedPhones.value = _selectedPhones.value!!.toMutableList().apply {
            if (contains(phone)) {
                remove(phone)
            } else {
                add(phone)
            }
        }
    }

    fun restorePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restorePhonesFromTrash(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun permanentlyDeletePhones(phones: List<PhoneModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deletePhones(phones.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedPhones.value = listOf()
            }
        }
    }

    fun onPhoneEntryChange(phone: PhoneModel) {
        _phoneEntry.value = phone
    }

    fun savePhone(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertPhone(phone)

            withContext(Dispatchers.Main) {
                PhoneBookRouter.navigateTo(Screen.Phones)

                _phoneEntry.value = PhoneModel()
            }
        }
    }

    fun movePhoneToTrash(phone: PhoneModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.movePhoneToTrash(phone.id)

            withContext(Dispatchers.Main) {
                PhoneBookRouter.navigateTo(Screen.Phones)
            }
        }
    }
}
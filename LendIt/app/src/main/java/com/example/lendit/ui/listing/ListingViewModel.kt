// ListingsViewModel.kt
package com.example.lendit.ui.listing

import ListingEntity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListingViewModel : ViewModel() {

    private val _listings = MutableLiveData<List<ListingEntity>>()
    val listings: LiveData<List<ListingEntity>> get() = _listings

}

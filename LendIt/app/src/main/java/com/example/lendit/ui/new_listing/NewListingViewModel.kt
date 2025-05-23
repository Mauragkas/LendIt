// ListingsViewModel.kt
package com.example.lendit.ui.new_listing

import EquipmentListing
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewListingViewModel : ViewModel() {

    private val _listings = MutableLiveData<List<EquipmentListing>>()
    val listings: LiveData<List<EquipmentListing>> get() = _listings

}

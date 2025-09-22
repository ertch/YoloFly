package com.example.butterflydetector.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpeciesCatalogViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the Speciescatalog Fragment"
    }
    val text: LiveData<String> = _text
}
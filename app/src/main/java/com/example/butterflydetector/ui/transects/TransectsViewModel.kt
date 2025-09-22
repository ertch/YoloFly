package com.example.butterflydetector.ui.transects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransectsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Transects"
    }
    val text: LiveData<String> = _text
}

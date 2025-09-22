package com.example.butterflydetector.ui.transectwalks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransectwalksViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is the Transectwalks Fragment"
    }
    val text: LiveData<String> = _text
}

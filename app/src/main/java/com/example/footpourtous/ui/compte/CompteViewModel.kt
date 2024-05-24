package com.example.footpourtous.ui.compte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CompteViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is compte Fragment"
    }
    val text: LiveData<String> = _text
}
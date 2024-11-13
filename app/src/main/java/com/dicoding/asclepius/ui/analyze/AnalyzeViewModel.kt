package com.dicoding.asclepius.ui.analyze

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnalyzeViewModel : ViewModel() {

    val currentImageUri: MutableLiveData<Uri?> = MutableLiveData()
}
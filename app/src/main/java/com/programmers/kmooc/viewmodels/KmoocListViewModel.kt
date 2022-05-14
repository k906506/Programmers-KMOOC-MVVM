package com.programmers.kmooc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _lectures = MutableLiveData<LectureList>()
    val lectures: LiveData<LectureList> get() = _lectures

    fun list() {
        _isLoading.postValue(true)
        repository.list { lectureList ->
            _lectures.postValue(lectureList)
            _isLoading.postValue(false)
        }
    }

    fun next() {
        _isLoading.postValue(true)
        // 강의 목록이 없으면 그냥 return
        val currentLectureList = _lectures.value ?: return
        repository.next(currentLectureList) { lectureList ->
            currentLectureList.lectures.toMutableList().apply {
                addAll(lectureList.lectures)
            }
            _lectures.postValue(currentLectureList)
            _isLoading.postValue(false)
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}
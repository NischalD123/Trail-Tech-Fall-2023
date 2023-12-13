package edu.vt.smarttrail.db

import android.os.AsyncTask
import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * Viewmodel allowing interaction with TakenSurveyRepository and corresponding database
 */
class TakenSurveyViewModel(private val repository: TakenSurveyRepository) : ViewModel() {

    // Using LiveData and caching what allTakenSurveys returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allTakenSurveys: LiveData<List<TakenSurvey>> = repository.allSurveys.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(takenSurvey: TakenSurvey): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            val data = repository.insert(takenSurvey)
            result.value = data
//            Log.d("TSVM", "data: ${data}")
        }
        return result
    }

    fun update(takenSurvey: TakenSurvey) {
        AsyncTask.execute {
            repository.update(takenSurvey)
        }
    }
}

/**
 * Generates viewmodel
 */
class TakenSurveyViewModelFactory(private val repository: TakenSurveyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TakenSurveyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TakenSurveyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package edu.vt.smarttrail.db

import androidx.lifecycle.*
import kotlinx.coroutines.launch

/**
 * Viewmodel allowing interaction with QuestionRepository and question database
 */
class QuestionViewModel(private val repository: QuestionRepository) : ViewModel() {

    // Using LiveData and caching what allQuestions returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
//    val allQuestions: LiveData<List<Question>> = repository.allQuestions.asLiveData()
    val s1Questions: LiveData<List<Question>> = repository.s1Questions.asLiveData()
    val s2Questions: LiveData<List<Question>> = repository.s2Questions.asLiveData()
    val s3Questions: LiveData<List<Question>> = repository.s3Questions.asLiveData()
    val s4Questions: LiveData<List<Question>> = repository.s4Questions.asLiveData()
    val s5Questions: LiveData<List<Question>> = repository.s5Questions.asLiveData()
    val s6Questions: LiveData<List<Question>> = repository.s6Questions.asLiveData()
    val s7Questions: LiveData<List<Question>> = repository.s7Questions.asLiveData()
    val s8Questions: LiveData<List<Question>> = repository.s8Questions.asLiveData()
    val s9Questions: LiveData<List<Question>> = repository.s9Questions.asLiveData()
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(question: Question) = viewModelScope.launch {
        repository.insert(question)
    }
}

/**
 * Creates the new viewmodel
 */
class QuestionViewModelFactory(private val repository: QuestionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

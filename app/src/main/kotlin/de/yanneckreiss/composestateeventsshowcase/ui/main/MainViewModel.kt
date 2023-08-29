package de.yanneckreiss.composestateeventsshowcase.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(MainViewState())
    val viewState = _viewState.asStateFlow()

    fun startProcess(useTimestamp: Boolean, dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate) {

        viewModelScope.launch(dispatcher) {

            _viewState.update { currentState -> currentState.copy(isLoading = true) }

            delay(3_000)

            if (useTimestamp) {
                _viewState.update { currentState ->
                    currentState.copy(
                        processSuccessWithTimestampEvent = triggered(SimpleDateFormat.getTimeInstance().format(Date())),
                        isLoading = false
                    )
                }
            } else {
                _viewState.update { currentState ->
                    currentState.copy(
                        processSuccessEvent = triggered,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setShowMessageConsumed() {
        _viewState.update { currentState ->
            currentState.copy(
                processSuccessEvent = consumed,
                processSuccessWithTimestampEvent = consumed()
            )
        }
    }
}

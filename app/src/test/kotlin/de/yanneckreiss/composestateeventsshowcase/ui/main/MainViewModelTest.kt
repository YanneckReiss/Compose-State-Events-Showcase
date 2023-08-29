@file:OptIn(ExperimentalCoroutinesApi::class)

package de.yanneckreiss.composestateeventsshowcase.ui.main

import app.cash.turbine.test
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.StateEventWithContentConsumed
import de.palm.composestateevents.StateEventWithContentTriggered
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import de.yanneckreiss.composestateeventsshowcase.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun `Loading state should be correctly set when executing the process`() = runTest {

        viewModel.viewState.test {
            // State before process start
            assertFalse(awaitItem().isLoading)
            viewModel.startProcess(useTimestamp = false)

            // State after process start
            assertTrue(awaitItem().isLoading)

            // State after process completion
            assertFalse(awaitItem().isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ViewState process Success event should be triggered after a process run`() = runTest {

        viewModel.startProcess(useTimestamp = false)

        advanceUntilIdle()

        assertEquals(
            viewModel.viewState.value.processSuccessEvent,
            StateEvent.Triggered
        )
    }

    @Test
    fun `ViewState process Success should be correctly be reset to consumed`() = runTest {
        viewModel.viewState.test {
            val viewStateBeforeProcessing: MainViewState = awaitItem()
            assertEquals(viewStateBeforeProcessing, viewStateBeforeProcessing.copy(processSuccessEvent = consumed))
            viewModel.startProcess(useTimestamp = false)

            val viewStateAfterProcessingStart: MainViewState = awaitItem()
            assertEquals(viewStateAfterProcessingStart, viewStateAfterProcessingStart.copy(isLoading = true))
            viewModel.setShowMessageConsumed()

            val viewStateAfterConsumption: MainViewState = awaitItem()
            assertEquals(viewStateAfterConsumption, viewStateAfterConsumption.copy(processSuccessEvent = consumed))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ViewState process Success with timestamp event should be triggered after a successful process run`() = runTest {

        viewModel.startProcess(useTimestamp = true)
        advanceUntilIdle()

        assertTrue(viewModel.viewState.value.processSuccessWithTimestampEvent is StateEventWithContentTriggered)
        assertTrue(viewModel.viewState.value.processSuccessEvent is StateEvent.Consumed)
    }
}

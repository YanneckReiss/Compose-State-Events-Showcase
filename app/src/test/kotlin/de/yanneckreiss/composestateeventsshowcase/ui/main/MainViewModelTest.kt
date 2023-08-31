@file:OptIn(ExperimentalCoroutinesApi::class)

package de.yanneckreiss.composestateeventsshowcase.ui.main

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.StateEventWithContentConsumed
import de.palm.composestateevents.StateEventWithContentTriggered
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import de.yanneckreiss.composestateeventsshowcase.MainDispatcherRule
import de.yanneckreiss.composestateeventsshowcase.data.time_provider.TimeProvider
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private val testTimeStamp = "12:00:00"
    private val timeProvider = object : TimeProvider {
        override fun getTimestampFromNow(): String = testTimeStamp
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = MainViewModel(timeProvider)
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
    fun `ViewState process Success should be correctly be reset to consumed`() = runTest(StandardTestDispatcher()) {

        viewModel.viewState.test {
            awaitItem()
            viewModel.startProcess(useTimestamp = false)

            awaitItem() // Loading state update
            assertEquals(awaitItem().processSuccessEvent, StateEvent.Triggered)
            viewModel.setShowMessageConsumed()

            assertEquals(awaitItem().processSuccessEvent, StateEvent.Consumed)

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

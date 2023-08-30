@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanneckreiss.composestateeventsshowcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import de.palm.composestateevents.EventEffect
import de.yanneckreiss.composestateeventsshowcase.data.time_provider.TimeProviderImpl
import de.yanneckreiss.composestateeventsshowcase.ui.main.MainViewModel
import de.yanneckreiss.composestateeventsshowcase.ui.main.MainViewState
import de.yanneckreiss.composestateeventsshowcase.ui.theme.ComposeStateEventsShowcaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStateEventsShowcaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    viewModel: MainViewModel = viewModel(
        modelClass = MainViewModel::class.java,
        factory = viewModelFactory { MainViewModel(TimeProviderImpl()) })
) {

    val viewState: MainViewState by viewModel.viewState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    EventEffect(
        event = viewState.processSuccessEvent,
        onConsumed = viewModel::setShowMessageConsumed
    ) {
        snackbarHostState.showSnackbar("Event success")
    }

    EventEffect(
        event = viewState.processSuccessWithTimestampEvent,
        onConsumed = viewModel::setShowMessageConsumed
    ) { timestamp ->
        snackbarHostState.showSnackbar("Event success at: $timestamp")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding: PaddingValues ->

        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            onStartProcessWithoutTimestamp = { viewModel.startProcess(useTimestamp = false) },
            onStartProcessWithTimestamp = { viewModel.startProcess(useTimestamp = true) },
            isLoading = viewState.isLoading
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    onStartProcessWithoutTimestamp: () -> Unit,
    onStartProcessWithTimestamp: () -> Unit,
    isLoading: Boolean
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopAppBar(
            title = { Text("Compose State Events Test App") }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartProcessWithoutTimestamp,
                enabled = !isLoading
            ) {
                Text(text = "Trigger event without timestamp")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onStartProcessWithTimestamp,
                enabled = !isLoading
            ) {
                Text(text = "Trigger event with timestamp")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeStateEventsShowcaseTheme {
        MainContent(
            modifier = Modifier.fillMaxSize(),
            onStartProcessWithoutTimestamp = {},
            isLoading = false,
            onStartProcessWithTimestamp = {},
        )
    }
}

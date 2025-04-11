package ru.skorobogatov.t_investsendbox.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import ru.skorobogatov.t_investsendbox.domain.entity.Candle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    component: DetailsComponent
) {
    val state by component.model.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = state.instrument.name) },
                navigationIcon = {
                    IconButton(
                        onClick = { component.onClickBack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { component.onClickChangeTimeframe() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timelapse,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { component.onClickChangePeriod() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = { component.onClickChangeFavouriteStatus() }
                    ) {
                        Icon(
                            imageVector = if (state.isFavourite) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val candleState = state.candlesState) {
            DetailsStore.State.CandlesState.Error -> {
                Text(
                    modifier = Modifier.padding(paddingValues),
                    text = "Error"
                )
            }

            DetailsStore.State.CandlesState.Initial -> {

            }

            is DetailsStore.State.CandlesState.Loaded -> {
                Terminal(
                    candleList = candleState.candlesList
                )
            }

            DetailsStore.State.CandlesState.Loading -> {

            }
        }
    }
}

@Composable
fun Terminal(
    candleList: List<Candle>
) {
    val listState = rememberLazyListState()
    var scale by remember { mutableStateOf(1f) }

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale *= zoom
                    scale = scale.coerceIn(0.5f, 3f)
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = 1f
            ),
        state = listState,
        reverseLayout = true
    ) {
        items(
            items = candleList,
            key = { it.toString() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
            ){
                Box(
                    modifier = Modifier.padding(2.dp)
                        .size(width = 8.dp, height = if (it.open> it.close) (it.open - it.close).dp else (it.close - it.open).dp)
                        .background(if (it.open > it.close) Color.Red else Color.Green)
                        .align(Alignment.Center)
                )
            }
        }
    }
}















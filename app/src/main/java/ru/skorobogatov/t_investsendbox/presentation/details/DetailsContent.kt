package ru.skorobogatov.t_investsendbox.presentation.details

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import ru.skorobogatov.t_investsendbox.domain.entity.Candle
import kotlin.math.absoluteValue

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
    var height by remember { mutableStateOf(0) }

    val visibleItemsInfo = remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo } }

    var priceRange by remember { mutableStateOf(1f) }
    var maxPrice by remember { mutableStateOf(1f) }
    var minPrice by remember { mutableStateOf(1f) }
    var pxPerPoint by remember { mutableStateOf(1f) }

    LaunchedEffect(
        key1 = visibleItemsInfo.value
    ) {
        val visibleCandles = visibleItemsInfo.value.mapNotNull { itemInfo ->
            candleList.getOrNull(itemInfo.index)
        }
        maxPrice = visibleCandles.maxOfOrNull { it.high } ?: 1f
        minPrice = visibleCandles.minOfOrNull { it.low } ?: 0f
        priceRange = (maxPrice - minPrice)
        pxPerPoint = height / priceRange
        Log.d(
            "price",
            "priceRange = $priceRange, maxPrice = $maxPrice, minPrice = $minPrice, pixelPerPrice = $pxPerPoint, height = $height"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned {
                height = it.size.height
            }
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, _, zoom, _ ->
                        scale *= zoom
                        scale = scale.coerceIn(0.5f, 3f)
                    }
                },
            state = listState,
            reverseLayout = true
        ) {
            items(
                items = candleList,
                key = { it.toString() },
            ) {
                Log.d(
                    "candle",
                    "candle.high = ${it.high}, candle.low = ${it.low}, candle.open = ${it.open}, candle.close = ${it.close}"
                )

                val wickHeight = (it.high - it.low) * pxPerPoint
                val bodyHeight = (it.open - it.close).absoluteValue * pxPerPoint

                val wickOffset = (maxPrice - it.high) * pxPerPoint
                val bodyOffset = (maxPrice - maxOf(it.open, it.close)) * pxPerPoint

                Log.d(
                    "item",
                    "wickHeight = $wickHeight, bodyHeight = $bodyHeight, wickOffset = $wickOffset, bodyOffset = $bodyOffset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(2.dp)
                        .width((8 * scale).dp)
                ) {
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        drawLine(
                            color = Color.White,
                            start = Offset(size.center.x, (maxPrice - it.high) * pxPerPoint),
                            end = Offset(size.center.x, (maxPrice - it.low) * pxPerPoint),
                            strokeWidth = 2f * scale
                        )
                        drawLine(
                            color = if (it.open > it.close) Color.Red else Color.Green,
                            start = Offset(size.center.x, (maxPrice - maxOf(it.open, it.close)) * pxPerPoint),
                            end = Offset(size.center.x, (maxPrice - minOf(it.open, it.close)) * pxPerPoint),
                            strokeWidth = 8f * scale
                        )
                    }
                }
            }
        }
    }
}

















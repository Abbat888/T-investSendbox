package ru.skorobogatov.t_investsendbox.presentation.details

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.skorobogatov.t_investsendbox.domain.entity.Candle
import ru.skorobogatov.t_investsendbox.domain.entity.Timeframe
import java.text.DateFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    component: DetailsComponent
) {
    val state by component.model.collectAsState()

    var expend by remember { mutableStateOf(false) }

    var openDialog by remember { mutableStateOf(false) }
    var dateRangePickerState = rememberDateRangePickerState()

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
        when (state.timeframeState) {
            DetailsStore.State.TimeframeState.OnChange -> {
                expend = true
                DropdownMenu(
                    expanded = expend,
                    onDismissRequest = { expend = false },
                    modifier = Modifier
                ) {
                    Timeframe.entries.forEachIndexed { index, timeFrame ->
                        if (index != 0) {
                            DropdownMenuItem(
                                text = { Text(text = timeFrame.name.removePrefix("CANDLE_INTERVAL_")) },
                                onClick = {
                                    component.onTimeFrameChanged(timeFrame)
                                    expend = false
                                }
                            )
                        }
                    }
                }
            }

            is DetailsStore.State.TimeframeState.SelectedTimeframe -> {
                when (state.periodState) {
                    DetailsStore.State.PeriodState.OnChange -> {
                        openDialog = true
                        DatePickerDialog(
                            onDismissRequest = { openDialog = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        val from = dateRangePickerState.selectedStartDateMillis
                                        val to = dateRangePickerState.selectedEndDateMillis
                                        if (from != null && to != null) {
                                            component.onPeriodChanged(
                                                from = from,
                                                to = to
                                            )
                                        }
                                        openDialog = false
                                    }
                                ) { Text(text = "Применить") }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        openDialog = false
                                    }
                                ) { Text(text = "Отмена") }
                            }
                        ) {
                            DateRangePicker(
                                state = dateRangePickerState,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    is DetailsStore.State.PeriodState.SelectedPeriod -> {
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
                                val timeframe =
                                    state.timeframeState as DetailsStore.State.TimeframeState.SelectedTimeframe
                                Terminal(
                                    candleList = candleState.candlesList.reversed(),
                                    timeframe = timeframe.timeframe
                                )
                            }

                            DetailsStore.State.CandlesState.Loading -> {

                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Terminal(
    candleList: List<Candle>,
    timeframe: Timeframe
) {
    val listState = rememberLazyListState()
    var scale by remember { mutableStateOf(1f) }
    var height by remember { mutableStateOf(0) }

    val visibleItemsInfo = remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo } }

    var priceRange by remember { mutableStateOf(1f) }
    var maxPrice by remember { mutableStateOf(1f) }
    var minPrice by remember { mutableStateOf(1f) }
    var pxPerPoint by remember { mutableStateOf(1f) }

    val textMeasurer = rememberTextMeasurer()

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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(top = 128.dp, bottom = 48.dp)
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
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(1.dp)
                            .width((8 * scale).dp)
                    ) {
                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            drawLine(
                                color = if (it.open > it.close) Color.Red else Color.Green,
                                start = Offset(size.center.x, (maxPrice - it.high) * pxPerPoint),
                                end = Offset(size.center.x, (maxPrice - it.low) * pxPerPoint),
                                strokeWidth = 4f * scale
                            )
                            drawLine(
                                color = if (it.open > it.close) Color.Red else Color.Green,
                                start = Offset(
                                    size.center.x,
                                    (maxPrice - maxOf(it.open, it.close)) * pxPerPoint
                                ),
                                end = Offset(
                                    size.center.x,
                                    (maxPrice - minOf(it.open, it.close)) * pxPerPoint
                                ),
                                strokeWidth = 16f * scale
                            )
                            val candleTime = it.time
                            val minutes = candleTime.get(Calendar.MINUTE)
                            val hours = candleTime.get(Calendar.HOUR_OF_DAY)
                            val dayOfWeek = candleTime.get(Calendar.DAY_OF_WEEK)
                            val dayOfMonth = candleTime.get(Calendar.DAY_OF_MONTH)
                            val month = candleTime.get(Calendar.MONTH)
                            val year = candleTime.get(Calendar.YEAR)
                            val nameOfMonth =
                                DateFormatSymbols(Locale.getDefault()).shortMonths[month]
                            var text = ""
                            val shouldDrawDelimiter = when (timeframe) {
                                Timeframe.CANDLE_INTERVAL_1_MIN,
                                Timeframe.CANDLE_INTERVAL_2_MIN,
                                Timeframe.CANDLE_INTERVAL_3_MIN,
                                Timeframe.CANDLE_INTERVAL_5_MIN -> {
                                    text = if (hours == 0) {
                                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                                    } else {
                                        String.format("%02d:00", hours)
                                    }
                                    minutes == 0 && hours.rem(2) == 0
                                }

                                Timeframe.CANDLE_INTERVAL_15_MIN,
                                Timeframe.CANDLE_INTERVAL_10_MIN,
                                Timeframe.CANDLE_INTERVAL_30_MIN -> {
                                    text = if (hours == 0) {
                                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                                    } else {
                                        String.format("%02d:00", hours)
                                    }
                                    minutes == 0 && hours.rem(4) == 0
                                }

                                Timeframe.CANDLE_INTERVAL_HOUR,
                                Timeframe.CANDLE_INTERVAL_2_HOUR,
                                Timeframe.CANDLE_INTERVAL_4_HOUR -> {
                                    text = if (dayOfMonth == 1) {
                                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                                    } else {
                                        String.format("%02d", dayOfMonth)
                                    }
                                    hours == 0 && dayOfMonth.rem(2) == 0
                                }

                                Timeframe.CANDLE_INTERVAL_DAY -> {
                                    text = if (dayOfMonth == 1) {
                                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                                    } else {
                                        String.format("%02d", dayOfMonth)
                                    }
                                    dayOfWeek == 1
                                }

                                Timeframe.CANDLE_INTERVAL_WEEK -> {
                                    text = if (month == 1) {
                                        String.format("%d %s", year, nameOfMonth)
                                    } else {
                                        String.format("%s", nameOfMonth)
                                    }
                                    month.rem(2) == 0
                                }

                                Timeframe.CANDLE_INTERVAL_MONTH -> {
                                    text = if (month == 1) {
                                        String.format("%d %s", year, nameOfMonth)
                                    } else {
                                        String.format("%s", nameOfMonth)
                                    }
                                    month.rem(6) == 0
                                }
                            }
                            if (shouldDrawDelimiter) {
                                drawLine(
                                    color = Color.White.copy(alpha = 0.5f),
                                    start = Offset(size.center.x, 0f),
                                    end = Offset(size.center.x, size.height),
                                    strokeWidth = 1f,
                                    pathEffect = PathEffect.dashPathEffect(
                                        floatArrayOf(
                                            4.dp.toPx(),
                                            4.dp.toPx()
                                        )
                                    )
                                )
                                val textLayoutResult = textMeasurer.measure(
                                    text = text,
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                )
                                drawText(
                                    textLayoutResult = textLayoutResult,
                                    topLeft = Offset(
                                        size.center.x - (textLayoutResult.size.width / 2),
                                        size.height
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .padding(top = 128.dp, bottom = 48.dp)
        ) {
            val lastPrice =
                if (candleList.first().open > candleList.first().close) candleList.first().open else candleList.first().close
            drawPrices(
                max = maxPrice,
                min = minPrice,
                pxPerPoint = pxPerPoint,
                lastPrice = lastPrice,
                textMeasurer = textMeasurer
            )
        }
    }
}

private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    lastPrice: Float,
    textMeasurer: TextMeasurer
) {
    //max price
    val maxPriceOffsetY = 0f
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY
    )

    //last price
    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY
    )

    //min price
    val minPericeOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPericeOffsetY),
        end = Offset(size.width, minPericeOffsetY)
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minPericeOffsetY
    )
}

private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}

















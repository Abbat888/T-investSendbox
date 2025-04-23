package ru.skorobogatov.t_investsendbox.presentation.details

import android.annotation.SuppressLint
import android.icu.util.Calendar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timelapse
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
    val dateRangePickerState = rememberDateRangePickerState()

    val oldFrom by remember {
        mutableLongStateOf(
            (state.periodState as DetailsStore.State.PeriodState.SelectedPeriod).from
        )
    }
    val oldTo by remember {
        mutableLongStateOf(
            (state.periodState as DetailsStore.State.PeriodState.SelectedPeriod).to
        )
    }

    val oldTimeframe by remember {
        mutableStateOf(
            (state.timeframeState as DetailsStore.State.TimeframeState.SelectedTimeframe).timeframe
        )
    }

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
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            component.onClickChangeTimeframe()
                            expend = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timelapse,
                            contentDescription = null
                        )
                    }
                    IconButton(
                        onClick = {
                            component.onClickChangePeriod()
                            openDialog = true
                        }
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
                            imageVector = if (state.isFavourite)
                                Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state.timeframeState) {
            DetailsStore.State.TimeframeState.OnChange -> {
                DropdownMenu(
                    expanded = expend,
                    onDismissRequest = {
                        component.onTimeFrameChanged(oldTimeframe)
                        expend = false
                    },
                    modifier = Modifier
                ) {
                    Timeframe.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(text = it.name.removePrefix("CANDLE_INTERVAL_")) },
                            onClick = {
                                component.onTimeFrameChanged(it)
                                expend = false
                            }
                        )
                    }
                }
            }

            is DetailsStore.State.TimeframeState.SelectedTimeframe -> {

            }
        }

        when (state.periodState) {
            DetailsStore.State.PeriodState.OnChange -> {
                DatePickerDialog(
                    onDismissRequest = {
                        component.onPeriodChanged(
                            from = oldFrom,
                            to = oldTo
                        )
                        openDialog = false
                    },
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
                                component.onPeriodChanged(
                                    from = oldFrom,
                                    to = oldTo
                                )
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

            }
        }

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
                    candleList = candleState.candlesList.reversed(),
                    timeframeState = state.timeframeState
                )
            }

            DetailsStore.State.CandlesState.Loading -> {

            }
        }
    }
}


@SuppressLint("DefaultLocale")
@Composable
fun Terminal(
    candleList: List<Candle>,
    timeframeState: DetailsStore.State.TimeframeState
) {
    val listState = rememberLazyListState()
    var scale by remember { mutableFloatStateOf(1f) }
    var height by remember { mutableIntStateOf(0) }

    val visibleItemsInfo = remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo } }

    var priceRange by remember { mutableFloatStateOf(1f) }
    var maxPrice by remember { mutableFloatStateOf(1f) }
    var minPrice by remember { mutableFloatStateOf(1f) }
    var pxPerPoint by remember { mutableFloatStateOf(1f) }

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
                reverseLayout = true,
                contentPadding = PaddingValues(2.dp)
            ) {
                itemsIndexed(
                    items = candleList,
                    key = { _, item -> item.toString() }
                ) { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((8 * scale).dp)
                    ) {

                        val nextCandle = if (candleList.size - 1 > index) candleList[index + 1]
                        else {
                            null
                        }

                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            drawLine(
                                color = if (item.open > item.close) Color.Red else Color.Green,
                                start = Offset(size.center.x, (maxPrice - item.high) * pxPerPoint),
                                end = Offset(size.center.x, (maxPrice - item.low) * pxPerPoint),
                                strokeWidth = 4f * scale
                            )
                            drawLine(
                                color = if (item.open > item.close) Color.Red else Color.Green,
                                start = Offset(
                                    size.center.x,
                                    (maxPrice - maxOf(item.open, item.close)) * pxPerPoint
                                ),
                                end = Offset(
                                    size.center.x,
                                    (maxPrice - minOf(item.open, item.close)) * pxPerPoint
                                ),
                                strokeWidth = 16f * scale
                            )

                            drawTimeDelimiter(
                                candle = item,
                                nextCandle = nextCandle,
                                timeframeState = timeframeState,
                                textMeasurer = textMeasurer
                            )
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
            val lastPrice = if (candleList.isNotEmpty()) {
                if (candleList.first().open > candleList.first().close) candleList.first().open
                else candleList.first().close
            } else 0f
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

@SuppressLint("DefaultLocale")
private fun DrawScope.drawTimeDelimiter(
    candle: Candle,
    nextCandle: Candle?,
    timeframeState: DetailsStore.State.TimeframeState,
    textMeasurer: TextMeasurer
) {
    val candleTime = candle.time
    val minutes = candleTime.get(Calendar.MINUTE)
    val hours = candleTime.get(Calendar.HOUR_OF_DAY)
    val dayOfMonth = candleTime.get(Calendar.DAY_OF_MONTH)
    val month = candleTime.get(Calendar.MONTH)
    val year = candleTime.get(Calendar.YEAR)
    val nameOfMonth =
        DateFormatSymbols(Locale.getDefault()).shortMonths[month]
    val nextDayOfMonth = nextCandle?.time?.get(Calendar.DAY_OF_MONTH)
    val nextMonth = nextCandle?.time?.get(Calendar.MONTH)
    val nextYear = nextCandle?.time?.get(Calendar.YEAR)
    var text = ""
    var shouldDrawDelimiter = false
    when (timeframeState) {
        DetailsStore.State.TimeframeState.OnChange -> {

        }

        is DetailsStore.State.TimeframeState.SelectedTimeframe -> {
            shouldDrawDelimiter = when (timeframeState.timeframe) {
                Timeframe.CANDLE_INTERVAL_1_MIN,
                Timeframe.CANDLE_INTERVAL_2_MIN,
                Timeframe.CANDLE_INTERVAL_3_MIN,
                Timeframe.CANDLE_INTERVAL_5_MIN -> {
                    text = if (dayOfMonth != nextDayOfMonth) {
                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                    } else {
                        String.format("%02d:00", hours)
                    }
                    minutes == 0 && hours.rem(2) == 0
                }

                Timeframe.CANDLE_INTERVAL_15_MIN,
                Timeframe.CANDLE_INTERVAL_10_MIN,
                Timeframe.CANDLE_INTERVAL_30_MIN -> {
                    text = if (dayOfMonth != nextDayOfMonth) {
                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                    } else {
                        String.format("%02d:00", hours)
                    }
                    (minutes == 0 && hours.rem(6) == 0) || dayOfMonth != nextDayOfMonth
                }

                Timeframe.CANDLE_INTERVAL_HOUR,
                Timeframe.CANDLE_INTERVAL_2_HOUR,
                Timeframe.CANDLE_INTERVAL_4_HOUR -> {
                    text = String.format("%02d %s", dayOfMonth, nameOfMonth)
                    dayOfMonth != nextDayOfMonth
                }

                Timeframe.CANDLE_INTERVAL_DAY -> {
                    text = if (year != nextYear && nextYear != null) {
                        String.format("%02d %s %d", dayOfMonth, nameOfMonth, year)
                    } else {
                        String.format("%02d %s", dayOfMonth, nameOfMonth)
                    }
                    dayOfMonth == 1 || (nextMonth != null && month != nextMonth)
                }

                Timeframe.CANDLE_INTERVAL_WEEK -> {
                    text = if (month == 0) {
                        String.format("%d %s", year, nameOfMonth)
                    } else {
                        String.format("%s", nameOfMonth)
                    }
                    (month.rem(2) == 0 || month == 0) && month != nextMonth
                }

                Timeframe.CANDLE_INTERVAL_MONTH -> {
                    text = String.format("%d", year)
                    year != nextYear
                }
            }
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

















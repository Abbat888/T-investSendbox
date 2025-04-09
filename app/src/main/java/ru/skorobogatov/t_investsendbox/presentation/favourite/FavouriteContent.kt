package ru.skorobogatov.t_investsendbox.presentation.favourite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.skorobogatov.t_investsendbox.domain.entity.InstrumentType

@Composable
fun FavouriteContent(
    component: FavouriteComponent
) {

    val state by component.model.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = state.instrumentItems,
            key = { it.instrument.figi }
        ) {
            InstrumentCard(it)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstrumentCard(
    instrumentItem: FavouriteStore.State.InstrumentItem
){
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ){
            GlideImage(
                model = instrumentItem.instrument.brandUrl,
                contentDescription = null,
                modifier = Modifier.size(64.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier.fillMaxHeight()
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ){
                Text(text = instrumentItem.instrument.name)
                InstrumentKind(instrumentItem.instrument.instrumentKind)
            }
            when(val lastPrice = instrumentItem.lastPriceState){
                FavouriteStore.State.LastPriceState.Error -> {
                    Text(text = "error")
                }
                FavouriteStore.State.LastPriceState.Initial -> {

                }
                is FavouriteStore.State.LastPriceState.Loaded -> {
                    Text(text = lastPrice.lastPrice.price.toString())
                }
                FavouriteStore.State.LastPriceState.Loading -> {
                    CircularProgressIndicator()
                }
            }
            Text(text = instrumentItem.instrument.currency)
        }
    }
}

@Composable
fun InstrumentKind(
    instrumentType: String
){
    val text = if (instrumentType == InstrumentType.INSTRUMENT_TYPE_BOND.name){
        "облигация"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_SHARE.name){
        "акция"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_CURRENCY.name){
        "валюта"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_ETF.name){
        "фонд"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_SP.name){
        "структурная нота"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_OPTION.name){
        "опцион"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_CLEARING_CERTIFICATE.name){
        "клиринговый сертификат"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_FUTURES.name){
        "фьючерс"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_INDEX.name){
        "индекс"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_COMMODITY.name){
        "товар"
    } else instrumentType

    Text(
        fontSize = 12.sp,
        text = text
    )
}
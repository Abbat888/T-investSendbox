package ru.skorobogatov.t_investsendbox.presentation.favourite

import android.icu.util.Currency
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.skorobogatov.t_investsendbox.domain.entity.Instrument
import ru.skorobogatov.t_investsendbox.domain.entity.InstrumentType

@Composable
fun FavouriteContent(
    component: FavouriteComponent
) {

    val state by component.model.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchCard {
                component.onClickSearch()
            }
        }
        items(
            items = state.instrumentItems,
            key = { it.instrument.figi }
        ) {
            InstrumentCard(
                instrument = it.instrument,
                lastPriceState = it.lastPriceState
            ) {
                component.onClickInstrumentItem(it.instrument)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstrumentCard(
    instrument: Instrument,
    lastPriceState: FavouriteStore.State.LastPriceState,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.medium
            ),
        colors = CardDefaults.cardColors(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
                .clickable{
                    onItemClick()
                }
        ) {
            GlideImage(
                model = instrument.brandUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = instrument.name
                )
                InstrumentKind(instrument.instrumentKind)
            }
            when (val lastPrice = lastPriceState) {
                FavouriteStore.State.LastPriceState.Error -> {
                    Text(text = "error")
                }

                FavouriteStore.State.LastPriceState.Initial -> {

                }

                is FavouriteStore.State.LastPriceState.Loaded -> {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = lastPrice.lastPrice.price.toString()
                    )
                }

                FavouriteStore.State.LastPriceState.Loading -> {
                    CircularProgressIndicator()
                }
            }
            val currency = Currency.getInstance(instrument.currency).symbol
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically),
                text = currency
            )
        }
    }
}

@Composable
private fun SearchCard(
    onClick: () -> Unit
) {
    Card(
        shape = CircleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onClick() }
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = "Поиск",
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
fun InstrumentKind(
    instrumentType: String
) {
    val text = if (instrumentType == InstrumentType.INSTRUMENT_TYPE_BOND.name) {
        "облигация"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_SHARE.name) {
        "акция"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_CURRENCY.name) {
        "валюта"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_ETF.name) {
        "фонд"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_SP.name) {
        "структурная нота"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_OPTION.name) {
        "опцион"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_CLEARING_CERTIFICATE.name) {
        "клиринговый сертификат"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_FUTURES.name) {
        "фьючерс"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_INDEX.name) {
        "индекс"
    } else if (instrumentType == InstrumentType.INSTRUMENT_TYPE_COMMODITY.name) {
        "товар"
    } else instrumentType

    Text(
        fontSize = 12.sp,
        fontStyle = FontStyle.Italic,
        text = text
    )
}
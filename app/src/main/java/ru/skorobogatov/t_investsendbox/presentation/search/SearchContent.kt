package ru.skorobogatov.t_investsendbox.presentation.search

import android.icu.util.Currency
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import ru.skorobogatov.t_investsendbox.presentation.favourite.InstrumentKind
import ru.skorobogatov.t_investsendbox.presentation.search.SearchStore.State.LastPriceState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    component: SearchComponent
){

    val state by component.model.collectAsState()

    val inputField =
        @Composable {
            InputField(
                query = state.searchQuery,
                onQueryChange = { component.changeSearchQuery(it) },
                onSearch = { component.onClickSearch() },
                expanded = true,
                onExpandedChange = {  },
                modifier = Modifier,
                enabled = true,
                leadingIcon = {
                    IconButton(onClick = { component.onClickBack() }) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
                    }
                },
                trailingIcon = {
                    IconButton(onClick = { component.onClickSearch() }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        }


    SearchBar(
        inputField = inputField,
        expanded = true,
        onExpandedChange = {  }
    ){
        LazyColumn(

        ) {
            items(
                items = state.instrumentItems
            ) {
                InstrumentCard(
                    instrumentState = it.instrumentState,
                    lastPriceState = it.lastPriceState
                ) {
                    when(it.instrumentState){
                        SearchStore.State.InstrumentState.Error -> {

                        }
                        SearchStore.State.InstrumentState.Initial -> {

                        }
                        is SearchStore.State.InstrumentState.Loaded -> {
                            component.onClickInstrument(it.instrumentState.instrument)
                        }
                        SearchStore.State.InstrumentState.Loading -> {

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun InstrumentCard(
    instrumentState: SearchStore.State.InstrumentState,
    lastPriceState: LastPriceState,
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
            when(instrumentState){
                SearchStore.State.InstrumentState.Error -> {

                }
                SearchStore.State.InstrumentState.Initial -> {

                }
                is SearchStore.State.InstrumentState.Loaded -> {
                    GlideImage(
                        model = instrumentState.instrument.brandUrl,
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
                            text = instrumentState.instrument.name
                        )
                        InstrumentKind(instrumentState.instrument.instrumentKind)
                    }
                    when (val lastPrice = lastPriceState) {
                        LastPriceState.Error -> {
                            Text(text = "error")
                        }

                        LastPriceState.Initial -> {

                        }

                        is LastPriceState.Loaded -> {
                            Text(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                text = lastPrice.lastPrice.price.toString()
                            )
                        }

                        LastPriceState.Loading -> {
                            CircularProgressIndicator()
                        }
                    }
                    val currency = Currency.getInstance(instrumentState.instrument.currency).symbol
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
                        text = currency
                    )
                }
                SearchStore.State.InstrumentState.Loading -> {

                }
            }
        }
    }
}
package ru.skorobogatov.t_investsendbox.presentation.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun StartContent(
    component: StartComponent
) {
    val state by component.model.collectAsState()

    var textState by remember { mutableStateOf("") }

    var isValid by remember { mutableStateOf(false) }
    var isNotEmpty by remember { mutableStateOf(false) }

    when(val tokenState = state.tokenState){
        is StartStore.State.TokenState.TokenIsNotEmpty -> {
            isNotEmpty = tokenState.isNotEmpty
        }
        is StartStore.State.TokenState.TokenIsValid -> {
            isValid = tokenState.isValid
        }
        StartStore.State.TokenState.TokenOnChecking -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if (isValid && isNotEmpty) component.onClickGoToFavourite() // Автоматизирует переход на следующий экран при наличии валидного токена

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
    ) {
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("Текст") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = if (isNotEmpty) Color.Green else Color.Red)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ){
                val text = if (isNotEmpty){
                    "Token is not empty"
                } else {
                    "Token is empty"
                }
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(color = if (isValid) Color.Green else Color.Red)
                    .weight(1f),
                contentAlignment = Alignment.Center
            ){
                val text = if (isValid){
                    "Token is valid"
                } else {
                    "Token is invalid"
                }
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = text
                )
            }
        }

        Button(
            onClick = {component.onClickSave(textState)},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Button(
            onClick = {component.onClickCheck()},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check")
        }

        Button(
            onClick = {component.onClickGoToFavourite()},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to favourite")
        }
    }
}
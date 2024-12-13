package com.valoy.network.connectivity.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.valoy.network.connectivity.R
import com.valoy.network.connectivity.ui.theme.dp_180
import com.valoy.network.connectivity.ui.theme.dp_16
import com.valoy.network.connectivity.ui.theme.dp_2
import com.valoy.network.connectivity.ui.theme.dp_32
import com.valoy.network.connectivity.ui.theme.dp_8

@Composable
fun NetworkScreen(modifier: Modifier = Modifier, viewModel: NetworkViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> {
            Loading(modifier = modifier)
        }

        is UiState.Success -> {
            NetworkStatusSpeed(status = state.status, speed = state.speed, modifier = modifier)
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(dp_180)
        )
    }
}

@Composable
private fun NetworkStatusSpeed(
    status: ConnectionStatus,
    speed: ConnectionSpeed?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dp_32),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Title()
        NetworkDivider()
        NetworkStatus(text = status.text, color = status.color)
        NetworkDivider()
        speed?.let { speed ->
            ConnectionSpeed(speed)
            NetworkDivider()
        }
    }
}

@Composable
private fun NetworkStatus(@StringRes text: Int, color: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dp_16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Subtitle(text = R.string.current_network_status)
        ColoredCircleWithText(
            color = Color(color),
            text = stringResource(text)
        )
    }
}

@Composable
private fun Subtitle(@StringRes text: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ColoredCircleWithText(color: Color, text: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(dp_180)
            .border(dp_2, color, shape = CircleShape)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NetworkDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dp_16)
    )
}

@Composable
private fun Title(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dp_8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_network_check),
            contentDescription = stringResource(id = R.string.app_name),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun Speed(@DrawableRes icon: Int, @StringRes text: Int, speed: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(dp_8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = stringResource(id = text),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = speed,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ConnectionSpeed(speed: ConnectionSpeed, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dp_16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Subtitle(text = R.string.current_connection_speed)
        Column {
            Speed(
                icon = R.drawable.ic_upload,
                text = R.string.upload_speed,
                speed = speed.upload
            )
            Speed(
                icon = R.drawable.ic_download,
                text = R.string.download_speed,
                speed = speed.download
            )
        }
    }
}
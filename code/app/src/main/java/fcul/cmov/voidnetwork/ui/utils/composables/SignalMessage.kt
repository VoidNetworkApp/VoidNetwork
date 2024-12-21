package fcul.cmov.voidnetwork.ui.utils.composables

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.ui.utils.MAX_MESSAGE_LENGTH


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalMessage(
    sequence: String,
    message: String,
    onMessageChange: (String) -> Unit,
    onSubmit: (String, String) -> Unit,
    submitText: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    messageRequired: Boolean = false,
    onTranslate: (String) -> String? = { null },
    color: Color = MaterialTheme.colorScheme.primary,
) {
    LaunchedEffect(sequence) {
        onMessageChange(
            if (sequence.isNotBlank()) onTranslate(sequence) ?: "" else ""
        )
    }

    Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            value = message,
            onValueChange = { onMessageChange(it.take(MAX_MESSAGE_LENGTH)) },
            textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
            label = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stringResource(R.string.message))
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colorScheme.onSurface,
                containerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(150.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color)
        ) {
            icon()
        }
        Text(
            text = sequence,
            fontSize = 40.sp,
            modifier = Modifier.padding(0.dp)
        )
        Button(
            onClick = { onSubmit(sequence, message) },
            enabled = sequence.isNotBlank() && (!messageRequired || message.isNotBlank())
        ) {
            Text(text = submitText)
        }
    }
}

@Composable
fun TouchSignalMessage(
    submitText: String,
    onSubmit: (String, String) -> Unit,
    onTranslate: (String) -> String? = { null },
    messageRequired: Boolean = false,
) {
    var message by rememberSaveable { mutableStateOf("") }
    val (sequence, pressModifier, resetSequence) = rememberTouchSequence()
    SignalMessage(
        sequence = sequence,
        message = message,
        onMessageChange = { message = it },
        onSubmit = { seq, msg ->
            onSubmit(seq, msg)
            resetSequence()
            message = ""
        },
        onTranslate = onTranslate,
        submitText = submitText,
        icon = {
            Icon(
                imageVector = Icons.Filled.TouchApp,
                contentDescription = stringResource(R.string.touch),
            )
        },
        modifier = pressModifier,
        messageRequired = messageRequired
    )
}

@Composable
fun LightSignalMessage(
    submitText: String,
    onSubmit: (String, String) -> Unit,
    onTranslate: (String) -> String? = { null },
) {
    var message by rememberSaveable { mutableStateOf("") }
    val luminosityProvider = rememberLuminosityProvider()
    val (sequence, _, resetSequence) = rememberLightSequence(luminosityProvider)
    val luminosity = luminosityProvider()
    val animatedAlpha by animateFloatAsState(
        targetValue = (luminosity / 1000f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = stringResource(R.string.luminosity)
    )
    SignalMessage(
        sequence = sequence,
        message = message,
        onMessageChange = { message = it },
        onSubmit = { seq, msg ->
            onSubmit(seq, msg)
            resetSequence()
            message = ""
        },
        onTranslate = onTranslate,
        submitText = submitText,
        icon = {
            Icon(
                imageVector = Icons.Filled.Lightbulb,
                contentDescription = stringResource(R.string.light)
            )
        },
        color = Color.White.copy(alpha = animatedAlpha)
    )
}



package fcul.cmov.voidnetwork.ui.screens.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fcul.cmov.voidnetwork.R
import fcul.cmov.voidnetwork.domain.Message
import fcul.cmov.voidnetwork.ui.utils.getCurrentUser
import fcul.cmov.voidnetwork.ui.utils.timeAgo
import fcul.cmov.voidnetwork.ui.viewmodels.MessageReceiverViewModel

@Composable
fun MessagesScreen(
    nav: NavController,
    viewModel: MessageReceiverViewModel
) {
    val currentUserId = getCurrentUser()?.uid
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.recent_messages),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(50.dp))
        }
        if (viewModel.messages.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_messages),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            items(viewModel.messages.reversed()) { message ->
                MessageItem(
                    message = message,
                    sent = message.sender == currentUserId,
                    onReplayMessage = { viewModel.replayMessage(message) }
                )
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    sent: Boolean,
    onReplayMessage: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (sent) Icons.AutoMirrored.Filled.CallMade else Icons.AutoMirrored.Filled.CallReceived,
            contentDescription = stringResource(R.string.message_sender_icon),
            modifier = Modifier.size(15.dp)
        )
        Button(
            onClick = onReplayMessage,
            modifier = Modifier.padding(5.dp)
        ) {
            Text(message.toString())
        }
        Text(
            text = timeAgo(message.timestamp!!),
            style = MaterialTheme.typography.titleSmall
        )
    }
}


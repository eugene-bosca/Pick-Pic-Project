package com.bmexcs.pickpic.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.utils.Utils // Your QRCodeUtils
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView

@Composable
fun EventCreateInviteView(
    navController: NavHostController,
    eventId: String
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current

    val obfuscatedEventId = "***" // TODO: make call to backend here to get obfuscated event id
    val event_name = "Event Name" // TODO: make call to backend (or fetch from frontend?) here to get event name


    val inviteLink = "myapp://invite?eventId=$obfuscatedEventId"
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(obfuscatedEventId) {
        qrCodeBitmap = Utils.generateQRCode(inviteLink, 512, 512)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Invite your friends to join ${event_name}!",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        qrCodeBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(256.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = inviteLink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        Button(onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Invite Link", inviteLink)
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(context, "Link copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
        }) {
            Text("Copy Link")
        }
    }
}
package com.bmexcs.pickpic.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.presentation.viewmodels.QrInviteViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Hashtable

@Composable
fun EventCreateInviteView(
    navController: NavHostController,
    viewModel: QrInviteViewModel = hiltViewModel(),
    eventId: String,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var obfuscatedEventId by remember { mutableStateOf<String?>(null) }
    var eventName by remember { mutableStateOf<String?>(null) }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var inviteLink by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(eventId) {
        scope.launch(Dispatchers.IO) {
            try {
                // Fetch obfuscated event ID
                val obfuscatedIdResult = fetchObfuscatedEventId(eventId)
                obfuscatedEventId = obfuscatedIdResult.first
                eventName = obfuscatedIdResult.second

                // Generate invite link
                obfuscatedEventId?.let {
                    inviteLink = "myapp://invite?eventId=$it"
                    qrCodeBitmap = generateQRCode(inviteLink!!, 512, 512)
                }
            } catch (e: Exception) {
                Log.e("EventCreateInviteView", "Error fetching data: ${e.message}")
                // Handle error (e.g., show a toast or error message)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        eventName?.let { name ->
            Text(
                text = "Invite your friends to join $name!",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        qrCodeBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(256.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        inviteLink?.let { link ->
            Text(
                text = link,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }

        Button(onClick = {
            inviteLink?.let { link ->
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Invite Link", link)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(context, "Link copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Copy Link")
        }
    }
}

private suspend fun fetchObfuscatedEventId(eventId: String): Pair<String?, String?> = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("YOUR_BACKEND_URL/generate_invite_link/$eventId/") // Replace with your backend URL
        .build()

    try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val json = JSONObject(responseBody)
            val inviteLink = json.getString("invite_link")
            val obfuscatedId = inviteLink.substringAfterLast("/") // Extract the obfuscated ID
            val eventName = fetchEventName(eventId)
            return@withContext Pair(obfuscatedId, eventName)
        } else {
            Log.e("EventCreateInviteView", "Failed to fetch obfuscated ID: ${response.code}")
            return@withContext Pair(null, null)
        }
    } catch (e: Exception) {
        Log.e("EventCreateInviteView", "Error fetching obfuscated ID: ${e.message}")
        return@withContext Pair(null, null)
    }
}

private suspend fun fetchEventName(eventId: String): String? = withContext(Dispatchers.IO){
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("YOUR_BACKEND_URL/events/$eventId/") //Replace with your backend url
        .build()

    try{
        val response = client.newCall(request).execute()
        if(response.isSuccessful){
            val responseBody = response.body?.string()
            val json = JSONObject(responseBody)
            return@withContext json.getString("event_name")
        } else{
            Log.e("EventCreateInviteView", "Failed to fetch event name: ${response.code}")
            return@withContext null
        }
    }catch(e: Exception){
        Log.e("EventCreateInviteView", "Error fetching event name: ${e.message}")
        return@withContext null
    }
}

fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
    return try {
        val hints = Hashtable<EncodeHintType, Any>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val bitMatrix = QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, width, height, hints)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
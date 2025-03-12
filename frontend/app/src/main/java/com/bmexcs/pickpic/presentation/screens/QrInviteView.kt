package com.bmexcs.pickpic.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
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
import java.util.Hashtable
import android.util.Log


@Composable
fun QrInviteView(
    navController: NavHostController,
    viewModel: QrInviteViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val inviteState by viewModel.inviteState.collectAsState()

    val eventId = navController.currentBackStackEntry?.arguments?.getString("eventId") ?: ""
    Log.d("QrInviteView", "eventId: $eventId")
    LaunchedEffect(eventId) {
        viewModel.fetchInviteDetails(eventId)
    }

    when (inviteState) {
        is QrInviteViewModel.InviteState.Loading -> {
            Text("Loading...")
        }
        is QrInviteViewModel.InviteState.Success -> {
            val successState = inviteState as QrInviteViewModel.InviteState.Success
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                successState.eventName?.let { name ->
                    Text(
                        text = "Invite your friends to join $name!",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                successState.qrCodeBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(256.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                successState.inviteLink?.let { link ->
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
                    successState.inviteLink?.let { link ->
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
        is QrInviteViewModel.InviteState.Error -> {
            val errorState = inviteState as QrInviteViewModel.InviteState.Error
            Text("Error: ${errorState.errorMessage}")
        }
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
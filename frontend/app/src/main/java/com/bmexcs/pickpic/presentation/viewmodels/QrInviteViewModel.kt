package com.bmexcs.pickpic.presentation.viewmodels;

import android.graphics.Bitmap
import android.graphics.Color;
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bmexcs.pickpic.data.sources.EventDataSource
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Hashtable
import javax.inject.Inject

@HiltViewModel
class QrInviteViewModel @Inject constructor(private val eventDataSource: EventDataSource) : ViewModel() {

    private val _inviteState = MutableStateFlow<InviteState>(InviteState.Loading)
            val inviteState: StateFlow<InviteState> = _inviteState

    fun fetchInviteDetails(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (obfuscatedEventId, eventName) = eventDataSource.fetchObfuscatedEventId(eventId)

                if (obfuscatedEventId != null && eventName != null) {
                    val inviteLink = "myapp://invite?eventId=$obfuscatedEventId"
                    val qrCodeBitmap = generateQRCode(inviteLink, 512, 512)

                    _inviteState.value = InviteState.Success(
                        eventName = eventName,
                        qrCodeBitmap = qrCodeBitmap,
                        inviteLink = inviteLink
                    )
                } else {
                    // Handle the case where either obfuscatedEventId or eventName is null
                    _inviteState.value = InviteState.Error("Failed to fetch event details.")
                }
            } catch (e: Exception) {
                _inviteState.value = InviteState.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class InviteState {
        object Loading : InviteState()
        data class Success(val eventName: String?, val qrCodeBitmap: Bitmap?, val inviteLink: String?) : InviteState()
        data class Error(val errorMessage: String) : InviteState()
    }

    private fun generateQRCode(data: String, width: Int, height: Int): Bitmap? {
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
}
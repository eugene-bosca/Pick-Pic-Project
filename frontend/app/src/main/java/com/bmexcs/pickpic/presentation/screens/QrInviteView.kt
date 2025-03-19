package com.bmexcs.pickpic.presentation.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import android.util.Log
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (inviteState) {

                is QrInviteViewModel.InviteState.Loading -> {
                    Text("Loading...")
                }

                is QrInviteViewModel.InviteState.Success -> {
                    val successState = inviteState as QrInviteViewModel.InviteState.Success
                    Column(
                        modifier = Modifier.fillMaxSize(),
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

                        Button(
                            onClick = {
                                successState.inviteLink?.let { link ->
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Invite Link", link)
                                    clipboard.setPrimaryClip(clip)
                                    android.widget.Toast.makeText(context, "Link copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
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
    }
}

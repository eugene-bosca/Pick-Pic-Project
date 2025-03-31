package com.bmexcs.pickpic.presentation.shared

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.navigation.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickPicScaffold(
    title: String, // Title for the TopAppBar
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    // scaffold is still here for layout purposes
    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text(title) },
//                // Remove the menu icon if you don't need it anymore
//                // Or keep it for potential future use
//                navigationIcon = {
//                    // Optional: You can keep this empty or remove it completely
//                    // If you might add navigation later, you could leave it as:
//                    // IconButton(onClick = { /* TODO */ }) {
//                    //     Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
//                    // }
//                },
//            )
//        },
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            content()
        }
    }
}
@Composable
fun NavDrawerEntry(
    text: String,
    navController: NavHostController,
    route: String // Route to navigate to. Defined as distinct objects in Route.kt.
) {
    val padding = 16.dp
    Box(
        modifier = Modifier.clickable {
            navController.navigate(route)
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(padding)
        )
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = padding),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

package com.bmexcs.pickpic.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bmexcs.pickpic.R
import com.bmexcs.pickpic.navigation.Route
import com.bmexcs.pickpic.presentation.viewmodels.CreateEventViewModel

@Composable
fun CreateEventScreenView(
    navController: NavHostController,
    viewModel: CreateEventViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.25f))
            EditableEventNameField()
            Spacer(modifier = Modifier.height(50.dp))
        }

        ElevatedButton(
            onClick = {
                viewModel.onCreate(
                    onSuccess = {
                        Toast.makeText(context, "Event created successfully!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Route.CreateEvent.route)
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.add_circle_24px),
                contentDescription = "Create Event Icon",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Create")
        }
    }
}

@Composable
fun EditableEventNameField(viewModel: CreateEventViewModel = hiltViewModel()) {
    var eventName by remember { mutableStateOf("") }

    Text(
        text = "New Event Name",
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = eventName,
        onValueChange = { newValue ->
            eventName = newValue
            viewModel.eventName.value = newValue},
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

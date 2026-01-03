package com.example.sakshi.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    uid: String = "demo_uid",
    viewModel: ProfileViewModel = viewModel()
) {
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load(uid)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Emergency Profile", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = profile.name,
            onValueChange = { viewModel.update(profile.copy(name = it)) },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = profile.phone,
            onValueChange = { viewModel.update(profile.copy(phone = it)) },
            label = { Text("Phone") }
        )

        OutlinedTextField(
            value = profile.bloodGroup,
            onValueChange = { viewModel.update(profile.copy(bloodGroup = it)) },
            label = { Text("Blood Group") }
        )

        OutlinedTextField(
            value = profile.age,
            onValueChange = { viewModel.update(profile.copy(age = it)) },
            label = { Text("Age") }
        )

        OutlinedTextField(
            value = profile.medicalInfo,
            onValueChange = { viewModel.update(profile.copy(medicalInfo = it)) },
            label = { Text("Medical Conditions") }
        )

        OutlinedTextField(
            value = profile.address,
            onValueChange = { viewModel.update(profile.copy(address = it)) },
            label = { Text("Address") }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = { viewModel.save(uid) }) {
            Text("Save Profile")
        }

    }
}

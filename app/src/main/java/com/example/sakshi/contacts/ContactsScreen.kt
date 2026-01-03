package com.example.sakshi.contacts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sakshi.model.EmergencyContact

@Composable
fun ContactsScreen(
    sharedVM: SharedContactsViewModel,
    navController: NavController
) {
    val contacts by sharedVM.contacts.collectAsState()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        sharedVM.loadContacts()
    }

    Column(Modifier.padding(16.dp)) {

        Text(
            text = "Emergency Contacts",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") }
        )

        Button(
            onClick = {
                if (phone.length >= 10) {
                    sharedVM.addContact(
                        EmergencyContact(name.trim(), phone.trim())
                    )
                    name = ""
                    phone = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Contact")
        }

        Spacer(Modifier.height(16.dp))

        if (contacts.isEmpty()) {
            Text(
                text = "At least one emergency contact is required",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            Button(onClick = { navController.navigate("home") }) {
                Text("Next")
            }
        }

        contacts.forEach { contact ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${contact.name} - ${contact.phone}")
                TextButton(
                    onClick = { sharedVM.removeContact(contact) }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

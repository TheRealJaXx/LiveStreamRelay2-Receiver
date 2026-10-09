package com.example.livestreamrelayreceiver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.livestreamrelayreceiver.ui.ReceiverScreen
import com.example.livestreamrelayreceiver.ui.ReceiverViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ReceiverViewModel(application)

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                ReceiverScreen(viewModel = viewModel)
            }
        }
    }
}

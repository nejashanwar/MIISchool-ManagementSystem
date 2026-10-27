package com.example.marasaaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.marasaaapp.repository.SchoolRepository
import com.example.marasaaapp.ui.navigation.MainAppScaffold
import com.example.marasaaapp.ui.theme.MarasaaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SchoolRepository.initialize(applicationContext)
        enableEdgeToEdge()
        setContent {
            MarasaaAppTheme {
                MainAppScaffold()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainAppScaffoldPreview() {
    MarasaaAppTheme {
        MainAppScaffold()
    }
}

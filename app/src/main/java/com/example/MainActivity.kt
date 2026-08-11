package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.JarvisViewModel
import com.example.ui.navigation.JarvisAppScaffold
import com.example.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val jarvisViewModel: JarvisViewModel = viewModel()
            val themeStyle by jarvisViewModel.themeStyle.collectAsState()

            JarvisTheme(themeStyle = themeStyle) {
                JarvisAppScaffold(viewModel = jarvisViewModel)
            }
        }
    }
}

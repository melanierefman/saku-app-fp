package com.example.saku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.saku.app.navigation.AppNavHost
import com.example.saku.app.ui.theme.SAKUAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SAKUAppTheme {
                AppNavHost()
            }
        }
    }
}
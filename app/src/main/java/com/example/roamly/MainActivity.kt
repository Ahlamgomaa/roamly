package com.example.roamly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.roamly.ui.theme.RoamlyTheme
import com.example.roamly.nav.rootnavigation.RootNavDisplay


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RoamlyTheme {
                RootNavDisplay()
            }
        }
    }
}
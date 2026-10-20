package com.fabiancg.persistenciaroomsqlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fabiancg.persistenciaroomsqlite.ui.MainScreen
import com.fabiancg.persistenciaroomsqlite.ui.theme.PersistenciaRoomSQLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PersistenciaRoomSQLiteTheme {
                MainScreen()
            }
        }
    }
}
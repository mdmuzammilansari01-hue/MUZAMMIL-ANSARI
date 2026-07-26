package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.AboutSettingsScreen
import com.example.ui.screens.AdminLoginScreen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ClassesScreen
import com.example.ui.screens.DetailedSolutionScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.OnlineTestScreen
import com.example.ui.screens.PdfViewerScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SubjectsScreen
import com.example.ui.screens.TestResultScreen
import com.example.ui.screens.TestsListScreen
import com.example.ui.screens.VideosScreen
import com.example.ui.theme.JACAppTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels {
    ViewModelProvider.AndroidViewModelFactory.getInstance(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val isDarkMode by viewModel.isDarkMode.collectAsState()

      JACAppTheme(darkTheme = isDarkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
          MainAppContent(viewModel = viewModel)
        }
      }
    }
  }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  when (currentScreen) {
    Screen.Splash -> SplashScreen()
    Screen.Auth -> AuthScreen(viewModel = viewModel)
    Screen.Home -> HomeScreen(viewModel = viewModel)
    Screen.Classes -> ClassesScreen(viewModel = viewModel)
    Screen.Subjects -> SubjectsScreen(viewModel = viewModel)
    Screen.TestsList -> TestsListScreen(viewModel = viewModel)
    Screen.OnlineTest -> OnlineTestScreen(viewModel = viewModel)
    Screen.TestResultScreen -> TestResultScreen(viewModel = viewModel)
    Screen.DetailedSolution -> DetailedSolutionScreen(viewModel = viewModel)
    Screen.Notes -> NotesScreen(viewModel = viewModel)
    Screen.PdfViewerScreen -> PdfViewerScreen(viewModel = viewModel)
    Screen.Videos -> VideosScreen(viewModel = viewModel)
    Screen.Performance -> PerformanceScreen(viewModel = viewModel)
    Screen.LeaderboardScreen -> LeaderboardScreen(viewModel = viewModel)
    Screen.AdminLogin -> AdminLoginScreen(viewModel = viewModel)
    Screen.AdminPanel -> AdminPanelScreen(viewModel = viewModel)
    Screen.AboutSettings -> AboutSettingsScreen(viewModel = viewModel)
  }
}

package cc.seaotter.tomatoes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import cc.seaotter.tomatoes.ui.TomatoesApp
import cc.seaotter.tomatoes.ui.navigation.TomatoesRoute
import cc.seaotter.tomatoes.ui.splash.SplashViewModel
import cc.seaotter.tomatoes.ui.theme.TomatoesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        val destination = splashViewModel.onAppStart()

        splashScreen.setKeepOnScreenCondition {
            splashViewModel.loading.value
        }

        setContent {
            // TODO: 在設定增加主題選擇的選項
            TomatoesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TomatoesApp(
                        startingDestination = destination
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TomatoesTheme {
        TomatoesApp(
            startingDestination = TomatoesRoute.TODO
        )
    }
}
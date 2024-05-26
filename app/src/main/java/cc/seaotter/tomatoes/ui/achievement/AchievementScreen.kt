package cc.seaotter.tomatoes.ui.achievement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun AchievementScreen(
    clearAndNavigate: (String) -> Unit,
    viewModel: AchievementViewModel = hiltViewModel()
) {
    AchievementScreenContent(
        signOut = { viewModel.signOut(clearAndNavigate) }
    )
}

@Composable
fun AchievementScreenContent(
    signOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("這個頁面還在建置中...")
        Button(onClick = {
            signOut()

        }) {
            Text(text = "Sign out")
        }
    }
}
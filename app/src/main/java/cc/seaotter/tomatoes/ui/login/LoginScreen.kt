package cc.seaotter.tomatoes.ui.login


import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import cc.seaotter.tomatoes.R
import cc.seaotter.tomatoes.ui.common.PrimaryButton
import cc.seaotter.tomatoes.ui.theme.TomatoesTheme
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID


private const val TAG = "LoginScreen"

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit, viewModel: LoginViewModel = hiltViewModel()
) {
    LoginScreenContent(
        signInWithGoogle = { credential -> viewModel.signInWithGoogle(credential, openAndPopUp) },
        signInWithAnonymous = { viewModel.createAnonymousAccount(openAndPopUp) },
    )
}

@Composable
fun LoginScreenContent(
    signInWithGoogle: (credential: String) -> Unit,
    signInWithAnonymous: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "開始使用",
            style = TextStyle(
                fontSize = 45.sp,
                lineHeight = 52.sp,
                fontWeight = FontWeight(400),
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
            )
        )
        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = "Welcome",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            val onSignInWithGoogleClick = {
                val credentialManager = CredentialManager.create(context)
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
                val signInWithGoogleOption: GetSignInWithGoogleOption =
                    Builder(context.getString(R.string.auth_web_client_id))
                        .setNonce(hashedNonce)
                        .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()

                val isDebug =
                    (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
                coroutineScope.launch {
                    try {
                        if (isDebug) {
                            Log.d(TAG, "Debug mode, sign in with Google")
                            signInWithGoogle("{\"sub\": \"abc123\", \"email\": \"test@seaotter.cc\", \"email_verified\": true}")
                            return@launch
                        }
                        val result = credentialManager.getCredential(
                            request = request,
                            context = context,
                        )
                        val credential = result.credential
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken
                        signInWithGoogle(googleIdToken)
                        Log.d(TAG, "GoogleIdToken $googleIdToken")
                    } catch (e: GetCredentialException) {
                        Log.e(TAG, "GetCredentialException: ${e.message}")
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "GoogleIdTokenParsingException: ${e.message}")
                    } catch (e: Exception) {
                        Log.e(TAG, "Exception: ${e.message}")
                    }
                }
            }

            PrimaryButton(
                onClick = { onSignInWithGoogleClick() },
                modifier = Modifier
                    .widthIn(min = 200.dp)
            ) {
                Text(text = "Login")
            }
            TextButton(
                onClick = {
                    signInWithAnonymous()
                }
            ) {
                Text(text = "使用訪客身份登入")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    TomatoesTheme {
        LoginScreenContent(
            signInWithGoogle = {},
            signInWithAnonymous = {}
        )
    }
}
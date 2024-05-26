package cc.seaotter.tomatoes.data.service.module

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

//    init {
//        Firebase.auth.useEmulator("192.168.1.185", 9099)
//        Firebase.auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
//        Firebase.firestore.useEmulator("192.168.1.185", 8080)
//    }

    @Provides
    fun auth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun firestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}
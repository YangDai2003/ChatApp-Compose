package com.yangdai.chatapp.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import com.yangdai.chatapp.core.AppKeyboardFocusManager
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.ui.theme.ChatWithMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OSSubscriptionObserver {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // OneSignal Enable Notification
        OneSignal.addSubscriptionObserver(this)
        OneSignal.disablePush(false)

        setContentView(ComposeView(this).apply {
            setContent {
                AppKeyboardFocusManager()
                ChatWithMeTheme {
                    MainScreenView()
                }
            }
        })
    }

    override fun onOSSubscriptionChanged(p0: OSSubscriptionStateChanges?) {
        if (p0!!.from.isSubscribed &&
            !p0.to.isSubscribed
        ) {
            println("Notifications Disabled!")
        }
        if (!p0.from.isSubscribed &&
            p0.to.isSubscribed
        ) {
            println("Notifications Enabled!")
        }
    }

    override fun onResume() {
        mainViewModel.setUserStatusToFirebase(UserStatus.ONLINE)
        super.onResume()
    }

    override fun onPause() {
        mainViewModel.setUserStatusToFirebase(UserStatus.OFFLINE)
        super.onPause()
    }

    override fun onDestroy() {
        mainViewModel.setUserStatusToFirebase(UserStatus.OFFLINE)
        super.onDestroy()
    }
}

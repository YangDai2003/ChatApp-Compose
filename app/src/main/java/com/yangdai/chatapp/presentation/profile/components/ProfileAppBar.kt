package com.yangdai.chatapp.presentation.profile.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.yangdai.chatapp.R
import com.yangdai.chatapp.main.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAppBar(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // 获取Compose的Context
    // 定义启动新Activity的ActivityResultLauncher
    val resultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // 在这里处理从新Activity返回的结果
    }
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            IconButton(onClick = {
                val intent = Intent(context, SettingsActivity::class.java)
                resultLauncher.launch(intent)
            }) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    )
}
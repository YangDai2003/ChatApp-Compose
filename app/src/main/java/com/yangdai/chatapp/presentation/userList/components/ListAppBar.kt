package com.yangdai.chatapp.presentation.userList.components

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.yangdai.chatapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListAppBar(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit // 外部传入的点击事件
) {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        modifier = modifier.statusBarsPadding(),
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.new_chat)
                )
            }
        }
    )
}
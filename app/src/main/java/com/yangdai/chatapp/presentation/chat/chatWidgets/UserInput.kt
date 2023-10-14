package com.yangdai.chatapp.presentation.chat.chatWidgets

import android.Manifest
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Duo
import androidx.compose.material.icons.outlined.InsertPhoto
import androidx.compose.material.icons.outlined.Mood
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.yangdai.chatapp.R
import com.yangdai.chatapp.core.Utils

enum class InputSelector {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE,
    MIC
}

@Preview
@Composable
fun UserInputPreview() {
    UserInput(onMessageChange = {})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    onMessageChange: (String) -> Unit
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }

    // 当有面板展开时，拦截返回手势
    if (currentInputSelector != InputSelector.NONE) {
        BackHandler(onBack = dismissKeyboard)
    }

    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }

    // 记录文本框焦点状态，决定键盘是否显示
    var textFieldFocusState by remember { mutableStateOf(false) }

    Surface(tonalElevation = 2.dp, contentColor = MaterialTheme.colorScheme.secondary) {
        Column(modifier = modifier) {
            UserInputText(
                onRecordTextChanged = { textState = textState.setText(it) },
                onSelectorChange = { currentInputSelector = it },
                textFieldValue = textState,
                onTextChanged = { textState = it },
                // 仅当没有输入选择器并且文本字段具有焦点时才显示键盘
                keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
                // 如果文本框获得焦点，则关闭扩展选择器
                onTextFieldFocused = { focused ->
                    if (focused) {
                        currentInputSelector = InputSelector.NONE
                    }
                    textFieldFocusState = focused
                }
            )
            UserInputSelector(
                onSelectorChange = { currentInputSelector = it },
                sendMessageEnabled = textState.text.isNotBlank(),
                onMessageSent = {
                    onMessageChange(textState.text)
                    // 重置文本字段并关闭键盘
                    textState = TextFieldValue()
                    dismissKeyboard()
                },
                currentInputSelector = currentInputSelector
            )
            SelectorExpanded(
                onTextAdded = { textState = textState.addText(it) },
                currentSelector = currentInputSelector
            )
        }
    }
}

// 在原有文本后添加文本
private fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText = this.text.replaceRange(
        this.selection.start,
        this.selection.end,
        newString
    )
    val newSelection = TextRange(
        start = newText.length,
        end = newText.length
    )

    return this.copy(text = newText, selection = newSelection)
}

// 替换原有文本为新文本
private fun TextFieldValue.setText(newString: String): TextFieldValue {
    val newSelection = TextRange(
        start = newString.length,
        end = newString.length
    )

    return this.copy(text = newString, selection = newSelection)
}

@Composable
private fun SelectorExpanded(
    currentSelector: InputSelector,
    onTextAdded: (String) -> Unit
) {
    if (currentSelector == InputSelector.NONE) return

    // 请求焦点以强制 TextField 失去焦点
    val focusRequester = FocusRequester()
    // 如果显示选择器，则始终请求焦点以触发 TextField.onFocusChange。
    SideEffect {
        if (currentSelector == InputSelector.EMOJI) {
            focusRequester.requestFocus()
        }
    }

    Surface(tonalElevation = 8.dp) {
        when (currentSelector) {
            InputSelector.EMOJI -> EmojiSelector(onTextAdded, focusRequester)
            InputSelector.DM -> FunctionalityNotAvailablePanel()
            InputSelector.PICTURE -> FunctionalityNotAvailablePanel()
            InputSelector.MAP -> FunctionalityNotAvailablePanel()
            InputSelector.PHONE -> FunctionalityNotAvailablePanel()
            InputSelector.MIC -> RecordPanel()
            else -> {
                throw NotImplementedError()
            }
        }
    }
}

@Composable
private fun UserInputSelector(
    onSelectorChange: (InputSelector) -> Unit,
    sendMessageEnabled: Boolean,
    onMessageSent: () -> Unit,
    currentInputSelector: InputSelector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(72.dp)
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.EMOJI) },
            icon = Icons.Outlined.Mood,
            selected = currentInputSelector == InputSelector.EMOJI,
            description = stringResource(id = R.string.emoji_selector_bt_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.DM) },
            icon = Icons.Outlined.AlternateEmail,
            selected = currentInputSelector == InputSelector.DM,
            description = stringResource(id = R.string.dm_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.PICTURE) },
            icon = Icons.Outlined.InsertPhoto,
            selected = currentInputSelector == InputSelector.PICTURE,
            description = stringResource(id = R.string.attach_photo_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.MAP) },
            icon = Icons.Outlined.Place,
            selected = currentInputSelector == InputSelector.MAP,
            description = stringResource(id = R.string.map_selector_desc)
        )
        InputSelectorButton(
            onClick = { onSelectorChange(InputSelector.PHONE) },
            icon = Icons.Outlined.Duo,
            selected = currentInputSelector == InputSelector.PHONE,
            description = stringResource(id = R.string.videochat_desc)
        )

        val border = if (!sendMessageEnabled) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        } else {
            null
        }
        Spacer(modifier = Modifier.weight(1f))

        val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

        val buttonColors = ButtonDefaults.buttonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = disabledContentColor
        )

        // Send button
        Button(
            modifier = Modifier.height(36.dp),
            enabled = sendMessageEnabled,
            onClick = onMessageSent,
            colors = buttonColors,
            border = border,
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = stringResource(id = R.string.send_desc)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(stringResource(id = R.string.send))
        }
    }
}

@Composable
private fun InputSelectorButton(
    onClick: () -> Unit,
    icon: ImageVector,
    description: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = if (selected) {
        Modifier.background(
            color = LocalContentColor.current,
            shape = RoundedCornerShape(14.dp)
        )
    } else {
        Modifier
    }
    IconButton(
        onClick = onClick,
        modifier = modifier.then(backgroundModifier)
    ) {
        val tint = if (selected) {
            contentColorFor(backgroundColor = LocalContentColor.current)
        } else {
            LocalContentColor.current
        }
        Icon(
            icon,
            tint = tint,
            modifier = Modifier
                .padding(8.dp)
                .size(56.dp),
            contentDescription = description
        )
    }
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalFoundationApi
@Composable
private fun UserInputText(
    onSelectorChange: (InputSelector) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    onRecordTextChanged: (String) -> Unit
) {
    var isRecordingMessage by remember { mutableStateOf(false) }
    val label = stringResource(id = R.string.textfield_desc)
    val noInternet = stringResource(id = R.string.noInternet)
    val noPermissions = stringResource(id = R.string.noPermission)
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.RECORD_AUDIO
        )
    )
    val context = LocalContext.current
    val myRecognitionListener = MyRecognitionListener()
    val mySpeechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    mySpeechRecognizer.setRecognitionListener(myRecognitionListener)
    val result = remember {
        mutableStateOf("")
    }
    myRecognitionListener.setResult(result)
    DisposableEffect(result.value) {
        onRecordTextChanged(result.value)

        onDispose {
            // Clean up any resources if needed
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.End
    ) {

        IconButton(
            onClick = {
                if (!isRecordingMessage) {
                    multiplePermissionState.launchMultiplePermissionRequest()
                    when {
                        multiplePermissionState.allPermissionsGranted -> {
                            if (Utils.isNetworkAvailable(context)) {
                                val recognitionIntent =
                                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                                recognitionIntent.putExtra(
                                    RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                                    true
                                )
                                mySpeechRecognizer.startListening(recognitionIntent)
                                onSelectorChange(InputSelector.MIC)
                            } else {
                                Toast.makeText(
                                    context,
                                    noInternet,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        multiplePermissionState.shouldShowRationale -> {
                            Toast.makeText(
                                context,
                                noPermissions,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } else {
                    onSelectorChange(InputSelector.NONE)
                    mySpeechRecognizer.stopListening()
                }
                isRecordingMessage = !isRecordingMessage
            },
            modifier = Modifier
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
                .padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = stringResource(id = R.string.record)
            )
        }


        Box(
            Modifier
                .fillMaxSize()
                .padding(start = 0.dp, top = 8.dp, end = 8.dp, bottom = 0.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                ),
        ) {

            UserInputTextField(
                textFieldValue,
                onTextChanged,
                onTextFieldFocused,
                keyboardType,
                Modifier.semantics {
                    contentDescription = label
                    keyboardShownProperty = keyboardShown
                }
            )

        }
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    var lastFocusState by remember { mutableStateOf(false) }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .align(Alignment.Center)
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            }
            .background(MaterialTheme.colorScheme.surface),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Done
        ),
        textStyle = LocalTextStyle.current.copy(
            color = LocalContentColor.current,
            fontSize = 22.sp
        )
    )
}

package com.yangdai.chatapp.presentation.chat.chatWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.yangdai.chatapp.R
import com.yangdai.chatapp.ui.theme.spacing

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppBar(
    title: String = "Title",
    description: String = "Description",
    pictureUrl: String = "",
    onBackArrowClick: (() -> Unit)? = null,
    onUserProfilePictureClick: (() -> Unit)? = null,
    onMoreDropDownBlockUserClick: (() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = Modifier
            .padding(top = 8.dp),
        title = {
            Row {
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterVertically),
                    shape = CircleShape,
                    color = Color.LightGray
                ) {
                    Image(
                        painter = if (pictureUrl.isNotEmpty()) {
                            rememberAsyncImagePainter(pictureUrl)
                        } else {
                            rememberVectorPainter(Icons.Filled.Person) // 使用默认的 Person 图标
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clickable { onUserProfilePictureClick?.invoke() })

                }
                Column(
                    modifier = Modifier
                        .padding(start = spacing.medium)
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = description,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { onBackArrowClick?.invoke() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    expanded = true
                }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = null)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.block))
                        },
                        onClick = {
                            onMoreDropDownBlockUserClick?.invoke()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Block,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    )
}

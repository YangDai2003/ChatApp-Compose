package com.yangdai.chatapp.presentation.profile.components

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.yangdai.chatapp.R


@Composable
fun PickPicFromGallery(
    profilePictureUrlForCheck: String,
    size: Dp = 200.dp,
    onSelect: (Uri?) -> Unit = {},
) {
    val context = LocalContext.current

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri
        onSelect(uri)
    }

    Box(
        modifier = Modifier
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        LaunchedEffect(key1 = imageUri) {
            if (imageUri != null) {
                val source = ImageDecoder
                    .createSource(context.contentResolver, imageUri!!)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
        }
        if (bitmap != null) {
            Image(
                painter = rememberAsyncImagePainter(bitmap),
                contentDescription = null,
                modifier = Modifier
                    .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    .size(size),
                contentScale = ContentScale.Crop
            )
        } else {
            if (profilePictureUrlForCheck != "") {
                Image(painter = rememberAsyncImagePainter(profilePictureUrlForCheck),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                        .size(size),
                    contentScale = ContentScale.Crop)
            } else {
                Image(painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context).data(R.mipmap.ic_launcher_round)
                        .build()
                ),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                        .size(size),
                    contentScale = ContentScale.Crop)
            }
        }
    }
}
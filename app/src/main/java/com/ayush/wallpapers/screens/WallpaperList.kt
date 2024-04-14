package com.ayush.wallpapers.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ayush.wallpapers.R
import com.ayush.wallpapers.data.WallpaperCategoricalViewModel
import com.ayush.wallpapers.data.WallpaperDownloader

@Composable
fun WallpaperListScreen(
    paddingValues: PaddingValues,
    wallpaperCategoricalViewModel: WallpaperCategoricalViewModel = viewModel(),
    context: Context = LocalContext.current
) {
    val downloader = WallpaperDownloader(context)
    val result = wallpaperCategoricalViewModel.categoryState
    var pgNo by rememberSaveable {
        mutableIntStateOf(1)
    }
    var download by rememberSaveable {
        mutableStateOf(false)
    }
    var downloadUrl by rememberSaveable {
        mutableStateOf(
            ""
        )
    }
    if (result.isEmpty()) {
        LoadingScreen(paddingValues = paddingValues)
    } else {
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                if (pgNo > 1) {
                    ElevatedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { pgNo-- },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            hoveredElevation = 4.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 6.dp
                        ),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = "PREVIOUS PAGE (${pgNo - 1})")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (pgNo < result.size) {
                    ElevatedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { pgNo++ },
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            hoveredElevation = 4.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 6.dp
                        ),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(text = "NEXT PAGE (${pgNo + 1})")
                    }
                }
            }
            LazyVerticalGrid(GridCells.Fixed(3)) {
                items(result[pgNo]!!) { url ->
                    WallpaperCard(url) {
                        download = !download
                        downloadUrl = url
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
//                item {
//                    if (pgNo > 1) {
//                        ElevatedButton(
//                            modifier = Modifier.weight(1f),
//                            onClick = { pgNo-- },
//                            elevation = ButtonDefaults.buttonElevation(
//                                defaultElevation = 8.dp,
//                                hoveredElevation = 4.dp,
//                                pressedElevation = 0.dp,
//                                focusedElevation = 6.dp
//                            ),
//                            border = BorderStroke(
//                                1.dp,
//                                MaterialTheme.colorScheme.onTertiaryContainer
//                            )
//                        ) {
//                            Text(text = "PREVIOUS PAGE")
//                        }
//                        Spacer(modifier = Modifier.width(4.dp))
//                    }
//                }
//                item {
//                    if (pgNo < result.size) {
//                        ElevatedButton(
//                            modifier = Modifier.weight(1f),
//                            onClick = { pgNo++ },
//                            elevation = ButtonDefaults.buttonElevation(
//                                defaultElevation = 8.dp,
//                                hoveredElevation = 4.dp,
//                                pressedElevation = 0.dp,
//                                focusedElevation = 6.dp
//                            ),
//                            border = BorderStroke(
//                                1.dp,
//                                MaterialTheme.colorScheme.onTertiaryContainer
//                            )
//                        ) {
//                            Text(text = "NEXT PAGE")
//                        }
//                    }
//                }
            }
        }
        if (download) {
            DialogWithImage(
                onDismissRequest = {
                    download = !download
                    downloadUrl = ""
                },
                onConfirmation = {
                    downloader.downloadFile(downloadUrl)
                    download = !download
                    Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show()
                },
                url = downloadUrl
            )
        }

    }
}


@Composable
fun WallpaperCard(url: String, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .animateContentSize()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 0.dp,
            hoveredElevation = 6.dp,
            focusedElevation = 4.dp
        )
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxSize(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .placeholder(R.drawable.wallpaper_placeholder)
                .crossfade(500)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}


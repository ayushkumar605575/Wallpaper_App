package com.ayush.wallpapers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ayush.wallpapers.R
import com.ayush.wallpapers.data.Category
import com.ayush.wallpapers.data.WallpaperViewModel
import com.ayush.wallpapers.network.ConnectivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

private val categoryBackground = listOf(
    R.drawable.resource_abstract,
    R.drawable.blob,
    R.drawable.blob_scene_haikei,
    R.drawable.layered_waves_haikei,
    R.drawable.stacked_waves,
    R.drawable.symbol_scatter_haikei,
    R.drawable.technology,
    R.drawable.wave_haikei
)

@Composable
fun WallpaperCategoryScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    wallpaperViewModel: WallpaperViewModel = viewModel()
) {
    val category = wallpaperViewModel.state.collectAsState(initial = listOf()).value
    if (category.isEmpty()) {
        LoadingScreen(paddingValues = paddingValues)
    } else {
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyVerticalGrid(GridCells.Fixed(2)) {
                items(category) { categoryName ->
                    ElevatedCard(
                        modifier = Modifier
                            .size(140.dp)
                            .padding(16.dp)
                            .clickable {
                                Category = categoryName
                                navController.navigate("wallpaperList")
                            },
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 8.dp,
                            hoveredElevation = 4.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 6.dp
                        )
                    ) {
                        Box {
                            Image(
                                painter = painterResource(
                                    id = categoryBackground[Random.nextInt(
                                        0,
                                        categoryBackground.size
                                    )]
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Text(
                                    text = categoryName.replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(
                                            Locale.ROOT
                                        ) else it.toString()
                                    }.replace("+", " "),
                                    fontStyle = FontStyle.Italic,
                                    fontFamily = FontFamily.Cursive,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavGraphModel(paddingValues: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "category") {
        composable("category") {
            WallpaperCategoryScreen(navController, paddingValues)
        }
        composable("wallpaperList") {
            WallpaperListScreen(paddingValues)
        }
    }

}

@Composable
fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Loading...",
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
fun NetworkIconStatus(imageVector: ImageVector) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer
    )
}


@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    url: String,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            LazyColumn {
                item {
                    Column(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalArrangement = Arrangement.Top,
                    ) {

                        AsyncImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .placeholder(R.drawable.wallpaper_placeholder)
                                .crossfade(200)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )

                        Text(
                            text = "Do you want to Download this Wallpaper?",
                            modifier = Modifier.padding(16.dp),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            TextButton(
                                onClick = { onDismissRequest() },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text("DISMISS", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(
                                onClick = { onConfirmation() },
                                modifier = Modifier.padding(8.dp),
                            ) {
                                Text(
                                    "DOWNLOAD",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun internetStatusUpdate(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    status: ConnectivityObserver.Status
) {
    when (status) {
        ConnectivityObserver.Status.Unavailable -> scope.launch {
            snackBarHostState.showSnackbar("Internet is Unavailable")
        }

        ConnectivityObserver.Status.Available -> scope.launch {
            snackBarHostState.showSnackbar("Internet is Connected")
        }

        ConnectivityObserver.Status.Losing -> scope.launch {
            snackBarHostState.showSnackbar("Internet Connection Losing")
        }

        ConnectivityObserver.Status.Lost -> scope.launch {
            snackBarHostState.showSnackbar("Internet Connection Lost!")
        }
    }
}
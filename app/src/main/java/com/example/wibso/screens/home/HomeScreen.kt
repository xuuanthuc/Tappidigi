package com.example.wibso.screens.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wibso.ui.components.IconButton
import com.example.wibso.ui.components.IconConfig
import com.example.wibso.ui.components.TextButton
import com.example.wibso.ui.components.TextConfig
import com.example.wibso.utils.ColorsPalette
import xt.qc.tappidigi.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun HomeScreen() {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController, startDestination = "home"
        ) {

            composable("home") {
                HomeScreens(
                    navController,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                )
            }

            composable(
                "details/{item}", arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item")
                val snack = listSnacks[id!!]
                DetailsScreen(
                    navController,
                    id,
                    snack,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedContentScope = this@composable
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun DetailsScreen(
    navController: NavHostController,
    id: Int,
    snack: SnackItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    with(sharedTransitionScope) {
        Column(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    }, indication = null
                ) {
                    navController.navigate("home")
                }) {
            Image(
                painterResource(id = snack.image),
                contentDescription = snack.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-$id"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
            Text(
                snack.name, fontSize = 38.sp, modifier = Modifier.sharedBounds(
                    sharedTransitionScope.rememberSharedContentState(key = "text-$id"),
                    animatedVisibilityScope = animatedContentScope,
                )
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeScreens(
    navController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {


    Column() {
        IconButton(
            decoration = IconConfig().copyWith(
                painterResource = R.drawable.microphone, contentColor = ColorsPalette.forestMoss
            ),
            onClick = {
                println("Button icon clicked!")
            },
        )
        IconButton(
            onClick = {
                println("Button icon clicked!")
            },
        )
        IconButton(
            decoration = IconConfig().copyWith(
                modifier = Modifier.size(60.dp), contentPadding = PaddingValues(16.dp)
            ),
            onClick = {
                println("Button icon clicked!")
            },
        )
        TextButton(
            onClick = {}, label = "Send", decoration = TextConfig().copyWith(
                isFullWidth = true
            )
        )
        TextButton(
            onClick = {}, label = "Send"
        )
    }


//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(8.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        itemsIndexed(listSnacks) { index, item ->
//            Row(
//                Modifier.clickable(
//                    interactionSource = remember {
//                        MutableInteractionSource()
//                    },
//                    indication = null
//                ) {
//                    navController.navigate("details/$index")
//                }
//            ) {
//                Spacer(modifier = Modifier.width(8.dp))
//                with(sharedTransitionScope) {
//                    Image(
//                        painterResource(id = item.image),
//                        contentDescription = item.description,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//                            .sharedElement(
//                                sharedTransitionScope.rememberSharedContentState(key = "image-$index"),
//                                animatedVisibilityScope = animatedContentScope
//                            )
//                            .size(100.dp)
//                            .dragAndDropSource {
//                                detectTapGestures(
//                                    onLongPress = {
//                                        startTransfer(
//                                            DragAndDropTransferData(
//                                                ClipData.newPlainText("image uri", "url")
//                                            )
//                                        )
//                                    }
//                                )
//                            }
//                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        item.name, fontSize = 18.sp,
//                        modifier = Modifier
//                            .align(Alignment.CenterVertically)
//                            .sharedBounds(
//                                sharedTransitionScope.rememberSharedContentState(key = "text-$index"),
//                                animatedVisibilityScope = animatedContentScope
//                            )
//                            .dragAndDropSource {
//                                detectTapGestures(
//                                    onLongPress = {
//                                        startTransfer(
//                                            DragAndDropTransferData(
//                                                ClipData.newPlainText("image uri", "url")
//                                            )
//                                        )
//                                    }
//                                )
//                            }
//                    )
//                }
//            }
//        }
//    }
}
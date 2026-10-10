package com.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.audio.BackgroundMusicManager
import com.example.core.audio.SoundManager
import com.example.data.model.CategoryType
import com.example.data.model.ThemeMode
import com.example.data.preferences.UserPreferencesRepository
import com.example.feature.activity.MiniActivityScreen
import com.example.feature.garden.LearningGardenScreen
import com.example.feature.home.HomeScreen
import com.example.feature.learn.LearnCharacterScreen
import com.example.feature.progress.ProgressTrackingScreen
import com.example.feature.reward.RewardScreen
import com.example.feature.selection.CategorySelectionScreen
import com.example.feature.certificate.CertificateScreen
import com.example.feature.coloring.ColoringStudioScreen
import com.example.feature.pet.VirtualPetScreen
import com.example.feature.stickers.StickerGalleryScreen
import com.example.feature.tracing.TracingScreen
import com.example.ui.theme.TinyTrailsTheme
import kotlinx.coroutines.launch

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.example.core.ads.AdManager
import com.example.core.ads.AdPlacement
import com.example.feature.parent.ParentAreaScreen
import java.util.Locale
import android.content.res.Configuration

@Composable
fun TinyTrailsApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesRepo = remember { UserPreferencesRepository(context) }
    val currentTheme by preferencesRepo.themeModeFlow.collectAsState(initial = ThemeMode.HAPPY_GARDEN)
    val currentLanguage by preferencesRepo.languageFlow.collectAsState(initial = "en")
    val audioFeedbackEnabled by preferencesRepo.audioFeedbackEnabledFlow.collectAsState(initial = true)
    val backgroundMusicEnabled by preferencesRepo.backgroundMusicEnabledFlow.collectAsState(initial = true)
    val appVolume by preferencesRepo.appVolumeFlow.collectAsState(initial = 0.80f)

    val soundManager = remember { SoundManager.getInstance(context) }
    val musicManager = remember { BackgroundMusicManager.getInstance(context) }
    val adManager = remember { AdManager.getInstance(context) }

    LaunchedEffect(audioFeedbackEnabled, appVolume) {
        soundManager.setSfxEnabled(audioFeedbackEnabled)
        soundManager.setVolume(appVolume)
    }

    LaunchedEffect(backgroundMusicEnabled, appVolume) {
        musicManager.setMusicEnabled(backgroundMusicEnabled)
        musicManager.setVolume(appVolume)
    }

    DisposableEffect(Unit) {
        if (backgroundMusicEnabled) {
            musicManager.start()
        }
        onDispose {
            musicManager.stop()
        }
    }

    val navController = rememberNavController()

    val localizedContext = remember(context, currentLanguage) {
        val locale = Locale(currentLanguage)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        TinyTrailsTheme(themeMode = currentTheme) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    HomeScreen(
                        currentTheme = currentTheme,
                        currentLanguage = currentLanguage,
                        audioFeedbackEnabled = audioFeedbackEnabled,
                        backgroundMusicEnabled = backgroundMusicEnabled,
                        appVolume = appVolume,
                        onThemeChange = { newMode ->
                            scope.launch { preferencesRepo.setThemeMode(newMode) }
                        },
                        onLanguageChange = { newLang ->
                            scope.launch { preferencesRepo.setLanguage(newLang) }
                        },
                        onAudioFeedbackChange = { enabled ->
                            scope.launch { preferencesRepo.setAudioFeedbackEnabled(enabled) }
                        },
                        onBackgroundMusicChange = { enabled ->
                            scope.launch { preferencesRepo.setBackgroundMusicEnabled(enabled) }
                        },
                        onAppVolumeChange = { volume ->
                            scope.launch { preferencesRepo.setAppVolume(volume) }
                        },
                        onNavigateCategory = { cat ->
                            navController.navigate("selection/${cat.name}")
                        },
                        onStartTodayLesson = { charId ->
                            navController.navigate("learn/$charId")
                        },
                        onNavigateGarden = {
                            navController.navigate("garden")
                        },
                        onNavigateStickers = {
                            navController.navigate("stickers")
                        },
                        onNavigateParentArea = {
                            navController.navigate("parent")
                        },
                        onNavigateProgress = {
                            navController.navigate("progress")
                        },
                        onNavigateColoring = {
                            navController.navigate("coloring")
                        },
                        onNavigatePet = {
                            navController.navigate("pet")
                        },
                        onNavigateCertificates = {
                            navController.navigate("certificates")
                        }
                    )
                }

                composable(
                    route = "selection/{categoryType}",
                    arguments = listOf(navArgument("categoryType") { type = NavType.StringType })
                ) { backStackEntry ->
                    val catName = backStackEntry.arguments?.getString("categoryType") ?: CategoryType.NUMBERS.name
                    val categoryType = try {
                        CategoryType.valueOf(catName)
                    } catch (e: Exception) {
                        CategoryType.NUMBERS
                    }

                    CategorySelectionScreen(
                        categoryType = categoryType,
                        onBack = { navController.popBackStack() },
                        onSelectCharacter = { charId ->
                            navController.navigate("learn/$charId")
                        }
                    )
                }

                composable(
                    route = "learn/{characterId}",
                    arguments = listOf(navArgument("characterId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val charId = backStackEntry.arguments?.getString("characterId") ?: "num_1"

                    LearnCharacterScreen(
                        characterId = charId,
                        onBack = { navController.popBackStack() },
                        onStartTracing = { id ->
                            navController.navigate("trace/$id")
                        }
                    )
                }

                composable(
                    route = "trace/{characterId}",
                    arguments = listOf(navArgument("characterId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val charId = backStackEntry.arguments?.getString("characterId") ?: "num_1"

                    TracingScreen(
                        characterId = charId,
                        onBack = { navController.popBackStack() },
                        onTracingComplete = { id, stars ->
                            navController.navigate("activity/$id/$stars") {
                                popUpTo("learn/$id") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "activity/{characterId}/{stars}",
                    arguments = listOf(
                        navArgument("characterId") { type = NavType.StringType },
                        navArgument("stars") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val charId = backStackEntry.arguments?.getString("characterId") ?: "num_1"
                    val stars = backStackEntry.arguments?.getInt("stars") ?: 3

                    MiniActivityScreen(
                        characterId = charId,
                        starsEarned = stars,
                        onBack = { navController.popBackStack() },
                        onActivityCompleted = { id, s ->
                            navController.navigate("reward/$id/$s") {
                                popUpTo("activity/$id/$s") { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = "reward/{characterId}/{stars}",
                    arguments = listOf(
                        navArgument("characterId") { type = NavType.StringType },
                        navArgument("stars") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val charId = backStackEntry.arguments?.getString("characterId") ?: "num_1"
                    val stars = backStackEntry.arguments?.getInt("stars") ?: 3

                    RewardScreen(
                        characterId = charId,
                        starsEarned = stars,
                        onNextLesson = { nextCharId ->
                            adManager.showSafeTransitionAd(AdPlacement.DAILY_SESSION_COMPLETED) {
                                navController.navigate("learn/$nextCharId") {
                                    popUpTo("home")
                                }
                            }
                        },
                        onBackHome = {
                            adManager.showSafeTransitionAd(AdPlacement.DAILY_SESSION_COMPLETED) {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("stickers") {
                    StickerGalleryScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("garden") {
                    LearningGardenScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("parent") {
                    ParentAreaScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("progress") {
                    ProgressTrackingScreen(
                        onBack = { navController.popBackStack() },
                        onSelectCharacter = { charId ->
                            navController.navigate("learn/$charId")
                        },
                        onNavigateCertificates = {
                            navController.navigate("certificates")
                        }
                    )
                }

                composable("coloring") {
                    ColoringStudioScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("pet") {
                    VirtualPetScreen(
                        onBack = { navController.popBackStack() }
                    )
                }

                composable("certificates") {
                    CertificateScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

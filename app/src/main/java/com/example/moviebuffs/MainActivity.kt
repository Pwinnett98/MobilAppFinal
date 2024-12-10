package com.example.moviebuffs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.moviebuffs.ui.MovieApp
import com.example.moviebuffs.ui.theme.MovieBuffsTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieBuffsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val windowSize = calculateWindowSizeClass(this)
                    MovieApp(
                        windowSize = windowSize.widthSizeClass
                    )
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MovieBuffsAppCompactPreview() {
        MovieBuffsTheme {
            Surface {
                MovieApp(
                    windowSize = WindowWidthSizeClass.Compact
                )
            }
        }
    }

    @Preview(showBackground = true, widthDp = 700)
    @Composable
    fun MovieBuffsAppMediumPreview() {
        MovieBuffsTheme {
            Surface {
                MovieApp(
                    windowSize = WindowWidthSizeClass.Medium
                )
            }
        }
    }

    @Preview(showBackground = true, widthDp = 1000)
    @Composable
    fun MovieBuffsAppExpandedPreview() {
        MovieBuffsTheme {
            Surface {
                MovieApp(
                    windowSize = WindowWidthSizeClass.Expanded
                )
            }
        }
    }
}
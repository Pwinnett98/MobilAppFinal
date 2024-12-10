@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.moviebuffs.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviebuffs.R
import com.example.moviebuffs.ui.utils.MovieContentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val viewModel: MovieViewModel = viewModel()
    val uiState by viewModel.navigationState.collectAsState()
    val contentType: MovieContentType
    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            contentType = MovieContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            contentType = MovieContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Expanded -> {
            contentType = MovieContentType.LIST_AND_DETAIL
        }
        else -> {
            contentType = MovieContentType.LIST_ONLY
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MovieTopAppBar(
                isShowingListPage = uiState.isShowingListPage,
                onBackButtonClick = { androidx.lifecycle.viewmodel.compose.viewModel.navigateToListPage() }
            )
        }
    ) {
            HomeScreen(
                viewModel = MovieViewModel,
                contentType = contentType,
                retryAction = MovieViewModel::getMovies,
                contentPadding = it
            )
        }
    }

@Composable
fun MovieTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    isShowingListPage: Boolean,
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        navigationIcon = {
            if (!isShowingListPage){
                IconButton(onClick = onBackButtonClick){
                    Icon(
                        painter = painterResource(R.drawable.arrow_circle_left),
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier
    )
}
package com.example.moviebuffs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviebuffs.R
import com.example.moviebuffs.network.MovieApiService
import com.example.moviebuffs.network.Movies
import com.example.moviebuffs.ui.theme.MovieBuffsTheme
import com.example.moviebuffs.ui.utils.MovieContentType

@Composable
fun HomeScreen(
    movieUiState: MovieUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (movieUiState) {
        is MovieUiState.Loading -> LoadingScreen(modifier = modifier)
        is MovieUiState.Success -> PhotosGridScreen(movieUiState.movies, modifier)
        is MovieUiState.Error -> ErrorScreen(retryAction, modifier = modifier)
    }
}

@Composable
fun MovieApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val viewModel: MovieViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
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
        topBar = {
            MovieAppBar(
                isShowingListPage = uiState.isShowingListPage,
                onBackButtonClick = { viewModel.navigateToListPage() },
            )
        }
    ) { innerPadding ->
        if (contentType == MovieContentType.LIST_AND_DETAIL) {
            MovieListAndDetails(
                movies = uiState.movieList,
                onClick = {
                    viewModel.updateCurrentMovie(it)
                },
                selectedMovie = uiState.currentMovie,
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            if (uiState.isShowingListPage) {
                MovieList(
                    movies = uiState.movieList,
                    onClick = {
                        viewModel.updateCurrentMovie(it)
                        viewModel.navigateToDetailPage()
                    },
                    contentPadding = innerPadding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = dimensionResource(R.dimen.padding_medium),
                            start = dimensionResource(R.dimen.padding_medium),
                            end = dimensionResource(R.dimen.padding_medium),
                        )
                )
            } else {
                MovieDetail(
                    selectedMovie = uiState.currentMovie,
                    contentPadding = innerPadding,
                    onBackPressed = {
                        viewModel.navigateToListPage()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppBar(
    onBackButtonClick: () -> Unit,
    isShowingListPage: Boolean,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text =
                if (!isShowingListPage) {
                    stringResource(R.string.detail_fragment_label)
                } else {
                    stringResource(R.string.list_fragment_label)
                }
            )
        },
        navigationIcon = if (!isShowingListPage) {
            {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        } else {
            { Box {} }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier,
    )
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }

    }
}

@Composable
fun PhotosGridScreen(
    movies: List<Movies>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(movies, key = { movies -> movies.title }) { movies ->
            MovieCard(
                movies: List<Movies>,
                onClick: (Movies) -> Unit,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )
        }
    }
}

@Composable
fun MovieCard(
    movies: List<Movies>,
    onClick: (Movies) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(movies)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.movies),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MovieListItem(
    movies: Movies,
    onItemClick: (Movies) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        onClick = { onItemClick(movies) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .size(dimensionResource(R.dimen.card_image_height))
        ) {
            Image(
                painter = painterResource(movies.imageResourceId),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.size(dimensionResource(R.dimen.card_image_height))
            )
            Column(
                modifier = Modifier
                    .padding(
                        vertical = dimensionResource(R.dimen.padding_small),
                        horizontal = dimensionResource(R.dimen.padding_medium)
                    )
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(movies.titleResourceId),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.card_text_vertical_space))
                )
                Text(
                    text = stringResource(movies.subtitleResourceId),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
                Spacer(Modifier.weight(1f))
                Row {
                    Text(
                        text = StringResource(
                            movies.poster,
                            movies.description,
                        ),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }

@Composable
private fun MovieList(
    movies: List<Movies>,
    onClick: (Movies) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier,
    ) {
        items(movies, key = { movies -> movies.title }) { movies ->
            MovieListItem(
                movies = movies,
                onItemClick = onClick,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MovieDetail(
    selectedMovie: Movies,
    onBackPressed: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackPressed()
    }

    Column(
        modifier = modifier
            .verticalScroll(state = rememberScrollState())
            .padding(contentPadding)
    ) {
        Box {
            Image(
                modifier = modifier
                    .size(64.dp)
                    .padding(8.dp)
                    .clip(MaterialTheme.shapes.small),
                painter = painterResource(selectedMovie.movieImageBanner),
                contentDescription = null,
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.FillWidth,
            )
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.scrim),
                            0f,
                            400f
                        )
                    )
            ) {
                Text(
                    text = stringResource(selectedMovie.titleResourceId),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    modifier = Modifier
                        .padding(horizontal = dimensionResource(R.dimen.padding_small))
                )
                Row(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                ) {
                    Text(
                        text = StringResource(
                            selectedMovie.poster,
                            selectedMovie.description
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Text(
            text = stringResource(selectedMovie.movieDetails),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(
                vertical = dimensionResource(R.dimen.padding_detail_content_vertical),
                horizontal = dimensionResource(R.dimen.padding_detail_content_horizontal)
            )
        )
    }
}

@Composable
fun MovieListAndDetails(
    movies: List<Movies>,
    onClick: (Movies) -> Unit,
    selectedMovie: Movies,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        MovieList(
            movies = movies,
            onClick = onClick,
            contentPadding = contentPadding,
            modifier = Modifier
                .weight(2f)
                .padding(
                    top = dimensionResource(R.dimen.padding_medium),
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium)
                )
        )
        MovieDetail(
            selectedMovie = selectedMovie,
            onBackPressed = { },
            contentPadding = contentPadding,
            modifier = Modifier.weight(3f)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    MovieBuffsTheme {
        val mockData = List(10) { Movies("$it", "") }
        PhotosGridScreen(mockData)
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

@Preview
@Composable
fun MovieListItemPreview() {
    MovieBuffsTheme {
        MovieListItem(
            movies = MovieApiService.defaultMovie,
            onItemClick = {}
        )
    }
}

@Preview
@Composable
fun MovieListPreview() {
    MovieBuffsTheme {
        Surface {
            MovieList(
                movies = MovieApiService.getMovies(),
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
fun MovieListandDetailsPreview() {
    MovieBuffsTheme {
        Surface {
            MovieList(
                movies = MovieApiService.selectedMovies(),
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
fun MovieBuffsDarkThemePreview() {
    MovieBuffsTheme(darkTheme = true) {
        MovieApp()
    }
}
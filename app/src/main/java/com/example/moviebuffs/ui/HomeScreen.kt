package com.example.moviebuffs.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moviebuffs.R
import com.example.moviebuffs.network.Movies
import com.example.moviebuffs.ui.theme.MovieBuffsTheme
import com.example.moviebuffs.ui.utils.MovieContentType

@Composable
fun HomeScreen(
    movieUiState: MovieUiState,
    contentType: MovieContentType,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val movieUiState = MovieViewModel.MovieUiState

    when (movieUiState) {
        is MovieUiState.Loading -> LoadingScreen(modifier = modifier)
        is MovieUiState.Success -> {
            MovieNav(
                viewModel = movieUiState,
                movies = (movieUiState as MovieUiState.Success).movies,
                contentType = contentType,
                contentPadding = contentPadding,
            ) }
        is MovieUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
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
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }

    }
}

@Composable
fun MovieNav(
    viewModel : MovieViewModel,
    movies: List<Movies>,
    modifier: Modifier = Modifier,
    contentType: MovieContentType,
    contentPadding: PaddingValues
) {
    val uiState by viewModel.navigationState.collectAsState()
    val currentMovie = uiState.currentMovie ?: movies.firstOrNull()
    MovieLazyScreen (
        movies = movies,
        modifier = Modifier,
        contentPadding = contentPadding
    )

    LauchedEffects(Unit){
        viewModel.getMovies()
    }
    if (contentType == MovieContentType.LIST_AND_DETAIL){
        currentMovie?.let { movie ->
            MovieListAndDetails(
                movies = movies,
                onClick = {currentMovie ->
                    viewModel.updateCurrentMovie(currentMovie)
                    viewModel.navigateToDetailPage()
                },
                currentMovie = movie,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun MovieCard(
    movies: Movies,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .fillMaxWidth()
            .padding(top = 8.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier
                    .width(120.dp)
                    .padding(end = 2.dp)
            )
            Column(
                modifier = Modifier.padding(end = 2.dp)
            ) {
                Text(
                    text = movies.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movies.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(end = 2.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.star),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = movies.reviewScore,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

    @Composable
    fun MovieList(
        movies: List<Movies>,
        onMovieClick: (Movies) -> Unit,
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(0.dp),
    ) {
        LazyColumn(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
            modifier = modifier.fillMaxWidth(),
        ) {
            items(movies, key = { movies -> movies.title }) { movies ->
                MovieCard(
                    movies = movies,
                    onClick = { onMovieClick(movies)},
                    modifier = modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun MovieDetail(
        movie: Movies,
        onBackPressed: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        BackHandler {
            onBackPressed()
        }
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(movie.bigImage)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.moviebuffs_icon),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = movie.contentRating,
                    style = MaterialTheme.typography.titleMedium
                )

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )

                Text(
                    text = movie.length,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = movie.releaseDate,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    painter = painterResource(R.drawable.star),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = movie.reviewScore,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }

    @Composable
    fun MovieListAndDetails(
        movies: List<Movies>,
        onClick: (Movies) -> Unit,
        currentMovie: Movies?,
        contentPadding: PaddingValues,
        modifier: Modifier = Modifier
    ) {
        Row(modifier = modifier) {
            MovieList(
                movies = movies,
                onMovieClick = onClick,
                modifier = Modifier.weight(2f),
            )
            currentMovie?.let { current ->
                MovieDetail(
                    movie = current,
                    onBackPressed = {},
                    modifier = Modifier.weight(3f)
                )
            }
        }
    }

@Composable
fun MovieLazyScreen(
    movies: List<Movies>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
){
    MovieList(
        movies = movies,
        onMovieClick = { },
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@Preview(showBackground = true)
@Composable
fun MovieCardPreview() {
    MovieBuffsTheme {
        Surface {
            val mockMovie = Movies(
                title = "Mock Movie",
                poster = "mock_poster_url",
                description = "Mock description",
                reviewScore = "4.5",
                bigImage = "",
                contentRating = "R",
                releaseDate = "July 21, 2023",
                length = "2h 00m"
            )
            MovieCard(
                movies = mockMovie,
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesListPreview() {
    MovieBuffsTheme {
        Surface {
            val mockData = List(18) { Movies("", "", "", "", "", " ", "", "") }
            MovieList(
                movies = mockData,
                onMovieClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MoviesDetailPreview() {
    MovieBuffsTheme {
        Surface {
            val mockMovie = Movies("", "", "", "", "", "", "", "")
            MovieDetail(
                movie = mockMovie,
                onBackPressed = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieLazyScreenPreview() {
    MovieBuffsTheme {
        val mockData = List(18) { Movies("", "", "", "", "", " ", "", "") }
        val contentPadding = PaddingValues(0.dp)
        MovieLazyScreen(
            movies = mockData,
            modifier = Modifier,
            contentPadding = contentPadding
        )
    }
}
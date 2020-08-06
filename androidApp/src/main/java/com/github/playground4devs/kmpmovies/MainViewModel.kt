package com.github.playground4devs.kmpmovies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.playground4devs.movies.Movie
import com.github.playground4devs.movies.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _movieList = MutableStateFlow<List<Movie>>(emptyList())
    val movieList = _movieList as StateFlow<List<Movie>>


    private val _currentMovie = MutableStateFlow<Movie?>(null)
    val currentMovie = _currentMovie as StateFlow<Movie?>

    init {
        viewModelScope.launch {
            _movieList.value = MovieRepository().loadMovies()
        }
    }

    fun onClickMovie(movie: Movie) {
        _currentMovie.value = movie
    }

    fun clearMovie() {
        _currentMovie.value = null
    }
}
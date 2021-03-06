package com.github.playground4devs.movies

import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import com.russhwolf.settings.long
import com.soywiz.klock.DateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MovieRepository(
    private val fetcher: MovieFetcher = MovieFetcher(),
    private val persister: MoviePersister = MoviePersister()
) {

    private val settings: Settings = Settings()

    private var lastDownload by settings.long(key = "last_download")

    suspend fun loadMovies(forceFetch: Boolean = false): Flow<Lce<List<Movie>>> {
        return flow {
            emit(Lce.Loading)
            emit(loadLce(forceFetch))
        }
    }

    private suspend fun loadLce(forceFetch: Boolean): Lce<List<Movie>> {
        val now = DateTime.now().unixMillisLong
        return if (forceFetch || now - lastDownload > 1000 * 60 * 5) {
            try {
                val moviesFromServer = fetcher.fetchMovies()
                persister.persist(moviesFromServer)
                lastDownload = now
                Lce.Success(moviesFromServer)
            } catch (e: Exception) {
                val dataFromCache = persister.load()
                if (dataFromCache != null) {
                    Lce.Success(dataFromCache)
                } else {
                    Lce.Error(e)
                }
            }
        } else {
            Lce.Success(persister.load()!!)
        }
    }
}

package com.rjornelas.netflix_clone.model

data class MovieDetail(
    val movie: Movie,
    val similar: List<Movie>
)
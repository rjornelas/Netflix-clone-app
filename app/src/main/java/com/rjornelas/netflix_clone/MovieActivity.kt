package com.rjornelas.netflix_clone

import android.graphics.drawable.LayerDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AndroidException
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rjornelas.netflix_clone.model.Movie

class MovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val txtTitle: TextView = findViewById(R.id.tv_movie_title)
        val txtSynopsis: TextView = findViewById(R.id.tv_movie_synopsis)
        val txtCast: TextView = findViewById(R.id.tv_cast)
        val rv: RecyclerView = findViewById(R.id.rv_similar)

        txtTitle.text = "Batman"
        txtSynopsis.text = getString(R.string.synopsis)
        txtCast.text = getString(R.string.cast, "Actor A, Actor B, Actress A, Actress B")

        val movies = mutableListOf<Movie>()

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MovieAdapter(movies, R.layout.movie_item_similar)

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable
        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_batman)
        layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
        val coverImg: ImageView = findViewById(R.id.movie_img)
        coverImg.setImageDrawable(layerDrawable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
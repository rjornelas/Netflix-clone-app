package com.rjornelas.netflix_clone

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rjornelas.netflix_clone.model.Movie
import com.rjornelas.netflix_clone.model.MovieDetail
import com.rjornelas.netflix_clone.util.MovieTask
import com.squareup.picasso.Picasso
import java.util.concurrent.Executors

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var txtTitle: TextView
    private lateinit var txtSynopsis: TextView
    private lateinit var txtCast: TextView
    private lateinit var rv: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var progressBar: ProgressBar

    private val movies = mutableListOf<Movie>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        txtTitle = findViewById(R.id.tv_movie_title)
        txtSynopsis = findViewById(R.id.tv_movie_synopsis)
        txtCast = findViewById(R.id.tv_cast)
        rv = findViewById(R.id.rv_similar)
        progressBar = findViewById(R.id.pb_movie_detail)

        val id = intent?.getIntExtra("id", 0) ?: throw IllegalStateException("ID not found")
        val url = "https://api.tiagoaguiar.co/netflixapp/movie/$id?apiKey=2532af7c-9d20-4a01-b351-48f508b8977b"

        MovieTask(this).execute(url)

        adapter = MovieAdapter(movies, R.layout.movie_item_similar)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter

        val toolbar: Toolbar = findViewById(R.id.movie_toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResult(movieDetail: MovieDetail) {
        progressBar.visibility = View.GONE
        txtTitle.text = movieDetail.movie.title
        txtSynopsis.text = movieDetail.movie.desc
        txtCast.text = getString(R.string.cast, movieDetail.movie.cast)

        movies.clear()
        movies.addAll(movieDetail.similar)
        adapter.notifyDataSetChanged()

        Executors.newSingleThreadExecutor().execute{
            val layerDrawable: LayerDrawable = ContextCompat.getDrawable(this@MovieActivity, R.drawable.shadows) as LayerDrawable
            val movieCover = BitmapDrawable(resources, Picasso.get().load(movieDetail.movie.coverUrl).get())
            layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
            val coverImg: ImageView = findViewById(R.id.movie_img)
            this.runOnUiThread {
                coverImg.setImageDrawable(layerDrawable)
            }
        }
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
}
package com.rjornelas.netflix_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rjornelas.netflix_clone.model.Category
import com.rjornelas.netflix_clone.model.Movie
import com.rjornelas.netflix_clone.util.CategoryTask

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = mutableListOf<Category>()

        val adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        CategoryTask().execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=2532af7c-9d20-4a01-b351-48f508b8977b")
    }
}
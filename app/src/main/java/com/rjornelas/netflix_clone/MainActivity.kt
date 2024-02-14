package com.rjornelas.netflix_clone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rjornelas.netflix_clone.model.Category
import com.rjornelas.netflix_clone.util.CategoryTask

class MainActivity : AppCompatActivity(), CategoryTask.Callback {

    private lateinit var progressBar: ProgressBar
    private val categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById(R.id.pb_main)

        adapter = CategoryAdapter(categories)
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=2532af7c-9d20-4a01-b351-48f508b8977b")
    }

    override fun onResult(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)

        adapter.notifyDataSetChanged()

        progressBar.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        progressBar.visibility = View.GONE
    }

    override fun onPreExecute() {
        progressBar.visibility = View.VISIBLE
    }
}
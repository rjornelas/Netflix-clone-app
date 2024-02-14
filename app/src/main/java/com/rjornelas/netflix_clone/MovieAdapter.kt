package com.rjornelas.netflix_clone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.rjornelas.netflix_clone.model.Movie
import com.squareup.picasso.Picasso

class MovieAdapter(
    private val movies: List<Movie>,
    @LayoutRes private val layoutId: Int,
    private val onItemClickListener: ( (Int) -> Unit)? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(movie: Movie){
            val imageCover: ImageView = itemView.findViewById(R.id.iv_movie)
            imageCover.setOnClickListener{
                onItemClickListener?.invoke(movie.id)
            }
            Picasso.get().load(movie.coverUrl).into(imageCover)
        }
    }

}
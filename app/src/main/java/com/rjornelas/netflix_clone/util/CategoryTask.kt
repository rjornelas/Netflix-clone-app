package com.rjornelas.netflix_clone.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rjornelas.netflix_clone.model.Category
import com.rjornelas.netflix_clone.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())

    interface Callback {
        fun onResult(categories: List<Category>)
        fun onFailure(message: String)
        fun onPreExecute()
    }

    fun execute(url: String){
        callback.onPreExecute()
        val executor = Executors.newSingleThreadExecutor()

        executor.execute{
            var urlConnection: HttpsURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {
                val requestUrl = URL(url)
                urlConnection = requestUrl.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 20000
                urlConnection.connectTimeout = 2000

                val statusCode = urlConnection.responseCode

                if(statusCode >= 400){
                    throw IOException("Erro na comunicação com o servidor")
                }

//                1° Forma (Mais simples):
//                val stream = urlConnection.inputStream
//                val jsonAsString = stream.bufferedReader().use {
//                    it.readText()
//                }

//                2° Forma (Mais manual):
                stream = urlConnection.inputStream
                buffer = BufferedInputStream(stream)
                val jsonAsString = toString(buffer)

                Log.i("Teste", jsonAsString)

                val categories = toCategories(jsonAsString)

                handler.post {
                    callback.onResult(categories)
                }

            }catch (ex: IOException){
                val message = ex.message ?: "erro desconhecido"
                Log.e("Teste", message, ex)
                handler.post {
                    callback.onFailure(message)
                }
            }finally {
                urlConnection?.disconnect()
                stream?.close()
                buffer?.close()
            }
        }
    }

    private fun toCategories(jsonAsString: String): List<Category>{
        val categories = mutableListOf<Category>()
        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for(i in 0 until jsonCategories.length()){
            val jsonCategory = jsonCategories.getJSONObject(i)
            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()){
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id.toLong(), coverUrl))
            }
            categories.add(Category(title, movies))
        }
        return categories
    }

    private fun toString(stream: InputStream): String{
        val bytes = ByteArray(1024)
        var read: Int
        val baos = ByteArrayOutputStream()
        while(true){
            read = stream.read(bytes)
            if(read <=0){
                break
            }
            baos.write(bytes, 0, read)
        }
        return String(baos.toByteArray())
    }
}
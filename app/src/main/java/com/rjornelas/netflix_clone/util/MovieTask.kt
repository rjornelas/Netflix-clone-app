package com.rjornelas.netflix_clone.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.rjornelas.netflix_clone.model.Movie
import com.rjornelas.netflix_clone.model.MovieDetail
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handler = Handler(Looper.getMainLooper())

    interface Callback {
        fun onResult(movieDetail: MovieDetail)
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
                urlConnection.connectTimeout = 20000

                val statusCode = urlConnection.responseCode

                if(statusCode == 400){
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)
                    val jsonAsString = toString(buffer)

                    val json = JSONObject(jsonAsString)
                    val message = json.getString("message")
                    throw IOException(message)

                }else if(statusCode > 400){
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

                val movieDetail = toMovieDetail(jsonAsString)

                handler.post {
                    callback.onResult(movieDetail)
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

    private fun toMovieDetail(jsonAsString: String): MovieDetail{
        val json = JSONObject(jsonAsString)
        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("desc")
        val cast = json.getString("cast")
        val coverUrl = json.getString("cover_url")
        val jsonMovies = json.getJSONArray("movie")

        val similarMovies = mutableListOf<Movie>()
        for(i in 0 until jsonMovies.length()){
            val jsonMovie = jsonMovies.getJSONObject(i)

            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            similarMovies.add(Movie(similarId, similarCoverUrl))

        }
        val movie = Movie(id, coverUrl, title, desc, cast)
        return MovieDetail(movie, similarMovies)
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
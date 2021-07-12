package com.risingstar.androidchallenge.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.risingstar.androidchallenge.*
import com.risingstar.androidchallenge.adapter.DBInsertAsync
import com.risingstar.androidchallenge.adapter.MainAdapter
import com.risingstar.androidchallenge.adapter.RetrieveHisAsync
import com.risingstar.androidchallenge.api.myapi
import com.risingstar.androidchallenge.database.HistoryEntity
import com.risingstar.androidchallenge.models.MyResponse
import com.risingstar.androidchallenge.models.resultList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var searchView: SearchView
    lateinit var recycler : RecyclerView
    lateinit var layoutManager: GridLayoutManager
    lateinit var recyclerAdapter : MainAdapter
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar
    lateinit var lvHis : ListView
    lateinit var llHis : LinearLayout
    lateinit var tvHis :TextView
    lateinit var dbHis : List<HistoryEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.searchView)
        recycler = findViewById(R.id.recycler_main)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        lvHis = findViewById(R.id.lvHis)
        llHis = findViewById(R.id.llHis)
        tvHis = findViewById(R.id.tvHis)

        layoutManager = GridLayoutManager(this@MainActivity,2)

        progressLayout.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE

        var artist: String?

        dbHis = RetrieveHisAsync(this@MainActivity).execute().get()

        var names : Array<String?> = arrayOfNulls(dbHis.size)
        for (i in dbHis.indices) {
            names.set(i,dbHis[i].searchedItem)
        }
        val listAdapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names)

        lvHis.adapter = listAdapter

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                progressLayout.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
               searchView.clearFocus()
                artist = query
                var his  = HistoryEntity(0,query)

                if (DBInsertAsync(this@MainActivity,his).execute().get()){
                    Toast.makeText(this@MainActivity, "Searching....", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this@MainActivity, "Could not search at the momennt", Toast.LENGTH_SHORT).show()
                }

                val url = "https://itunes.apple.com/"
                val songsList  = arrayListOf<resultList>()

                val retrofit = Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                val api = retrofit.create(myapi::class.java)

                val call : Call<MyResponse> = api.getmodels(artist)
                call.enqueue(object : Callback<MyResponse>{
                    override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                        if(response.isSuccessful && response.body()!=null) {
                            progressLayout.visibility = View.INVISIBLE
                            progressBar.visibility = View.INVISIBLE
                            llHis.visibility = View.INVISIBLE
                            val data = response.body()?.results
                            if (data != null) {
                                for (element in data) {
                                    val songList = resultList(
                                            element.wrapperType,
                                            element.kind,
                                            element.artistId,
                                            element.collectionId,
                                            element.trackId,
                                            element.artistName,
                                            element.collectionName ,
                                            element.trackName,
                                            element.collectionCensoredName,
                                            element.trackCensoredName,
                                            element.collectionArtistName,
                                            element.artistViewUrl,
                                            element.collectionViewUrl,
                                            element.trackViewUrl,
                                            element.previewUrl,
                                            element.artworkUrl30,
                                            element.artworkUrl60,
                                            element.artworkUrl100,
                                            element.collectionPrice,
                                            element.trackPrice,
                                            element.releaseDate,
                                            element.collectionExplicitness,
                                            element.trackExplicitness,
                                            element.discCount,
                                            element.discNumber,
                                            element.trackCount,
                                            element.trackNumber,
                                            element.trackTimeMillis,
                                            element.country,
                                            element.currency,
                                            element.primaryGenreName,
                                            element.isStreamable
                                    )
                                    songsList.add(songList)
                                    recyclerAdapter = MainAdapter(this@MainActivity as Context,songsList)
                                    recycler.layoutManager = layoutManager
                                    recycler.adapter = recyclerAdapter


                                }
                            }
                        }
                        else{
                            Toast.makeText(this@MainActivity, "Failed to get data", Toast.LENGTH_SHORT).show();
                        }

                    }

                })

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tvHis.visibility = View.INVISIBLE
                listAdapter.filter.filter(newText)
                return false
            }

        })

    }
}

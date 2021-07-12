package com.risingstar.androidchallenge.adapter

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.risingstar.androidchallenge.R
import com.risingstar.androidchallenge.activity.PlaySongsActivity
import com.risingstar.androidchallenge.database.HistoryDatabase
import com.risingstar.androidchallenge.database.HistoryEntity
import com.risingstar.androidchallenge.models.resultList
import java.io.IOException

class MainAdapter(var context : Context, var itemList: ArrayList<resultList>) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    class MainViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val tvSong : TextView = view.findViewById(R.id.tvSong)
        val tvCollection : TextView = view.findViewById(R.id.tvCollection)
        val tvArtist : TextView = view.findViewById(R.id.tvArtist)
        val cardSong : CardView = view.findViewById(R.id.cardSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_single_block,parent,false)
        return MainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val songs = itemList[position]
        holder.tvSong.text = songs.trackName.toString().trim()
        holder.tvCollection.text = songs.collectionName.toString().trim()
        holder.tvArtist.text = songs.artistName.toString().trim()

        holder.cardSong.setOnClickListener {
            val intent = Intent(context,PlaySongsActivity::class.java)
            intent.putExtra("Song url",songs.previewUrl)
            intent.putExtra("Title",songs.trackName)
            context.startActivity(intent)
        }

    }


}
class DBInsertAsync(context: Context,val item : HistoryEntity):AsyncTask<Void,Void,Boolean>(){
    val db = Room.databaseBuilder(context, HistoryDatabase::class.java,"hisDB").build()
    override fun doInBackground(vararg params: Void?): Boolean {
        db.hisDao().insert(item)
        db.close()
        return true
    }

}
class RetrieveHisAsync(context: Context):AsyncTask<Void,Void,List<HistoryEntity>>(){
    private val db = Room.databaseBuilder(context, HistoryDatabase::class.java,"hisDB").build()
    override fun doInBackground(vararg params: Void?): List<HistoryEntity> {
        return db.hisDao().getHistory()
    }

}
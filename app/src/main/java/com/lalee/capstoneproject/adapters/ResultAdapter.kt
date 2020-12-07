package com.lalee.capstoneproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.TrashResult
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_result.view.*

class ResultAdapter(private var trashResults: ArrayList<TrashResult>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_result, parent, false))
    }

    override fun onBindViewHolder(holder: ResultAdapter.ViewHolder, position: Int) {
        holder.dataBind(trashResults[position])
    }

    override fun getItemCount(): Int {
        return trashResults.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun dataBind(trashResult: TrashResult){
            itemView.tv_home_title.text = trashResult.name
            itemView.iv_result_logo.setImageBitmap(trashResult.image)
        }
    }


}
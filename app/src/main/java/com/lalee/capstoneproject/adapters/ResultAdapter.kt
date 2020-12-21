package com.lalee.capstoneproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lalee.capstoneproject.R
import com.lalee.capstoneproject.model.TrashType
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_result.view.*

class ResultAdapter(private var trashResults: ArrayList<TrashType>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false))
    }

    override fun onBindViewHolder(holder: ResultAdapter.ViewHolder, position: Int) {
        holder.dataBind(trashResults[position])
    }

    override fun getItemCount(): Int {
        return trashResults.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun dataBind(trashType: TrashType){
            itemView.tv_result_title.text = trashType.name
            itemView.tv_result_extrainfo.text = trashType.info
            Glide.with(itemView.context).load(trashType.imageUrl).into(itemView.iv_result_logo)
            itemView.iv_result_small_logo.setImageResource(R.drawable.re)
        }
    }


}
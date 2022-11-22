package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.databinding.ItemViewpagerImageBinding

class PagerImageAdapter(var imageList : List<String>) :
    RecyclerView.Adapter<PagerImageAdapter.PagerViewHolder>() {
    inner class  PagerViewHolder(val binding: ItemViewpagerImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position : Int,image : String){
            Glide.with(itemView)
                .load(image)
                .into(binding.ivImageItem)
            if(position <= imageList.size){
                val endPosition = if(position + 2 > imageList.size){
                    imageList.size
                }else{
                    position + 2
                }
                imageList.subList(position,endPosition).forEach{
                    Glide.with(itemView)
                        .load(it).preload()
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(ItemViewpagerImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindInfo(position, imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}
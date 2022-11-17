package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.databinding.ItemViewpagerImageBinding

class PagerImageAdapter(var imageList : List<String>) :
    RecyclerView.Adapter<PagerImageAdapter.PagerViewHolder>() {
    private lateinit var binding : ItemViewpagerImageBinding
    inner class  PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent){
        fun bindInfo(image : String){
            Glide.with(itemView)
                .load(image)
                .into(binding.ivImageItem)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        binding = ItemViewpagerImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PagerViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindInfo(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}
package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.data.model.Board
import com.buy.together.databinding.ItemImageBinding

class ImageAdpater (var ImageList : ArrayList<String>) : RecyclerView.Adapter<ImageAdpater.ImageHolder>() {
    private lateinit var binding : ItemImageBinding
    inner class ImageHolder(view : View) : RecyclerView.ViewHolder(view){
        fun bindInfo(position: Int,dto: String){
            Glide.with(binding.ivImageImg)
                .load(dto)
            binding.ibImageClose.setOnClickListener{
                ImageList.removeAt(position)
                notifyItemRemoved(position)
                itemClickListener.onClick(it,ImageList.size)
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, size: Int)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.apply {
            bindInfo(position, ImageList[position])
        }
    }

    override fun getItemCount(): Int = ImageList.size
}
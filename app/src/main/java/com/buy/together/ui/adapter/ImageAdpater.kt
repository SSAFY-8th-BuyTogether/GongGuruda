package com.buy.together.ui.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.R
import com.buy.together.databinding.ItemImageBinding

private const val TAG = "ImageAdpater_싸피"
class ImageAdpater (var content : Context) : RecyclerView.Adapter<ImageAdpater.ImageHolder>() {
    val ImageList : ArrayList<Uri> = arrayListOf()

    fun setListData(data : ArrayList<Uri>){
        ImageList.clear()
        ImageList.addAll(data)
        notifyDataSetChanged()
    }

    inner class ImageHolder(private val binding : ItemImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int,dto: Uri){
            Glide.with(content)
                .load(dto)
                .placeholder(R.drawable.img_category_carrot)
                .error({
                    Log.d(TAG, "bindInfo: error===========")
                })
                .into(binding.ivImageImg)

            binding.ibImageClose.setOnClickListener{
                Log.d(TAG, "clcked: ${ImageList[position]}")
                val index = ImageList.indexOf(dto)
                ImageList.removeAt(index)
                Log.d(TAG, "bindInfo: ${index}")
                Log.d(TAG, "삭제 한 후 결과: ${ImageList}")
                notifyDataSetChanged()
                itemClickListener.onClick(it,ImageList.size)
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, size: Int)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdpater.ImageHolder {
        return ImageHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position : dto ${ImageList[position]}")
        holder.bindInfo(position, ImageList[position])
    }

    override fun getItemCount(): Int = ImageList.size
}
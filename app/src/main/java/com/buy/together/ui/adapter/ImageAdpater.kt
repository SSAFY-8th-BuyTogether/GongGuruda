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
class ImageAdpater (var content : Context, var ImageList : ArrayList<Uri>) : RecyclerView.Adapter<ImageAdpater.ImageHolder>() {
    private lateinit var binding : ItemImageBinding
    inner class ImageHolder(view : View) : RecyclerView.ViewHolder(view){
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
                ImageList.removeAt(position)
                notifyDataSetChanged()
                Log.d(TAG, "finish remove: ${ImageList[position]}")
                Log.d(TAG, "bindInfo: ${ImageList.size}, position : ${position}")
                Log.d(TAG, "bindInfo: ${ImageList}")
                itemClickListener.onClick(it,ImageList.size)
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, size: Int)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdpater.ImageHolder {
        binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position : dto ${ImageList[position]}")
        holder.bindInfo(position, ImageList[position])
    }

    override fun getItemCount(): Int = ImageList.size
}
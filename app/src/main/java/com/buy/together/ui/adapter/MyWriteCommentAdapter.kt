package com.buy.together.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.MyWriteCommentDto
import com.buy.together.databinding.ItemAddressBinding
import com.buy.together.databinding.ItemMyWriteCommentBinding


class MyWriteCommentAdapter() : RecyclerView.Adapter<MyWriteCommentAdapter.Holder>() {

    private val itemList : ArrayList<MyWriteCommentDto> = arrayListOf()

    fun setListData(dataList: ArrayList<MyWriteCommentDto>){
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    inner class Holder(private val binding: ItemMyWriteCommentBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, writeCommentDto: MyWriteCommentDto){
            binding.itemDto = writeCommentDto
            binding.layoutItemMyWriteComment.setOnClickListener { itemClickListener.onClickItem(it, position, itemList[position]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(ItemMyWriteCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val dto = itemList[position]
        holder.apply {
            bindInfo(position, dto)
            itemView.tag = dto
        }
    }

    override fun getItemCount(): Int = itemList.size

    interface ItemClickListener{
        fun onClickItem(view: View, position: Int, writeCommentDto: MyWriteCommentDto)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener){ this.itemClickListener = itemClickListener }

}
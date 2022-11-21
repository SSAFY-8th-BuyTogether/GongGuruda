package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.data.model.domain.MyParticipateDto
import com.buy.together.databinding.ItemMyParticipateBinding

class MyParticipateAdapter() : RecyclerView.Adapter<MyParticipateAdapter.Holder>() {

    private val itemList : ArrayList<MyParticipateDto> = arrayListOf()

    fun setListData(dataList: ArrayList<MyParticipateDto>){
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    inner class Holder(private val binding: ItemMyParticipateBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, participateDto: MyParticipateDto){
            binding.itemDto = participateDto
            binding.layoutItemMyWriteComment.setOnClickListener { itemClickListener.onClickItem(it, position, itemList[position]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(ItemMyParticipateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        fun onClickItem(view: View, position: Int, participateDto: MyParticipateDto)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener){ this.itemClickListener = itemClickListener }

}
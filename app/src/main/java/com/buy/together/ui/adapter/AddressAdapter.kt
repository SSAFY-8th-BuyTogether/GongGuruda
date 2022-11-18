package com.buy.together.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.databinding.ItemAddressBinding

class AddressAdapter() : RecyclerView.Adapter<AddressAdapter.Holder>() {

    private val itemList : ArrayList<AddressDto> = arrayListOf()

    fun setListData(dataList: ArrayList<AddressDto>){
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    inner class Holder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, address: AddressDto){
            binding.apply {
                tvAddressItem.text = address.addressDetail
                tvAddressItem.setOnClickListener { itemClickListener.onClickItem(it, position, itemList[position]) }
                btnAddressItemDelete.setOnClickListener { itemClickListener.onClickRemove(it, position, itemList[position]) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        fun onClickItem(view: View, position: Int, addressDto: AddressDto)
        fun onClickRemove(view: View, position: Int, addressDto: AddressDto)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener){ this.itemClickListener = itemClickListener }
}
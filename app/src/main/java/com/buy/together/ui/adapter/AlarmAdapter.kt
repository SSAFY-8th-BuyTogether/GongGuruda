package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.AlarmDto
import com.buy.together.databinding.ItemAddressBinding
import com.buy.together.databinding.ItemAlarmBinding

class AlarmAdapter() : RecyclerView.Adapter<AlarmAdapter.Holder>() {

    private val itemList : ArrayList<AlarmDto> = arrayListOf()

    fun setListData(dataList: ArrayList<AlarmDto>){
        itemList.clear()
        itemList.addAll(dataList)
        notifyDataSetChanged()
    }


    inner class Holder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, alarmDto: AlarmDto){
            binding.itemDto = alarmDto
            binding.layoutItemAlarm.setOnClickListener { itemClickListener.onClickItem(it, position, itemList[position])  }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
        fun onClickItem(view: View, position: Int, alarmDto: AlarmDto)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener){ this.itemClickListener = itemClickListener }
}
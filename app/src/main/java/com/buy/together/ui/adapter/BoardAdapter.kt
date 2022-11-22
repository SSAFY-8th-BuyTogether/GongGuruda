package com.buy.together.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.Application
import com.buy.together.R
import com.buy.together.data.model.domain.BoardDto
import com.buy.together.databinding.ItemBoardBinding
import com.buy.together.util.CommonUtils

class BoardAdapter : RecyclerView.Adapter<BoardAdapter.BoardHolder>() {
    var boardDtoList : ArrayList<BoardDto> = arrayListOf()

    fun setList(list : ArrayList<BoardDto>) {
        boardDtoList.clear()
        boardDtoList.addAll(list)
        notifyDataSetChanged()
    }

    inner class BoardHolder(private val binding : ItemBoardBinding) : RecyclerView.ViewHolder(binding.root){
        fun bindInfo(position: Int, dto : BoardDto){
            binding.apply {
                val userId = Application.sharedPreferences.getAuthToken()
                if(dto.writer == userId){
                    ibOptionButton.visibility = View.VISIBLE
                }else{
                    ibOptionButton.visibility = View.GONE
                }
                if(dto.images.isEmpty()){
                    binding.ivBoardImg.setImageResource(R.drawable.img_category_carrot)
                }else{
                    Glide.with(itemView)
                        .load(Uri.parse(dto.images[0]))
                        .into(binding.ivBoardImg)
                }
                tvBoardTitle.text = dto.title
                tvBoardMoney.text = CommonUtils.makeComma(dto.price)
                tvBoardTime.text = CommonUtils.getDiffTime(dto.writeTime)
                tvBoardDday.text = CommonUtils.getDday(dto.deadLine)
                tvBoardAddress.text = dto.meetPoint?.split(" ", limit = 2)?.get(0) ?: "지역무관"
                binding.clBoardLayout.setOnClickListener{
                    itemClickListener.onClick(it,boardDtoList[position])
                }
                ibOptionButton.setOnClickListener {
                    itemClickListener.onItemOptionClick(it,dto)
                }
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, dto : BoardDto)
        fun onItemOptionClick(view: View, dto : BoardDto)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardHolder {
        return BoardHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BoardHolder, position: Int) {
        holder.apply {
            bindInfo(position, boardDtoList[position])
        }
    }

    override fun getItemCount(): Int = boardDtoList.size
}
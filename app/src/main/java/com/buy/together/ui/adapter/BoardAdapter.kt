package com.buy.together.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.R
import com.buy.together.data.dto.BoardDto
import com.buy.together.databinding.ItemBoardBinding
import com.buy.together.util.CommonUtils

class BoardAdapter : RecyclerView.Adapter<BoardAdapter.BoardHolder>() {
    private lateinit var binding : ItemBoardBinding
    var boardDtoList : List<BoardDto> = mutableListOf()

    inner class BoardHolder(view : View) : RecyclerView.ViewHolder(view){
        fun bindInfo(position: Int, dto : BoardDto){
            binding.apply {
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
                binding.clBoardLayout.setOnClickListener{
                    itemClickListener.onClick(it,boardDtoList[position])
                }
            }
        }
    }

    interface ItemClickListener{
        fun onClick(view: View, dto : BoardDto)
    }
    lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardHolder {
        binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BoardHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BoardHolder, position: Int) {
        holder.apply {
            bindInfo(position, boardDtoList[position])
        }
    }

    override fun getItemCount(): Int = boardDtoList.size
}
package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.data.model.Board
import com.buy.together.databinding.ItemBoardBinding

private const val TAG = "CategoryAdapter_싸피"
class CategoryAdapter(var boardList : ArrayList<Board>) : RecyclerView.Adapter<CategoryAdapter.BoardHolder>() {
    private lateinit var binding : ItemBoardBinding
    
    inner class BoardHolder(view : View) : RecyclerView.ViewHolder(view){
        fun bindInfo(position: Int){
            binding.apply {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardHolder {
        binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BoardHolder(binding.root)
    }

    override fun onBindViewHolder(holder: BoardHolder, position: Int) {
        holder.apply {
            bindInfo(position)
        }
    }

    override fun getItemCount(): Int = boardList.size
}
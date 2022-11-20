package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.Application
import com.buy.together.data.dto.CommentDto
import com.buy.together.databinding.ItemCommentBinding
import com.buy.together.util.CommonUtils

class CommentAdapter() : RecyclerView.Adapter<CommentAdapter.CommentHolder>(){
    private lateinit var binding : ItemCommentBinding
    var commentList: ArrayList<CommentDto> = arrayListOf()
    inner class CommentHolder(view : View): RecyclerView.ViewHolder(view){
        fun bindInfo(position: Int, comment : CommentDto){
            binding.apply{
                val userId = Application.sharedPreferences.getAuthToken()
                if(comment.writer == userId){
                    ibOptionButton.visibility = View.VISIBLE
                }else{
                    ibOptionButton.visibility = View.GONE
                }
                tvCommentWriter.text = comment.writer
                tvCommentContent.text = comment.content
                if(comment.mention == null){
                    tvMention.visibility = View.GONE
                }else{
                    tvMention.visibility = View.VISIBLE
                    tvMention.text = "\u0040${comment.mention}"
                }
                tvCommentTime.text = CommonUtils.getDateString(comment.time.seconds * 1000)
                ibOptionButton.setOnClickListener {
                    itemClickListener.onItemOptionClick(it,comment)
                }
                clCommentLayout.setOnClickListener{
                    itemClickListener.onClick(it,comment)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bindInfo(position, commentList[position])
    }


    override fun getItemCount(): Int = commentList.size

    interface ItemClickListener{
        fun onClick(view: View, dto : CommentDto)
        fun onItemOptionClick(view: View, dto : CommentDto)
    }
    lateinit var itemClickListener: ItemClickListener //TODO : Mention

}
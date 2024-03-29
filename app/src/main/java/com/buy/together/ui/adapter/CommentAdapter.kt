package com.buy.together.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buy.together.Application
import com.buy.together.data.model.domain.CommentDto
import com.buy.together.databinding.ItemCommentBinding
import com.buy.together.util.CommonUtils

class CommentAdapter() : RecyclerView.Adapter<CommentAdapter.CommentHolder>(){
    var commentList: ArrayList<CommentDto> = arrayListOf()

    fun setListData(data : ArrayList<CommentDto>){
        commentList.clear()
        commentList.addAll(data)
        notifyDataSetChanged()
    }

    inner class CommentHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root){
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
                if(comment.writerProfile != null){
                    Glide.with(itemView)
                        .load(comment.writerProfile)
                        .into(ivWriterProfile)
                }
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentHolder {
        return CommentHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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
package com.buy.together.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buy.together.data.dto.BoardDto
import com.buy.together.data.dto.CommentDto
import com.buy.together.databinding.ItemCommentBinding
import com.buy.together.util.CommonUtils

private const val TAG = "CommentAdapter_싸피"
class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    private lateinit var binding : ItemCommentBinding
    var commentList: ArrayList<CommentDto> = arrayListOf()
    inner class CommentHolder(view : View): RecyclerView.ViewHolder(view){
        fun bindInfo(position: Int, comment : CommentDto){
            binding.apply{
                tvCommentWriter.text = comment.writer
                tvCommentContent.text = comment.content
                Log.d(TAG, "bindInfo: time : ${comment.time}")
                tvCommentTime.text = CommonUtils.getDateString(comment.time.seconds * 1000)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CommentHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        holder.bindInfo(position, commentList[position])
    }



    override fun getItemCount(): Int = commentList.size

    interface ItemClickListener{
        fun onClick(view: View, dto : BoardDto)
    }
    lateinit var itemClickListener: ItemClickListener

}
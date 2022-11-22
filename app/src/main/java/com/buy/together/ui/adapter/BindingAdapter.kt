package com.buy.together.ui.adapter

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.buy.together.data.model.domain.AlarmDto
import com.buy.together.data.model.domain.MyWriteCommentDto
import com.buy.together.util.CommonUtils

object BindingAdapter  {
    @JvmStatic
    @BindingAdapter("imageDrawable")
    fun bindImageFromSrcTypeInt(imageView: ImageView, src: String) {
        val drawableId = imageView.context.resources.getIdentifier(src,"drawable", imageView.context.packageName)
        val imageSrc = if("@drawable" in src) drawableId else Uri.parse(src)
        Glide.with(imageView.context)
            .load(imageSrc)
            .into(imageView)
    }
    @JvmStatic
    @BindingAdapter("writeCommentType")
    fun bindTextFromWriteCommentType(textView: TextView, type : MyWriteCommentDto.TYPE){
        textView.text = if (type==MyWriteCommentDto.TYPE.WRITE) "글" else "댓글"
    }
    @JvmStatic
    @BindingAdapter("writeCommentContent")
    fun bindTextFromWriteCommentContent(textView: TextView, content : String){
        textView.text = if (content.length<10) "\"${content}\"" else "\"${content.substring(0,10)}...\""
    }
    @JvmStatic
    @BindingAdapter("writeCommentDate")
    fun bindTextFromWriteCommentDate(textView: TextView, date : Long){
        textView.text = CommonUtils.getDateString(date*1000)
    }
    @JvmStatic
    @BindingAdapter("alarmType")
    fun bindTextFromAlarmType(textView: TextView, type : AlarmDto.TYPE){
        textView.text = if (type==AlarmDto.TYPE.WRITE) "글" else "댓글"
    }
    @JvmStatic
    @BindingAdapter("alarmContent")
    fun bindTextFromAlarmContent(textView: TextView, content : String){
        textView.text = if (content.length<10) "\"${content}\"" else "\"${content.substring(0,10)}...\""
    }
    @JvmStatic
    @BindingAdapter("alarmDate")
    fun bindTextFromAlarmDate(textView: TextView, date : Long){
        textView.text = CommonUtils.getDiffTime(date*1000)
    }
}
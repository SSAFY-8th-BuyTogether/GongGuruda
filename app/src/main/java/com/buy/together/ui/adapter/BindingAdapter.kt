package com.buy.together.ui.adapter

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageDrawable")
fun bindImageFromSrcTypeInt(imageView: ImageView, src: String) {
    val drawableId = imageView.context.resources.getIdentifier(src,"drawable", imageView.context.packageName)
    val imageSrc = if("@drawable" in src) drawableId else Uri.parse(src)
    Glide.with(imageView.context)
        .load(imageSrc)
        .into(imageView)
}
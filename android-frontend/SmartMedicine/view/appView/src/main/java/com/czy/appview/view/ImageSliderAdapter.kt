package com.czy.appview.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.R
import com.czy.baseutil.image.ImageLoadUtil
import com.makeramen.roundedimageview.RoundedImageView

class ImageSliderAdapter(private val imageSources: List<Any>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>(){

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: RoundedImageView = itemView.findViewById(R.id.imageView) // 这里引用 imageView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val source = imageSources[position]

        when (source) {
            is String -> {
                // 加载 URL
                ImageLoadUtil.loadImageViewByResource(
                    source,
                    holder.imageView
                )
            }
            is Int -> {
                // 加载本地 drawable 资源
                holder.imageView.setImageResource(source)
            }
        }
    }

    override fun getItemCount(): Int = imageSources.size
}
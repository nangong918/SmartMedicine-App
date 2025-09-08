package com.czy.appview.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.czy.appview.R
import com.czy.baseutil.image.ImageLoadUtil

class ImageSliderAdapter(private val imageSources: List<Any>) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>(){

    companion object {
        val TAG: String = ImageSliderAdapter::class.java.name
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView) // 这里引用 imageView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        when (val source = imageSources[position]) {
            is String -> {
                Log.d(TAG, "onBindViewHolder: $source")
                // 加载 URL
                ImageLoadUtil.loadImageViewByResource(
                    source,
                    holder.imageView
                )
            }
            is Int -> {
                // 加载本地 drawable 资源
                Log.d(TAG, "onBindViewHolder: $source")
                holder.imageView.setImageResource(source)
            }
        }
    }

    override fun getItemCount(): Int = imageSources.size
}
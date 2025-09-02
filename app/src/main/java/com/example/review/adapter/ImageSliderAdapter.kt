package com.example.review.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.review.databinding.ItemImageSlideBinding
import kotlin.math.abs

class ImageSliderAdapter(
    private var realUrls: List<String> = emptyList(),
    private val onClick: ((realPosition: Int) -> Unit)? = null
) : RecyclerView.Adapter<ImageSliderAdapter.VH>() {

    // 양끝 복제된 표시용 리스트
    private var displayUrls: List<String> = buildDisplayUrls(realUrls)

    fun submitUrls(urls: List<String>) {
        realUrls = urls
        displayUrls = buildDisplayUrls(urls)
        notifyDataSetChanged()
    }

    fun getRealSize(): Int = realUrls.size
    fun getDisplaySize(): Int = displayUrls.size

    /** displayPos -> realPos 매핑 */
    fun toRealPos(displayPos: Int): Int {
        if (realUrls.isEmpty()) return 0
        return when (displayPos) {
            0 -> realUrls.lastIndex       // 첫 복제(C)
            displayUrls.lastIndex -> 0    // 끝 복제(A)
            else -> displayPos - 1
        }
    }

    private fun buildDisplayUrls(src: List<String>): List<String> {
        if (src.isEmpty()) return emptyList()
        if (src.size == 1) return listOf(src[0]) // 한 장일 때는 복제 불필요
        val first = src.first()
        val last = src.last()
        return listOf(last) + src + listOf(first)
    }

    override fun getItemCount(): Int = displayUrls.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemImageSlideBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = displayUrls[position]
        Glide.with(holder.binding.slideImageIv)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(16))
            .into(holder.binding.slideImageIv)

        holder.binding.slideImageIv.setOnClickListener {
            onClick?.invoke(toRealPos(position))
        }
    }

    class VH(val binding: ItemImageSlideBinding) : RecyclerView.ViewHolder(binding.root)
}
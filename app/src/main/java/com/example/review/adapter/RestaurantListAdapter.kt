import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.review.R
import com.example.review.api.Response.RestaurantResponse

// MainActivity에서 클릭 이벤트를 처리하기 위한 람다
class RestaurantListAdapter(private val onItemClick: (RestaurantResponse) -> Unit) :
    ListAdapter<RestaurantResponse, RestaurantListAdapter.RestaurantViewHolder>(RestaurantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant_list, parent, false)
        return RestaurantViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RestaurantViewHolder(itemView: View, val onItemClick: (RestaurantResponse) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_restaurant_name_item)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tv_restaurant_category_item)
        private val summaryTextView: TextView = itemView.findViewById(R.id.tv_restaurant_summary_item)

        fun bind(restaurant: RestaurantResponse) {
            nameTextView.text = restaurant.store_name
            categoryTextView.text = restaurant.category
            summaryTextView.text = restaurant.review_summary ?: "리뷰 요약 정보가 없습니다."

            // 아이템 뷰 클릭 시 람다 실행
            itemView.setOnClickListener {
                onItemClick(restaurant)
            }
        }
    }
}

// 리스트 변경 시 효율적인 업데이트를 위한 DiffUtil
class RestaurantDiffCallback : DiffUtil.ItemCallback<RestaurantResponse>() {
    override fun areItemsTheSame(oldItem: RestaurantResponse, newItem: RestaurantResponse): Boolean {
        return oldItem.store_id == newItem.store_id
    }

    override fun areContentsTheSame(oldItem: RestaurantResponse, newItem: RestaurantResponse): Boolean {
        return oldItem == newItem
    }
}
package co.ruizhang.trademe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.ruizhang.trademe.databinding.ListingItemBinding
import co.ruizhang.trademe.viewmodels.ListingItemViewData
import com.squareup.picasso.Picasso

interface ListingListClickListener {
    fun onListingItemClicked(id: Long)
}


class ListingViewHolder(
    private val binding: ListingItemBinding,
    private val listener: ListingListClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewdata: ListingItemViewData, picasso: Picasso) {
        binding.title.text = viewdata.name
        binding.price.text = viewdata.price
        binding.card.setOnClickListener {
            listener.onListingItemClicked(viewdata.id)
        }
        picasso.load(viewdata.imageUrl)
            .into(binding.thumbnail)


    }
}

class ListingListAdapter(val listener: ListingListClickListener) :
    ListAdapter<ListingItemViewData, ListingViewHolder>(
        DIFF_CALLBACK
    ) {


    private val picasso = Picasso.get()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListingItemBinding.inflate(inflater, parent, false)
        return ListingViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position), picasso)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListingItemViewData>() {
            override fun areItemsTheSame(
                oldItem: ListingItemViewData,
                newItem: ListingItemViewData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListingItemViewData,
                newItem: ListingItemViewData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}

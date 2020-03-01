package co.ruizhang.trademe.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.ruizhang.trademe.databinding.CategoryListCategoryItemBinding
import co.ruizhang.trademe.viewmodels.CategoryViewData

interface CategoryListClickListener {
    fun onPathNodeClicked(indexNumber: String)
    fun onCategoryClicked(category: CategoryViewData)
}

class CategoryViewHolder(
    private val binding: CategoryListCategoryItemBinding,
    private val listener: CategoryListClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewData: CategoryViewData) {
        binding.title.text = viewData.name
        binding.card.setOnClickListener {
            listener.onCategoryClicked(viewData)
        }
    }
}

class CategoryListAdapter(private val listener: CategoryListClickListener) :
    ListAdapter<CategoryViewData, CategoryViewHolder>(
        DIFF_CALLBACK
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryListCategoryItemBinding.inflate(inflater, parent, false)
        return CategoryViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryViewData>() {
            override fun areItemsTheSame(
                oldItem: CategoryViewData,
                newItem: CategoryViewData
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryViewData,
                newItem: CategoryViewData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package co.ruizhang.trademe.views

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.ruizhang.trademe.R

class ListingListDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        with(outRect) {
            val itemCount = parent.adapter?.itemCount ?: 1
            this.left = view.resources.getDimensionPixelSize(R.dimen.default_space_x0_5)
            this.right = view.resources.getDimensionPixelOffset(R.dimen.default_space_x0_5)
            if (position == itemCount - 1) {
                this.bottom = 0
            } else {
                this.bottom = view.resources.getDimensionPixelSize(R.dimen.default_space_x0_5)
            }
            if (position == 0) {
                this.top = view.resources.getDimensionPixelSize(R.dimen.default_space_x0_5)
            }
        }
    }
}
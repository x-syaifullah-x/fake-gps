package id.xxx.fake.gps.history.domain.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ItemSwipeLR<T : RecyclerView.ViewHolder>(
    blockOnSwipe: (T?) -> Unit
) : ItemTouchHelper(ItemTouchCallback(blockOnSwipe)) {

    private class ItemTouchCallback<T : RecyclerView.ViewHolder>(
        private val blockOnSwipe: (T?) -> Unit
    ) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(0, LEFT or RIGHT)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            @Suppress("UNCHECKED_CAST")
            blockOnSwipe(viewHolder as? T)
        }
    }
}
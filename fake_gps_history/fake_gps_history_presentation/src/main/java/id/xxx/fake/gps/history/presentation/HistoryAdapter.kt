package id.xxx.fake.gps.history.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import id.xxx.fake.gps.history.domain.model.HistoryModel
import id.xxx.fake.gps.history.presentation.databinding.ItemHistoryBinding
import id.xxx.module.domain.adapter.PagingAdapter
import id.xxx.module.domain.adapter.RecyclerViewViewHolder

class HistoryAdapter(
    private val onItemClick: (ItemHistoryBinding, HistoryModel) -> Unit = { _, _ -> }
) : PagingAdapter<HistoryModel, RecyclerViewViewHolder<ItemHistoryBinding>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewViewHolder(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerViewViewHolder<ItemHistoryBinding>, pos: Int) {
        val data = getItem(pos) ?: return
        holder.binding.apply {
            this.data = data
            root.setOnClickListener { onItemClick(this, data) }
            addressHistory.setOnLongClickListener {
                val clipboardManager =
                    it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", (it as AppCompatTextView).text)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(it.context, "copy", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
        }
    }
}
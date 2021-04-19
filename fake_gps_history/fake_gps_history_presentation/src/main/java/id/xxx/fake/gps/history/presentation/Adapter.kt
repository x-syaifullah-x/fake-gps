package id.xxx.fake.gps.history.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import id.xxx.base.domain.adapter.BaseAdapter
import id.xxx.base.domain.adapter.HolderWithBinding
import id.xxx.fake.gps.history.domain.model.HistoryModel
import id.xxx.fake.gps.history.presentation.databinding.ItemHistoryBinding

class Adapter(
    private val onItemClick: (ItemHistoryBinding, HistoryModel) -> Unit = { _, _ -> }
) : BaseAdapter.WithPaging3AndViewHolder<HistoryModel, ItemHistoryBinding>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderWithBinding(
        ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: HolderWithBinding<ItemHistoryBinding>, position: Int) {
        val data = getItem(position) ?: return

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
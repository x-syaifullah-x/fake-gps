package id.xxx.fake.gps.history.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import id.xxx.fake.gps.history.domain.adapter.ItemSwipeLR
import id.xxx.fake.gps.history.presentation.databinding.FragmentHistoryBinding
import id.xxx.fake.gps.history.presentation.databinding.ItemHistoryBinding
import id.xxx.module.domain.adapter.RecyclerViewViewHolder
import id.xxx.module.presentation.binding.delegate.viewBinding
import id.xxx.module.presentation.extension.setResult
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val binding by viewBinding<FragmentHistoryBinding>()

    private var isLoaded = false

    private lateinit var adapterPaging: HistoryAdapter

    private val viewModel: HistoryViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterPaging = HistoryAdapter { _, model ->
            setResult {
                putExtra("latitude", model.latitude)
                putExtra("longitude", model.longitude)
            }
        }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, 1))
            ItemSwipeLR {
                @Suppress("UNCHECKED_CAST")
                val holder = (it as? RecyclerViewViewHolder<ItemHistoryBinding>)
                viewModel.delete(holder?.binding?.data ?: return@ItemSwipeLR)
            }.attachToRecyclerView(this)
            adapter = adapterPaging
        }

        viewModel.data(requireActivity().intent.getStringExtra("USER_ID_DATA_EXTRA"))
            .observe(viewLifecycleOwner) { adapterPaging.submitData(lifecycle, it) }

        adapterPaging.addLoadStateListener { handleLoadStateListener(it) }
    }

    private fun handleLoadStateListener(loadState: CombinedLoadStates) {
        if (loadState.refresh is LoadState.Loading) {
            binding.loadingProgressBar.visibility = View.VISIBLE
            isLoaded = true
        } else {
            if (isLoaded) binding.loadingProgressBar.visibility = View.GONE
            val error = when {
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            error?.let {
                Toast.makeText(context, it.error.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}
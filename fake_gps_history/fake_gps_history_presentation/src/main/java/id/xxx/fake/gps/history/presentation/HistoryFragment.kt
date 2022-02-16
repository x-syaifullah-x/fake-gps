package id.xxx.fake.gps.history.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingDataDiffer
import androidx.paging.map
import androidx.recyclerview.widget.DividerItemDecoration
import id.xxx.fake.gps.history.domain.adapter.ItemSwipeLR
import id.xxx.fake.gps.history.presentation.databinding.FragmentHistoryBinding
import id.xxx.fake.gps.history.presentation.databinding.ItemHistoryBinding
import id.xxx.module.domain.adapter.RecyclerViewViewHolder
import id.xxx.module.view.binding.ktx.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val binding by viewBinding<FragmentHistoryBinding>()

    private val viewModel: HistoryViewModel by viewModel()

    private var isLoaded = false

    private val adapterPaging: HistoryAdapter = HistoryAdapter { _, m ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("fake-gps://home_fragment?latitude=${m.latitude}&longitude=${m.longitude}".toUri())
            .build()
        findNavController().navigate(request)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.also { rv ->
            rv.setHasFixedSize(true)
            rv.addItemDecoration(DividerItemDecoration(context, 1))
            ItemSwipeLR<RecyclerViewViewHolder<ItemHistoryBinding>> {
                viewModel.delete(it?.binding?.data ?: return@ItemSwipeLR)
            }.attachToRecyclerView(rv)
            rv.adapter = adapterPaging
        }

        viewModel.data(requireActivity().intent.getStringExtra("USER_ID_DATA_EXTRA"))
            .observe(viewLifecycleOwner) {
                adapterPaging.submitData(lifecycle, it)
            }
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
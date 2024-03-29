package com.treasurehunt.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentFeedBinding
import com.treasurehunt.ui.feed.adapter.FeedAdapter
import com.treasurehunt.ui.feed.adapter.FeedFooterLoadStateAdapter
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val INIT_LOG_COUNT = 15

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter = FeedAdapter { log -> moveToDetail(log) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSegmentedButton()
        bindLogs()
        showFeed()
        initSwipeRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSwipeRefresh() {
        binding.splLogs.setOnRefreshListener {
            feedAdapter.refresh()
            binding.splLogs.isRefreshing = false
        }
    }

    private fun initAdapter() {
        val stateAdapter = FeedFooterLoadStateAdapter { feedAdapter.retry() }
        val feedAdapter = feedAdapter.withLoadStateFooter(stateAdapter)
        binding.rvLogs.adapter = feedAdapter
        val layoutManager = binding.rvLogs.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == feedAdapter.itemCount - 1 && stateAdapter.itemCount == 1) {
                    if (feedAdapter.itemCount > INIT_LOG_COUNT) 3
                    else 0
                } else {
                    1
                }
            }
        }
    }

    private fun initSegmentedButton() {
        binding.btnFeed.isSelected = true
        binding.btnMap.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }
    }

    private fun moveToDetail(log: LogModel) {
        val curDestination = findNavController().currentDestination?.id
        curDestination?.let {
            if (it == R.id.feedFragment) {
                val action = FeedFragmentDirections.actionFeedFragmentToLogDetailFragment(log)
                findNavController().navigate(action)
            }
        }
    }

    private fun bindLogs() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pagingLogs
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { pagingLogs ->
                        feedAdapter.submitData(pagingLogs)
                }
        }
    }

    private fun showFeed() {
        viewLifecycleOwner.lifecycleScope.launch {
            feedAdapter.loadStateFlow.collect { loadState ->
                val isEmptyListLoaded =
                    loadState.refresh is LoadState.NotLoading && feedAdapter.itemCount == 0
                binding.tvNoTreasure.isVisible = isEmptyListLoaded
                binding.cpiLoading.isVisible = loadState.source.refresh is LoadState.Loading
            }
        }
    }
}
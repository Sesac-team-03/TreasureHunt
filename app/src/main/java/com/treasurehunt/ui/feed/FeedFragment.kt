package com.treasurehunt.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentFeedBinding
import com.treasurehunt.ui.feed.adapter.FeedAdapter
import com.treasurehunt.ui.model.LogModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    private val feedAdapter = FeedAdapter { moveDetail(it) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSegmentedButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        binding.rvLogs.adapter = feedAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect {
                binding.logs = it.logs
            }
        }
    }

    private fun initSegmentedButton() {
        binding.btnFeed.isSelected = true
        binding.btnMap.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_homeFragment)
        }
    }

    private fun moveDetail(log: LogModel) {
        val action = FeedFragmentDirections.actionFeedFragmentToDetailFragment(log, null)
        findNavController().navigate(action)
    }
}
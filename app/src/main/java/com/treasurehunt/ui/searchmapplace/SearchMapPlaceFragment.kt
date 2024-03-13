package com.treasurehunt.ui.searchmapplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.treasurehunt.databinding.FragmentSearchMapPlaceBinding
import com.treasurehunt.ui.searchmapplace.adapter.SearchMapPlaceAdapter
import com.treasurehunt.util.convertMapXYToLatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMapPlaceFragment : Fragment() {

    private var _binding: FragmentSearchMapPlaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchMapPlaceViewModel by viewModels()
    private lateinit var adapter: SearchMapPlaceAdapter
    private val args: SearchMapPlaceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchMapPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        binding.tietSearchMapPlace.setOnEditorActionListener { _, _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                val keyword = binding.tietSearchMapPlace.text.toString()
                val result = viewModel.search(keyword)
                adapter.submitList(result)
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        val clickListener = MapPlaceClickListener { mapPlace ->
            val mapPlacePosition = convertMapXYToLatLng(mapPlace.mapx to mapPlace.mapy)
            val action =
                SearchMapPlaceFragmentDirections.actionSearchMapPlaceFragmentToHomeFragment(
                    mapPlacePosition
                )
            findNavController().navigate(action)
        }
        adapter = SearchMapPlaceAdapter(args.userPosition, clickListener)
        binding.rvSearchResult.adapter = adapter
    }
}
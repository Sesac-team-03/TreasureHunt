package com.treasurehunt.ui.searchmapplace

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSearchMapPlaceBinding
import com.treasurehunt.ui.model.MapPlaceModel
import com.treasurehunt.ui.searchmapplace.adapter.SearchMapPlaceAdapter
import com.treasurehunt.util.convertMapXYToLatLng
import com.treasurehunt.util.getDistance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchMapPlaceFragment : Fragment() {

    private var _binding: FragmentSearchMapPlaceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchMapPlaceViewModel by viewModels()
    private lateinit var adapter: SearchMapPlaceAdapter
    private val args: SearchMapPlaceFragmentArgs by navArgs()
    private var job: Job? = null

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
        setSearchBar()
        setBackButton()
        setCancelButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        adapter = SearchMapPlaceAdapter(args.userPosition, getClickListener())
        binding.rvSearchResult.adapter = adapter
    }

    private fun getClickListener() = MapPlaceClickListener { mapPlace ->
        val mapPlacePosition = convertMapXYToLatLng(mapPlace.mapx to mapPlace.mapy)
        val distance = getDistance(mapPlace.mapx to mapPlace.mapy, args.userPosition)
        val action = SearchMapPlaceFragmentDirections.actionSearchMapPlaceFragmentToHomeFragment(
            mapPlace = mapPlace.copy(
                position = mapPlacePosition,
                distance = distance
            )
        )
        findNavController().navigate(action)
    }

    private fun setSearchBar() {
        binding.tietSearchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fetchResult(s?.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchResult(keyword: String?) {
        job?.cancel()

        job = viewLifecycleOwner.lifecycleScope.launch {
            if (keyword == null) return@launch

            val result = viewModel.search(keyword)
            showResult(result)
        }
    }

    private fun showResult(result: List<MapPlaceModel> = emptyList()) {
        adapter.submitList(result)
    }

    private fun setBackButton() {
        binding.ibBack.setOnClickListener {
            findNavController().navigate(R.id.action_searchMapPlaceFragment_to_homeFragment)
        }
    }

    private fun setCancelButton() {
        binding.ibCancel.setOnClickListener {
            binding.tietSearchBar.text = null
            showResult()
        }
    }
}
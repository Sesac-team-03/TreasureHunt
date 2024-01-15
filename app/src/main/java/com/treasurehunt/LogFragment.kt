package com.treasurehunt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.treasurehunt.databinding.FragmentLogBinding

class LogFragment : Fragment() {
    private var _binding: FragmentLogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LogViewModel by viewModels()
    private val recordAdapter = LogAdapter { imageModel -> viewModel.removeImage(imageModel) }
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data?.clipData != null) {
                val count = result.data?.clipData!!.itemCount
                for (i in 0 until count) {
                    viewModel.addImage(ImageModel(result.data?.clipData!!.getItemAt(i).uri.toString()))
                }
            } else if (result.data?.data != null) {
                viewModel.addImage(ImageModel(result.data?.data.toString()))
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setAddImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        binding.rvPhoto.adapter = recordAdapter
    }

    private fun setAddImage() {
        binding.ibSelectPhoto.setOnClickListener {
            imageLauncher.launch(viewModel.getImage())
        }
    }
}
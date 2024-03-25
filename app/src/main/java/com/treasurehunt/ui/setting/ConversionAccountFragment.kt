package com.treasurehunt.ui.setting

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentConversionAccountBinding
import com.treasurehunt.ui.model.NaverUser
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val NAVER_LOGIN_CLIENT_ID = BuildConfig.NAVER_LOGIN_CLIENT_ID
private const val NAVER_LOGIN_CLIENT_SECRET = BuildConfig.NAVER_LOGIN_CLIENT_SECRET
private const val APP_NAME = BuildConfig.APP_NAME

@AndroidEntryPoint
class ConversionAccountFragment : DialogFragment() {
    private var _binding: FragmentConversionAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverIdLoginSDK.initialize(
            requireContext(), NAVER_LOGIN_CLIENT_ID, NAVER_LOGIN_CLIENT_SECRET, APP_NAME
        )
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentConversionAccountBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
            .apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.requestFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCloseButton()
        setCancelButton()
        setAccountConversion()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCloseButton() {
        binding.ibClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setCancelButton() {
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setAccountConversion() {
        binding.btnAccountConversion.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
            NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
                override fun onSuccess() {
                    fetchNaverUser()
                }

                override fun onFailure(httpStatus: Int, message: String) {}

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            })
        }
    }

    private fun fetchNaverUser() {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val naverProfile = result.profile ?: return kotlin.run {
                    binding.btnAccountConversion.showSnackbar(R.string.login_naver_login_error)
                }
                val naverUser = NaverUser(
                    naverProfile.id!!,
                    naverProfile.email!!,
                    naverProfile.nickname!!,
                    naverProfile.profileImage!!
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    val naverUserEmail = viewModel.checkNaverUser(naverUser.email.toString())
                    if (naverUserEmail.isEmpty()) {
                        binding.btnAccountConversion.showSnackbar(R.string.conversion_account_sb_has_naver_account)
                    } else {
                    }
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {}

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }
}
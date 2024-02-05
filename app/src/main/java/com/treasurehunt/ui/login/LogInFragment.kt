package com.treasurehunt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.data.User
import com.treasurehunt.databinding.FragmentLoginBinding
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.launch

private const val NAVER_LOGIN_CLIENT_ID = BuildConfig.NAVER_LOGIN_CLIENT_ID
private const val NAVER_LOGIN_CLIENT_SECRET = BuildConfig.NAVER_LOGIN_CLIENT_SECRET
private const val APP_NAME = BuildConfig.APP_NAME

class LogInFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverIdLoginSDK.initialize(
            requireContext(),
            NAVER_LOGIN_CLIENT_ID,
            NAVER_LOGIN_CLIENT_SECRET,
            APP_NAME
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNaverLogin()
        setGuestLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setNaverLogin() {
        binding.btnNaverLogin.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
            NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
                override fun onSuccess() {
                    fetchNaverAccount()
                }

                override fun onFailure(httpStatus: Int, message: String) {
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }
    }

    private fun fetchNaverAccount() {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(result: NidProfileResponse) {
                val naverProfile = result.profile!!
                val user = User(
                    naverProfile.email!!,
                    naverProfile.id!!,
                    naverProfile.nickname,
                    naverProfile.profileImage
                )

                loginAccount(user)
            }

            override fun onFailure(httpStatus: Int, message: String) {
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

        })
    }

    private fun createAccount(user: User) {
        Firebase.auth.createUserWithEmailAndPassword(user.email!!, user.id)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        viewModel.insertNaverUser(user)
                        findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                    }
                } else {
                    binding.root.showSnackbar(R.string.login_create_account_error)
                }
            }
    }

    private fun loginAccount(user: User) {
        Firebase.auth.signInWithEmailAndPassword(user.email!!, user.id)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        viewModel.initLocalData()
                        findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                    }
                } else {
                    createAccount(user)
                }
            }
    }

    private fun setGuestLogin() {
        binding.btnGuestLogin.setOnClickListener {
            Firebase.auth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lifecycleScope.launch {
                            viewModel.insertGuestUser()
                            findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                        }
                    } else {
                        binding.root.showSnackbar(R.string.login_guest_login_error)
                    }
                }
        }
    }
}
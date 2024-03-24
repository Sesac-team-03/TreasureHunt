package com.treasurehunt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentLoginBinding
import com.treasurehunt.ui.model.NaverUser
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val NAVER_LOGIN_CLIENT_ID = BuildConfig.NAVER_LOGIN_CLIENT_ID
private const val NAVER_LOGIN_CLIENT_SECRET = BuildConfig.NAVER_LOGIN_CLIENT_SECRET
private const val APP_NAME = BuildConfig.APP_NAME

@AndroidEntryPoint
class LoginFragment : PreloadFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverIdLoginSDK.initialize(
            requireContext(), NAVER_LOGIN_CLIENT_ID, NAVER_LOGIN_CLIENT_SECRET, APP_NAME
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
                val naverProfile = result.profile ?: return kotlin.run {
                    showErrorMessage(LoginError.NAVER_LOGIN_FAIL)
                }
                val naverUser = NaverUser(
                    naverProfile.id!!,
                    naverProfile.email!!,
                    naverProfile.nickname,
                    naverProfile.profileImage
                )

                loginAccount(naverUser)
            }

            override fun onFailure(httpStatus: Int, message: String) {
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }

        })
    }

    private fun loginAccount(naverUser: NaverUser) {
        if (naverUser.email == null) {
            showErrorMessage(LoginError.NAVER_LOGIN_FAIL)
            return
        }

        Firebase.auth.signInWithEmailAndPassword(naverUser.email, naverUser.id)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    launchIfUserExistsElseShowErrorMessage(LoginError.NAVER_LOGIN_FAIL) { currentUser ->
                        viewModel.initLocalData(currentUser.uid)
                        preloadProfileImage(currentUser)
                        findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                    }
                } else {
                    createAccount(naverUser)
                }
            }
    }

    private fun createAccount(naverUser: NaverUser) {
        if (naverUser.email == null) {
            showErrorMessage(LoginError.NAVER_LOGIN_FAIL)
            return
        }

        Firebase.auth.createUserWithEmailAndPassword(naverUser.email, naverUser.id)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    launchIfUserExistsElseShowErrorMessage(LoginError.NAVER_LOGIN_FAIL) { currentUser ->
                        viewModel.insertNaverUser(naverUser, currentUser)
                        preloadProfileImage(currentUser)
                        findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                    }
                } else {
                    showErrorMessage(LoginError.NAVER_LOGIN_FAIL)
                }
            }
    }

    private fun setGuestLogin() {
        binding.btnGuestLogin.setOnClickListener {
            Firebase.auth.signInAnonymously().addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    launchIfUserExistsElseShowErrorMessage(LoginError.GUEST_LOGIN_FAIL) { currentUser ->
                        viewModel.insertGuestUser(currentUser.uid)
                        findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                    }
                } else {
                    showErrorMessage(LoginError.GUEST_LOGIN_FAIL)
                }
            }
        }
    }

    private fun launchIfUserExistsElseShowErrorMessage(
        error: LoginError, block: suspend (currentUser: FirebaseUser) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUser = Firebase.auth.currentUser ?: return@launch kotlin.run {
                showErrorMessage(error)
            }

            block(currentUser)
        }
    }

    private fun showErrorMessage(error: LoginError) {
        val errorMessage = when (error) {
            LoginError.NAVER_LOGIN_FAIL -> {
                R.string.login_naver_login_error
            }

            LoginError.GUEST_LOGIN_FAIL -> {
                R.string.login_guest_login_error
            }
        }
        binding.root.showSnackbar(errorMessage)
    }

    enum class LoginError {
        NAVER_LOGIN_FAIL, GUEST_LOGIN_FAIL
    }
}
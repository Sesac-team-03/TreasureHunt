package com.treasurehunt.ui.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileMap
import com.navercorp.nid.profile.data.NidProfileResponse
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentLoginBinding

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
        guestLogin()
    }

    private fun setNaverLogin() {
        binding.btnNaverLogin.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
            NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
                override fun onSuccess() {
                    createNaverAccount()
                    findNavController().navigate(R.id.action_logInFragment_to_homeFragment)
                }

                override fun onFailure(httpStatus: Int, message: String) {
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }
    }

    private fun createNaverAccount() {
        NidOAuthLogin().callProfileApi(object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                Toast.makeText(context, "$response", Toast.LENGTH_SHORT).show()
                createAccount(response.profile?.email!!, response.profile?.id!!)
            }

            override fun onFailure(httpStatus: Int, message: String) {
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        })
    }

    private fun createAccount(email: String, password: String) {
        viewModel.auth.observe(viewLifecycleOwner) {
            it.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("12345", "createUserWithEmail:success")
                    } else {
                        Log.w("12345", "createUserWithEmail:failure", task.exception)
                    }
                }
        }

    }

    private fun guestLogin() {
        binding.btnGuestLogin.setOnClickListener {
            viewModel.auth.observe(viewLifecycleOwner) {
                it.signInAnonymously()
                    .addOnCompleteListener(Activity()) { task ->
                        if (task.isSuccessful) {
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                requireContext(),
                                "Authentication failed.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
            }
        }
    }
}
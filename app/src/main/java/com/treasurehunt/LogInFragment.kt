package com.treasurehunt

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthBehavior
import com.navercorp.nid.oauth.OAuthLoginCallback
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
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        naverLogin()
        nonMemberLogin()
    }

    private fun naverLogin() {
        binding.btnNaverLogin.setOnClickListener {
            NaverIdLoginSDK.behavior = NidOAuthBehavior.NAVERAPP
            NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
                override fun onSuccess() {}
                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        context,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }

            })
        }
    }

    private fun nonMemberLogin() {
        binding.btnNonMembersLogin.setOnClickListener {
            viewModel.auth.observe(viewLifecycleOwner) {
                it.signInAnonymously()
                    .addOnCompleteListener(Activity()) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Authentication Success.",
                                Toast.LENGTH_SHORT,
                            ).show()
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
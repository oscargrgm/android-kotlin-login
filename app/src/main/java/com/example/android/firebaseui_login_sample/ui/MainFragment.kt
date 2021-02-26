/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.firebaseui_login_sample.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.android.firebaseui_login_sample.R
import com.example.android.firebaseui_login_sample.databinding.FragmentMainBinding
import com.example.android.firebaseui_login_sample.ui.login.AuthenticationState
import com.example.android.firebaseui_login_sample.ui.login.LoginViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainFragment : Fragment() {

    private val signInContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val response = IdpResponse.fromResultIntent(it.data)
        val message = if (it.resultCode == Activity.RESULT_OK) {
            "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}"
        } else {
            "Sign in unsuccessful ${response?.error?.errorCode}"
        }
        Log.d(TAG, message)
    }

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        // TODO Remove the two lines below once observeAuthenticationState is implemented.
//        binding.welcomeText.text = viewModel.getFactToDisplay(requireContext())
//        binding.authButton.text = getString(R.string.login_btn)

        binding.authButton.setOnClickListener { launchSignInFlow() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()

        binding.authButton.setOnClickListener {
            // TODO call launchSignInFlow when authButton is clicked
            launchSignInFlow()
        }
    }

    /**
     * [onActivityResult] is deprecated and has been replaced by the new
     * [ActivityResultContracts.StartActivityForResult] contract.
     *
     * @see [signInContract]
     */
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // TODO Listen to the result of the sign in process by filter for when
//        //  SIGN_IN_REQUEST_CODE is passed back. Start by having log statements to know
//        //  whether the user has signed in successfully
//    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        val factToDisplay = viewModel.getFactToDisplay(requireContext())

        // TODO Use the authenticationState variable from LoginViewModel to update the UI
        //  accordingly.
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    //  TODO If there is a logged-in user, authButton should display Logout. If the
                    //   user is logged in, you can customize the welcome message by utilizing
                    //   getFactWithPersonalization().
                    binding.welcomeText.text = getFactWithPersonalization(factToDisplay)
                    binding.authButton.run {
                        text = getString(R.string.logout_button_text)
                        setOnClickListener { AuthUI.getInstance().signOut(requireContext()) }
                    }
                }
                else -> {
                    // TODO If there is no logged in user, authButton should display Login and
                    //  launch the sign in screen when clicked. There should also be no
                    //  personalization of the message displayed.
                    binding.welcomeText.text = factToDisplay
                    binding.authButton.run {
                        text = getString(R.string.login_button_text)
                        setOnClickListener { launchSignInFlow() }
                    }
                }
            }
        })
    }


    private fun getFactWithPersonalization(fact: String): String {
        return String.format(
            resources.getString(
                R.string.welcome_message_authed,
                FirebaseAuth.getInstance().currentUser?.displayName,
                Character.toLowerCase(fact[0]) + fact.substring(1)
            )
        )
    }

    private fun launchSignInFlow() {
        // TODO Complete this function by allowing users to register and sign in with
        //  either their email address or Google account.
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val singInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInContract.launch(singInIntent)
    }

    companion object {
        private val TAG = MainFragment::class.java.simpleName
    }
}
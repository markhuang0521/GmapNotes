package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.database.DatabaseUtils
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LoginFragment"
        const val SIGN_IN_RESULT_CODE = 101
    }
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
val binding:ActivityAuthenticationBinding =DataBindingUtil.setContentView(this,R.layout.activity_authentication)

        binding.btnLogin.setOnClickListener {
            launchSignInFlow()
        }

        viewModel.authenticationState.observe(this, Observer { state->
            if(state== AuthenticationState.AUTHENTICATED){
                navigateToReminderActivity()
            }

        })

    }
    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )


        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.common_google_signin_btn_icon_dark)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            SIGN_IN_RESULT_CODE-> {
//                val response = IdpResponse.fromResultIntent(data)
                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in user.
                    Log.i(
                        TAG, "Successfully signed in user " + "${FirebaseAuth.getInstance().currentUser?.displayName}!")
                    navigateToReminderActivity()
                }else{
                    Log.i(TAG, "Sign in unsuccessful ")

                }
            }else->{
            Toast.makeText(this, "testing", Toast.LENGTH_SHORT).show()
        }

        }
    }


    private fun navigateToReminderActivity() {
        startActivity(Intent(this, RemindersActivity::class.java))
        finish()
    }
}

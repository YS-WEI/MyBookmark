package com.siang.wei.mybookmark.service

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.OnCompleteListener










class GoogleDriveService {
    companion object {
        const val TAG = "GoogleDriveService"
        const val RC_SIGN_IN = 9001
        var mGoogleSignInClient: GoogleSignInClient? = null
        var account: GoogleSignInAccount? = null
    }

    fun loginGoogle(activity : Activity) {
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        signIn(activity)
    }

    private fun signIn(activity : Activity) {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }

    }
}
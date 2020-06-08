package com.example.demologin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException


class MainActivity : AppCompatActivity(), OnConnectionFailedListener {

    private lateinit var callbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    companion object {
        private const val EMAIL = "email"
        const val TAG: String = ""
        const val RC_SIGN_IN = 1
    }

    override fun onStart() {
        super.onStart()
// Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        callbackManager = CallbackManager.Factory.create();
        updateUI(account)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
// Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create()
        getUserProfile(AccessToken.getCurrentAccessToken())
        btnFbLogin.setPermissions(listOf("email", "public_profile"))
        btnFbLogin.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {

                val loggedIn = AccessToken.getCurrentAccessToken() == null
                Log.e(TAG,"Facebook Login Success $loggedIn")
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException?) {
                Log.e(TAG,"Facebook Login Failed ${error?.message}")
            }
        });

        googleSignIn.setOnClickListener {

            signIn();

        }
    }



    private fun getUserProfile(currentAccessToken: AccessToken?) {
        val request = GraphRequest.newMeRequest(currentAccessToken) { `object`, response ->

            Log.d("TAG", " hii $response")
            try {
               /* val first_name = `object`.getString("first_name")
                val last_name = `object`.getString("last_name")
                val email = `object`.getString("email")
                val id = `object`.getString("id")
                val image_url =
                    "https://graph.facebook.com/$id/picture?type=normal"*/
               /* txtUsername.setText("First Name: $first_name\nLast Name: $last_name")
                txtEmail.setText(email)
                Picasso.with(this@MainActivity).load(image_url).into(imageView)*/
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()


    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        progressBar.visibility=View.VISIBLE
    }

    override fun onConnectionFailed(p0: ConnectionResult) {


    }

/*
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }*/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        progressBar.visibility=View.INVISIBLE
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) { // The Task returned from this call is always completed, no need to attach
// a listener.

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) handleSignInResult(task)
            else Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) { // The ApiException status code indicates the detailed failure reason.
// Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        Log.e(TAG, "Update UI Func=${account?.idToken}")
       if (account!=null){
        startActivity(Intent(this, HomeActivity::class.java))
        finish()}

    }


}

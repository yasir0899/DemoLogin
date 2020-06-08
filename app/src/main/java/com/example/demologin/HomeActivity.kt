package com.example.demologin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_home.*


class HomeActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    companion object {

        const val TAG: String = ""
        const val RC_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
// Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        progressBar.visibility= View.VISIBLE
        updateUi()
        btnSignOut.setOnClickListener {

            signOut();

        }
    }

    private fun updateUi() {
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        progressBar.visibility= View.INVISIBLE
        tvName.text=acct?.displayName
        tvEmail.text=acct?.email
        Glide.with(this)
            .load(acct?.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_launcher_background)
            .apply(RequestOptions.circleCropTransform().override(130,130))
            .skipMemoryCache(true)
            .into(ivProfileImage)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}

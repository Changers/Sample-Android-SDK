package com.blacksquared.sampleandroidsdk

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.blacksquared.sdk.activity.WebActivity
import com.blacksquared.sdk.app.Changers

class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)
        setupListeners()
    }

    private fun setupListeners() {
        findViewById<View>(R.id.launchButton).setOnClickListener {
            launchWebApp()
        }
    }

    private fun launchWebApp() {
        Log.d("LauncherActivity", "Is client authenticated? ${Changers.isClientAuthenticated}")
        Log.d("LauncherActivity", "Is user authenticated? ${Changers.isUserAuthenticated}")

        startActivity(WebActivity.newIntent(this))
    }
}
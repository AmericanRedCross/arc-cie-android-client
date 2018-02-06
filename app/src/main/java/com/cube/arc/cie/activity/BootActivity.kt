package com.cube.arc.cie.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import com.cube.arc.R
import com.cube.arc.onboarding.activity.OnboardingActivity
import com.cube.lib.helper.PermissionHelper

/**
 * Entry point of the app. This class will decide to show the onboarding feature or to progress to the
 * main application. This class will also do a check for content updates.
 */
class BootActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		checkStoragePermission()
	}

	/**
	 * Checks to ensure the storage permission has been granted, it is required for storing exports
	 */
	fun checkStoragePermission()
	{
		if (!PermissionHelper.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
		{
			PermissionHelper.doPermissionCheck(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1, "Storage permission required to use this app")
		}
		else
		{
			showSplash()
		}
	}

	/**
	 * Shows the splash screen, but only once during the install lifespan of the app
	 */
	fun showSplash()
	{
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("seen_splash", false))
		{
			setContentView(R.layout.splash_view)

			with (findViewById(R.id.content))
			{
				animation = AnimationSet(true).apply {
					addAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
				}

				animation.fillAfter = true
				animation.startOffset = 100L
				animation.start()
			}

			Handler().postDelayed(
			{
				PreferenceManager.getDefaultSharedPreferences(this).edit()
					.putBoolean("seen_splash", true)
					.apply()

				startOnboarding()
			}, 2500)
		}
		else
		{
			startOnboarding()
		}
	}

	/**
	 * Starts the onboarding activity if it has not previously been shown, else it will launch the main
	 * app
	 */
	fun startOnboarding()
	{
		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("seen_onboarding", false))
		{
			startActivity(Intent(this, OnboardingActivity::class.java))
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
		}
		else
		{
			startActivity(Intent(this, MainActivity::class.java))
		}

		finish()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)

		if (requestCode == 1)
		{
			checkStoragePermission()
		}
	}
}

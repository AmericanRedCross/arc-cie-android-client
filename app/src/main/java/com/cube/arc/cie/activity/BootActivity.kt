package com.cube.arc.cie.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.cube.arc.onboarding.activity.OnboardingActivity

/**
 * Entry point of the app. This class will decide to show the onboarding feature or to progress to the
 * main application. This class will also do a check for content updates.
 */
class BootActivity : Activity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		startOnboarding()
	}

	fun startOnboarding()
	{
		startActivity(Intent(this, OnboardingActivity::class.java))
		finish()
	}
}

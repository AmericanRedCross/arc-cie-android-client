package com.cube.arc.cie.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.cube.arc.R
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

		if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("seen_splash", false))
		{
			setContentView(R.layout.splash_view)

			with (findViewById(R.id.logo) as ImageView)
			{
				animation = AnimationSet(true).apply {
					addAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
					addAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_top))
				}

				animation.fillAfter = true
				animation.start()
			}

			with (findViewById(R.id.title) as TextView)
			{
				animation = AnimationSet(true).apply {
					addAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
					addAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_top))
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

	fun startOnboarding()
	{
		startActivity(Intent(this, OnboardingActivity::class.java))
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
		finish()
	}
}

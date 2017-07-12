package com.cube.arc.onboarding.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cube.arc.onboarding.fragment.OnboardingFragment

/**
 * Hosts the onboarding fragment that shows the app introduction and link to tutorial video
 */
class OnboardingActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		supportFragmentManager.beginTransaction()
			.replace(android.R.id.content, OnboardingFragment())
			.commit()
	}
}

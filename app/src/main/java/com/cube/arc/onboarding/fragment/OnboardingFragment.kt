package com.cube.arc.onboarding.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cube.arc.R
import com.cube.arc.onboarding.activity.VideoPlayerActivity
import com.cube.lib.util.bind

/**
 * Fragment that hosts the UI component of the onboarding feature
 */
class OnboardingFragment : Fragment()
{
	private val videoButton by bind<Button>(R.id.video)
	private val skipButton by bind<Button>(R.id.skip)

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater?.inflate(R.layout.onboarding_fragment_view, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		videoButton.setOnClickListener {
			startActivity(Intent(activity, VideoPlayerActivity::class.java))
		}

		skipButton.setOnClickListener {

		}
	}
}

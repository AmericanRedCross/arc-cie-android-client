package com.cube.arc.workflow.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cube.arc.R

/**
 * Activity host for adding/editing a note
 */
class NoteActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.note_activity_view)
	}
}

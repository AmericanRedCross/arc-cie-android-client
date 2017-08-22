package com.cube.lib.helper

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity

/**
 * Permission helper class used for checking and requesting permissions
 */
object PermissionHelper
{
	/**
	 * Checks and requests permission authorisation
	 *
	 * @param fragment
	 * @param permission
	 * @param requestCode
	 *
	 * @return true if its ok to continue, false if not
	 */
	fun doPermissionCheck(fragment: Fragment, permission: String, requestCode: Int, dialogMessage: String = ""): Boolean
	{
		if (PermissionChecker.checkSelfPermission(fragment.activity, permission) != PackageManager.PERMISSION_GRANTED)
		{
			if (!fragment.shouldShowRequestPermissionRationale(permission) && dialogMessage.isNotEmpty())
			{
				AlertDialog.Builder(fragment.activity)
					.setMessage(dialogMessage)
					.setPositiveButton("OK") { dialog, which -> fragment.requestPermissions(arrayOf(permission), requestCode) }
					.setCancelable(false)
					.show()

				return false
			}

			fragment.requestPermissions(arrayOf(permission), requestCode)
			return false
		}

		return true
	}

	/**
	 * Checks and requests permission authorisation
	 *
	 * @param context
	 * @param permission
	 * @param requestCode
	 *
	 * @return true if its ok to continue, false if not
	 */
	fun doPermissionCheck(context: AppCompatActivity, permission: String, requestCode: Int, dialogMessage: String = ""): Boolean
	{
		if (PermissionChecker.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
		{
			if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission) && dialogMessage.isNotEmpty())
			{
				AlertDialog.Builder(context)
					.setMessage(dialogMessage)
					.setPositiveButton("OK") { dialog, which -> ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode) }
					.setCancelable(false)
					.show()

				return false
			}

			ActivityCompat.requestPermissions(context, arrayOf(permission), requestCode)
			return false
		}

		return true
	}

	/**
	 * Checks if a permission is granted
	 */
	fun hasPermission(context: Context, permission: String): Boolean
	{
		return PermissionChecker.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
	}
}

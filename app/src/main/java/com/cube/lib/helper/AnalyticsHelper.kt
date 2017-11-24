package com.cube.lib.helper

import android.content.Context
import com.cube.arc.BuildConfig
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.dmsdk.model.FileDescriptor
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker

/**
 * Makes use of the Google Analytics tracker a little more concise and less prone to errors.
 */
object AnalyticsHelper
{
	private lateinit var analytics: GoogleAnalytics
	private lateinit var tracker: Tracker

	fun initialise(ctx: Context)
	{
		analytics = GoogleAnalytics.getInstance(ctx.applicationContext)
		analytics.setLocalDispatchPeriod(30)

		tracker = analytics.newTracker(BuildConfig.GA_CODE)
		tracker.enableAdvertisingIdCollection(true)
		tracker.enableAutoActivityTracking(false)
		tracker.enableExceptionReporting(false)
		tracker.setSessionTimeout(30)

		tracker.setScreenName(null)
	}

	/**
	 * Send event hit as GA event
	 *
	 * @param category The category
	 * @param action The action
	 * @param label The label
	 */
	fun sendEvent(category: String, action: String, label: String? = null)
	{
		tracker.send(HitBuilders.EventBuilder(category, action).setLabel(label).build())
	}

	/**
	 * Send page hit as GA event
	 *
	 * @param pageName The name of the screen to be registered as a hit
	 */
	fun sendPage(pageName: String)
	{
		tracker.setScreenName(pageName)
		tracker.send(HitBuilders.ScreenViewBuilder().build())
	}

	/* app specific tracking methods
	 * Category (Onboarding) */
	fun userWatchTutorialVideo() = sendPage("Tutorial video")
	fun userTapsTutorialVideo() = sendEvent("Pre-Onboarding", "Watch video")
	fun userTapsOnboardingSkip() = sendEvent("Pre-Onboarding", "Skip")

	/* Category (Workflow) */
	fun userTapsToolkit() = sendEvent("Workflow", "Toolkit")
	fun userTapsCriticalTools() = sendEvent("Workflow", "Critical tools")
	fun userViewExportDialog() = sendPage("Export content")
	fun userViewSettings() = sendPage("Settings")
	fun userExpandsDirectory(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Expanded")
	fun userCollapsesDirectory(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Collapsed")
	fun userTapsDirectoryRoadmap(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "View roadmap")
	fun userChecksDirectoryCheckbox(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Checked")
	fun userUnchecksDirectoryCheckbox(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Unchecked")
	fun userTapsAddNote(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Add note")
	fun userTapsEditNote(directory: Directory) = sendEvent("%s %s".format(directory.metadata?.getOrElse("hierarchy", { null }) ?: "", directory.title), "Edit note")
	fun userTapsToolDownload(directory: Directory) = sendEvent(directory.title, "Download")
	fun userMarksToolCritical(directory: Directory) = sendEvent(directory.title, "Marked as critical")
	fun userUnmarksToolCritical(directory: Directory) = sendEvent(directory.title, "Unmarked as critical")
	fun userTapsToolShare(directory: Directory) = sendEvent(directory.title, "Share")
	fun userViewsProgress() = sendPage("Progress")
	fun userViewsWorkflow() = sendPage("Workflow")

	/* Category (Document viewer) */
	fun userTapsExportDocument(document: FileDescriptor) = sendEvent("Document ${document.title}", "Export")

	/* Category (Search) */
	fun userSearches(input: String) = sendEvent("Search", input)
	fun userViewsSearchResults() = sendPage("Search results")

	/* Category (Notes) */
	fun userViewsNoteEditor() = sendPage("Note editor")
	fun userCancelsNoteEditor() = sendEvent("Note editor", "Button", "Cancel")
	fun userCompletesNoteEditor() = sendEvent("Note editor", "Button", "Done")

	/* Category (Settings) */
	fun userViewsSettings() = sendPage("Settings")
	fun userViewsTutorialVideo() = sendPage("Tutorial video")
	fun userTapsResetData() = sendEvent("Settings", "Reset all data")
	fun userTapsChangeLanguage() = sendEvent("Settings", "Change Language")

	/* Category (Export) */
	fun userTapsExportCriticalPath() = sendEvent("Settings", "Export Critical Path Tools")
	fun userTapsExportEntireToolkit() = sendEvent("Settings", "Export Entire Toolkit")
	fun userTapsExportEntireProgress() = sendEvent("Settings", "Export Entire Progress")
	fun userTapsExportCriticalProgress() = sendEvent("Settings", "Export Critical Progress")
}

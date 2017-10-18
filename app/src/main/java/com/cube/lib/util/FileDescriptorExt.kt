package com.cube.lib.util

import android.support.annotation.DrawableRes
import com.cube.arc.R
import com.cube.arc.dmsdk.model.FileDescriptor

/**
 * Returns an icon for given filedescriptor based on [FileDescriptor.mime]
 */
@DrawableRes
fun FileDescriptor.mimeIcon(): Int = when (mime)
{
	"text/plain", // .txt
	"text/richtext", // .rtf
	"application/vnd.oasis.opendocument.text", // .odt
	"application/msword", // .doc
	"application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
		-> R.drawable.ic_mime_doc

	"application/vnd.ms-excel", // .xls
	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
	"application/vnd.oasis.opendocument.spreadsheet" // .ods
		-> R.drawable.ic_mime_xls

	"application/vnd.ms-powerpoint", // .ppt
	"application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
	"application/vnd.oasis.opendocument.presentation" // .opt
		-> R.drawable.ic_mime_ppt

	"application/pdf" -> R.drawable.ic_mime_pdf
	"application/zip" -> R.drawable.ic_mime_zip

	else -> R.drawable.ic_mime_misc
}

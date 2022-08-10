package com.ayvytr.network.ext.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

internal class ContextProvider : ContentProvider() {
    companion object {
        lateinit var globalContext: Context
    }

    override fun onCreate(): Boolean {
        globalContext = context!!.applicationContext
        return false
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun query(p0: Uri, p1: Array<String>?, p2: String?, p3: Array<String>?,
                       p4: String?): Cursor? {
        return null
    }


    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<String>?): Int {
        return -1
    }

    override fun delete(p0: Uri, p1: String?, p2: Array<String>?): Int {
        return -1
    }

    override fun getType(p0: Uri): String? {
        return null
    }
}
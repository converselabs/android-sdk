package com.deepconverse.chatbot

import android.app.Activity
import android.content.Intent
import android.net.Uri
import java.lang.Exception

class DeepConverseSDK (
    var session: DeepConverseSession,
    var callbacks: DeepConverseCallbacks
) {

    init {
        createSession()
    }

    private var botUri: Uri? = null

    private fun createSession() {
        if (session.subdomian.isEmpty() || session.botname.isEmpty()) {
            callbacks.didFailToLoadBot(DeepConverseErrors.SDKInitializationError)
            return
        }
        val uri = createUrl(session.subdomian, session.botname, session.metadata)
        uri?.let {
            botUri = it
        } ?: run {
            callbacks.didFailToLoadBot(DeepConverseErrors.InvalidParameters)
        }
    }

    private fun createUrl(subdomain: String, botName: String, context: HashMap<String, String>): Uri? {
        return try {
            val hostname = "$subdomain-$botName.deepconverse.com"
            val builder = Uri.Builder()
            builder.scheme("https")
                .authority("cdn.converseapps.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendPath("widget")
                .appendPath("embedded-chatbot")
                .appendQueryParameter("hostname",hostname)
            context.forEach {
                builder.appendQueryParameter(it.key, it.value)
            }
            builder.build()
        } catch (e: Exception) {
            null
        }
    }

    fun open(context: Activity) {
        botUri?.let {
            val intent = Intent(context.applicationContext, Deepconverse::class.java)
            intent.putExtra(Deepconverse.URI_PARAM, it.toString())
            context.startActivity(intent)
        } ?: run {
            callbacks.didFailToLoadBot(DeepConverseErrors.InvalidSession)
            return
        }
    }
}
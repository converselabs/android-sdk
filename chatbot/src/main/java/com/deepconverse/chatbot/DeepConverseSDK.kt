package com.deepconverse.chatbot

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

    private fun createUrl(subdomain: String, botname: String, context: HashMap<String, String>): Uri? {
        return try {
            val builder = Uri.Builder()
            builder.scheme("https")
                .authority(subdomain)
                .appendPath(botname)
            context.forEach {
                builder.appendQueryParameter(it.key, it.value)
            }
            builder.build()
        } catch (e: Exception) {
            null
        }
    }

    fun open() {
        botUri?.let {

        } ?: run {
            callbacks.didFailToLoadBot(DeepConverseErrors.InvalidSession)
            return
        }
    }
}
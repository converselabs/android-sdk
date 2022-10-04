package com.deepconverse.chatbot

interface DeepConverseCallbacks {
    fun didCloseBot()
    fun didOpenBot()
    fun didFailToLoadBot(errors: DeepConverseErrors)
    fun didReceiveEvent(event: Map<String, String>)
}




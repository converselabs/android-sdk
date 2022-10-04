package com.deepconverse.chatbot

data class DeepConverseSession(
    var subdomian: String,
    var botname: String,
    var metadata: HashMap<String, String>,
    var webviewTimeout: Double? = 30.0,
)

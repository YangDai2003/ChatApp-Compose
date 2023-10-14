package com.yangdai.chatapp.presentation.chat.chatWidgets

import android.os.Bundle
import android.speech.RecognitionListener
import android.util.Log
import androidx.compose.runtime.MutableState

class MyRecognitionListener : RecognitionListener {

    private lateinit var result: MutableState<String>
    fun setResult(value: MutableState<String>) {
        result = value
    }

    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {
        result.value = ""
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(error: Int) {
        Log.e("Recognition", error.toString())
    }

    override fun onResults(results: Bundle?) {
        val resultArray =
            results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
        result.value = resultArray?.get(0) ?: return
    }

    override fun onPartialResults(partialResults: Bundle?) {
        val resultArray =
            partialResults?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
        result.value = resultArray?.get(0) ?: return
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }
}


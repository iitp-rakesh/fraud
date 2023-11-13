package com.example.fraud

import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

class FraudDetectionClassifier {
    private var interpreter: Interpreter? = null
    private var isModelDownloaded = false

    fun initialise(model: String, callback: () -> Unit) {
        downloadModel(model, callback)
    }

    fun predict(input: ByteBuffer): Float {
        if (isModelDownloaded) {
            val output = ByteBuffer.allocateDirect(1 * 4).order(ByteOrder.nativeOrder())
            interpreter?.run(input, output)
            return output.getFloat(0)
        } else {
            throw IllegalStateException("Model not downloaded yet")
        }
    }

    private fun downloadModel(modelName: String, callback: () -> Unit) {
        val conditions = CustomModelDownloadConditions.Builder().build()
        FirebaseModelDownloader.getInstance()
            .getModel(
                modelName, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND,
                conditions
            )
            .addOnSuccessListener { model: CustomModel? ->
                Log.d("ML", "onViewCreated: Download Complete ${model?.file}")
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    isModelDownloaded = true
                    callback()
                }
            }
    }

}

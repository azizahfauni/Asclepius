package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.asclepius.data.AppDatabase
import com.dicoding.asclepius.data.PredictionHistory
import com.dicoding.asclepius.databinding.ActivityResultBinding
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val resultPrediction = intent.getStringExtra(EXTRA_RESULT)

        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }

        resultPrediction?.let {
            binding.resultText.text = it
        }

        binding.saveButton.setOnClickListener {
            if (imageUri != null && resultPrediction != null) {
                savePrediction(imageUri, resultPrediction, 0.85f) // Contoh confidence score
            }
        }
    }

    private fun savePrediction(imageUri: String, result: String, confidence: Float) {
        val prediction = PredictionHistory(imageUri = imageUri, result = result, confidenceScore = confidence)
        val database = AppDatabase.getDatabase(applicationContext)
        lifecycleScope.launch {
            database.predictionHistoryDao().insert(prediction)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}

package com.dicoding.asclepius.ui.analyze

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.asclepius.databinding.FragmentAnalyzeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class AnalyzeFragment : Fragment() {
    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val viewModel: AnalyzeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            viewModel.currentImageUri.value?.let { analyzeImage(it) }
        }

        viewModel.currentImageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun startGallery() {
        gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val gallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            startCrop(uri)
        } else {
            showToast("Gambar tidak dipilih.")
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setHideBottomControls(false)
            setFreeStyleCropEnabled(true)
        }
        val uCropIntent = UCrop.of(uri, destinationUri)
            .withOptions(options)
            .withMaxResultSize(224, 224)
            .getIntent(requireContext())

        cropImageLauncher.launch(uCropIntent)
    }

    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let { uri ->
                viewModel.currentImageUri.value = uri
                showImage()
            }
        }
    }

    private fun showImage() {
        viewModel.currentImageUri.value.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage(selectedImage: Uri) {
        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?) {
                    activity?.runOnUiThread {
                        results?.let {
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                val resultPrediction = results.joinToString("\n") {
                                    "${it.categories[0].label} : ${(it.categories[0].score * 100).toInt()}%"
                                }
                                moveToResult(selectedImage, resultPrediction)
                            } else {
                                showToast()
                            }
                        }
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(selectedImage)
    }

    private fun moveToResult(selectedImage: Uri, resultPrediction: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, selectedImage.toString())
        intent.putExtra(ResultActivity.EXTRA_RESULT, resultPrediction)
        startActivity(intent)
    }

    private fun showToast(message: String = "result not found") {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

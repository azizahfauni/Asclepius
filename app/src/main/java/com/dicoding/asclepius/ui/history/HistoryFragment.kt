package com.dicoding.asclepius.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.AppDatabase
import com.dicoding.asclepius.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val predictionDao = database.predictionHistoryDao()

        predictionDao.getAllPredictions().observe(viewLifecycleOwner) { predictions ->
            val adapter = HistoryAdapter(predictions) { prediction ->
                // Aksi hapus item
                lifecycleScope.launch {
                    predictionDao.deletePredictionById(prediction.id)
                }
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

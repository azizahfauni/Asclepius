package com.dicoding.asclepius.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.NewsRepository
import com.dicoding.asclepius.databinding.FragmentNewsBinding
import com.dicoding.asclepius.api.NewsService

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NewsViewModel
    private val apiKey = "b9363098a250411394dbf2ae9b53c66d"  // Ganti dengan API key Anda

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repository = NewsRepository(NewsService.create())
        viewModel = ViewModelProvider(this, NewsViewModelFactory(repository)).get(NewsViewModel::class.java)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Start loading news
        viewModel.fetchCancerNews(apiKey)

        // Observe articles and loading status
        viewModel.newsArticles.observe(viewLifecycleOwner) { articles ->
            binding.recyclerView.adapter = NewsAdapter(articles)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

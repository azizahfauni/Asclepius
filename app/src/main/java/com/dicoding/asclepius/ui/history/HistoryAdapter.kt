package com.dicoding.asclepius.ui.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.PredictionHistory
import com.dicoding.asclepius.databinding.ItemHistoryBinding

class HistoryAdapter(private val predictions: List<PredictionHistory>, private val onDeleteClicked : (PredictionHistory) -> Unit) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val prediction = predictions[position]
        holder.bind(prediction)
        holder.binding.buttonDelete.setOnClickListener {
            onDeleteClicked(prediction)
        }
    }

    override fun getItemCount() = predictions.size

    class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prediction: PredictionHistory) {
            binding.imageView.setImageURI(Uri.parse(prediction.imageUri))
            binding.textResult.text = prediction.result

        }
    }
}

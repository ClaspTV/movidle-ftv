package tv.vizbee.movidletv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemScoresRecyclerViewBinding

class ScoresRecyclerAdapter(val scores: ArrayList<String> = arrayListOf()) :
    RecyclerView.Adapter<ScoresRecyclerAdapter.ScoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoresViewHolder {
        val binding =
            ItemScoresRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoresViewHolder, position: Int) {
        holder.bind(scores[position], position)
    }

    override fun getItemCount(): Int = scores.size

    inner class ScoresViewHolder(private val binding: ItemScoresRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: String, position: Int) {
            binding.itemScoreRankTextView.text = "${position + 1}."
            binding.itemScorePlayerNameTextView.text = "Player ${position}"
            binding.itemScoreScoreTextView.text = "$score"
        }
    }
}

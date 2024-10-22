package tv.vizbee.movidletv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemScoresRecyclerViewBinding
import tv.vizbee.movidletv.vizbee.PlayerManager

class ScoresRecyclerAdapter(val players: ArrayList<PlayerManager.Player> = arrayListOf()) :
    RecyclerView.Adapter<ScoresRecyclerAdapter.ScoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoresViewHolder {
        val binding =
            ItemScoresRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoresViewHolder, position: Int) {
        holder.bind(players[position].score, position, players[position].userName)
    }

    override fun getItemCount(): Int = players.size

    inner class ScoresViewHolder(private val binding: ItemScoresRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(score: String, position: Int, username: String) {
            binding.itemScoreRankTextView.text = "${position + 1}."
            binding.itemScorePlayerNameTextView.text = "${username}"
            binding.itemScoreScoreTextView.text = "$score"
        }
    }
}

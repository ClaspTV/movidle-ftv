package tv.vizbee.movidletv.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemScoresRecyclerViewBinding
import tv.vizbee.movidletv.data.model.Player

class ScoresRecyclerAdapter(val players: ArrayList<Player> = arrayListOf()) :
    RecyclerView.Adapter<ScoresRecyclerAdapter.ScoresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoresViewHolder {
        val binding =
            ItemScoresRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoresViewHolder, position: Int) {
        holder.bind(players[position].score, position, players[position].userName)

        val context = holder.itemView.context
        val itemCount = itemCount

        // Set rounded corner background for the first and last item
        val roundCorner = 10 * context.resources.displayMetrics.density
        val background = if (itemCount == 1) {
            GradientDrawable().apply {
                cornerRadii = floatArrayOf(
                    roundCorner, roundCorner, roundCorner, roundCorner,
                    roundCorner, roundCorner, roundCorner, roundCorner
                ) // Total rounded
                setColor(Color.parseColor("#80FFFFFF"))
            }
        } else {
            when (position) {
                0 -> GradientDrawable().apply {
                    cornerRadii = floatArrayOf(roundCorner, roundCorner, roundCorner, roundCorner, 0f, 0f, 0f, 0f) // Top rounded
                    setColor(Color.parseColor("#80FFFFFF"))
                }

                itemCount - 1 -> GradientDrawable().apply {
                    cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, roundCorner, roundCorner, roundCorner, roundCorner) // Bottom rounded
                    setColor(Color.parseColor("#80FFFFFF"))
                }

                else -> GradientDrawable().apply {
                    setColor(Color.parseColor("#80FFFFFF")) // No rounded corners
                }
            }
        }

        holder.itemView.background = background
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

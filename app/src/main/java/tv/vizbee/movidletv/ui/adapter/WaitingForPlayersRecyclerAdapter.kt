package tv.vizbee.movidletv.ui.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.data.model.Player
import tv.vizbee.movidletv.databinding.ItemWaitingForPlayersRecyclerViewBinding

class WaitingForPlayersRecyclerAdapter(private val players: ArrayList<Player> = arrayListOf()) :
    RecyclerView.Adapter<WaitingForPlayersRecyclerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            ItemWaitingForPlayersRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind("${players[position].userName}")

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

    fun addPlayer(player: Player?) {
        Log.i("WaitingForPlayersRecyclerAdapter", "addPlayer invoked. player = $player")
        player?.let {
            players.find { it.userId == player.userId }?.let {
                Log.i("WaitingForPlayersRecyclerAdapter", "Player already exists")
                // Do Nothing
            } ?: kotlin.run {
                Log.i("WaitingForPlayersRecyclerAdapter", "Adding new player")
                players.add(it)
                notifyItemInserted(players.size - 1)
            }
        }
    }

    fun remove(deviceId: String?) {
        players.find { it.userId == deviceId }?.let {
            players.remove(it)
            notifyDataSetChanged()
        }
    }

    fun getAll(): ArrayList<Player> {
        return players
    }

    fun updateAll(players: List<Player>?) {
        players?.let {
            this.players.clear()
            this.players.addAll(players)
            notifyDataSetChanged()
        }
    }

    inner class PlayerViewHolder(private val binding: ItemWaitingForPlayersRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playerName: String) {
            binding.playerNameTextView.text = playerName
        }
    }
}

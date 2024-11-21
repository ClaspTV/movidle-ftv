package tv.vizbee.movidletv.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemWaitingForPlayersRecyclerViewBinding
import tv.vizbee.movidletv.data.model.Player

class WaitingForPlayersRecyclerAdapter(private val players: ArrayList<Player> = arrayListOf()) :
    RecyclerView.Adapter<WaitingForPlayersRecyclerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            ItemWaitingForPlayersRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind("${players[position].userName}")
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
            binding.root.text = playerName
        }
    }
}

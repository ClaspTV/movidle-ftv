package tv.vizbee.movidletv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemWaitingForPlayersRecyclerViewBinding
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

class WaitingForPlayersRecyclerAdapter(private val players: ArrayList<VizbeeDevice> = arrayListOf()) :
    RecyclerView.Adapter<WaitingForPlayersRecyclerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            ItemWaitingForPlayersRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind("${players[position].deviceId} ${position + 1}")
    }

    override fun getItemCount(): Int = players.size

    fun addPlayer(player: VizbeeDevice?) {
        player?.let {
            players.add(it)
            notifyItemInserted(players.size - 1)
        }
    }

    fun remove(device: VizbeeDevice?) {
        players.remove(device)
        notifyDataSetChanged()
    }

    inner class PlayerViewHolder(private val binding: ItemWaitingForPlayersRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playerName: String) {
            binding.root.text = playerName
        }
    }
}

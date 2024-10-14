package tv.vizbee.movidletv.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tv.vizbee.movidletv.databinding.ItemWaitingForPlayersRecyclerViewBinding

class WaitingForPlayersRecyclerAdapter(val playerNames: ArrayList<String> = arrayListOf()) :
    RecyclerView.Adapter<WaitingForPlayersRecyclerAdapter.PlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val binding =
            ItemWaitingForPlayersRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(playerNames[position] + " ${position + 1}")
    }

    override fun getItemCount(): Int = playerNames.size

    fun addPlayer(playerName: String) {
        playerNames.add(playerName)
        notifyItemInserted(playerNames.size - 1)
    }

    inner class PlayerViewHolder(private val binding: ItemWaitingForPlayersRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(playerName: String) {
            binding.root.text = playerName
        }
    }
}

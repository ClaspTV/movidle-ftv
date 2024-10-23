package tv.vizbee.movidletv.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.adapter.WaitingForPlayersRecyclerAdapter
import tv.vizbee.movidletv.databinding.ActivityWaitingForPlayersBinding
import tv.vizbee.movidletv.vizbee.PlayerManager
import tv.vizbee.movidletv.vizbee.VizbeeXMessageType
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

class WaitingForPlayersActivity : BaseActivity() {

    private lateinit var binding: ActivityWaitingForPlayersBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWaitingForPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.root.setOnClickListener {
            if (getAdapterCount() == 5) {
                startTheGame()
            } else {
                addPlayer(
                    PlayerManager.Player(
                        userId = getAdapter()?.itemCount.toString(),
                        userName = "${System.currentTimeMillis()}"
                    )
                )
            }
        }

        setupRecyclerView()
    }

    private fun startTheGame() {
        Intent(this, GameStatusActivity::class.java).apply {
            putExtra("contentPosition", 0)
        }.also {
            startActivity(it)
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.waitingForPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@WaitingForPlayersActivity)
            adapter = WaitingForPlayersRecyclerAdapter()
        }

        PlayerManager.players.values.forEach {
            addPlayer(it)
        }
    }

    private fun addPlayer(player: PlayerManager.Player) {
        (binding.waitingForPlayersRecyclerView.adapter as? WaitingForPlayersRecyclerAdapter)?.addPlayer(player)
    }

    private fun getAdapter(): WaitingForPlayersRecyclerAdapter? {
        return (binding.waitingForPlayersRecyclerView.adapter as? WaitingForPlayersRecyclerAdapter)
    }

    private fun getAdapterCount(): Int {
        return binding.waitingForPlayersRecyclerView.adapter?.itemCount ?: 0
    }

    override fun onStartActivityAction(messageType: String, payload: JSONObject) {
        super.onStartActivityAction(messageType, payload)

        if (messageType == VizbeeXMessageType.START_GAME.value) {
            startTheGame()
        }
    }

    override fun onDeviceChangeAction(device: VizbeeDevice?) {
        super.onDeviceChangeAction(device)

        Log.i("WaitingForPlayersActivity", "Device change event: Current Players ${PlayerManager.players.values}")
        PlayerManager.players.values.find { it.userId == device?.deviceId }?.let { player ->
            getAdapter()?.addPlayer(player)
        } ?: kotlin.run {
            getAdapter()?.remove(device?.deviceId)
        }
    }
}

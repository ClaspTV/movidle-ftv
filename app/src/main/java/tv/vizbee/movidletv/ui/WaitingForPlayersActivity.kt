package tv.vizbee.movidletv.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.adapter.WaitingForPlayersRecyclerAdapter
import tv.vizbee.movidletv.databinding.ActivityWaitingForPlayersBinding

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
                Intent(this, GameStatusActivity::class.java).apply {
                    putExtra("contentPosition", 0)
                }.also {
                    startActivity(it)
                    finish()
                }
            } else {
                addPlayer()
            }
        }

//        binding.startGameTextView.setOnClickListener {
//            navigate(this, StartGameCountDownActivity::class.java)
//        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.waitingForPlayersRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@WaitingForPlayersActivity)
            adapter = WaitingForPlayersRecyclerAdapter()
        }
        addPlayer()
    }

    private fun addPlayer() {
        (binding.waitingForPlayersRecyclerView.adapter as? WaitingForPlayersRecyclerAdapter)?.addPlayer("Player")
    }

    private fun getAdapterCount(): Int {
        return binding.waitingForPlayersRecyclerView.adapter?.itemCount ?: 0
    }
}

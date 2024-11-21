package tv.vizbee.movidletv.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import org.json.JSONObject
import tv.vizbee.movidletv.R
import tv.vizbee.movidletv.databinding.ActivityMainBinding
import tv.vizbee.movidletv.ui.fragment.BaseFragment
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_host_fragment)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getCurrentFragment(): BaseFragment<*>? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull() as? BaseFragment<*>
    }

    override fun onDeviceChangeAction(device: VizbeeDevice?) {
        getCurrentFragment()?.onDeviceChangeAction(device)
    }

    override fun onStartActivityAction(messageType: String, payload: JSONObject) {
        getCurrentFragment()?.onStartActivityAction(messageType, payload)
    }

    override fun onScoreUpdate(payload: JSONObject) {
        getCurrentFragment()?.onScoreUpdate(payload)
    }
}
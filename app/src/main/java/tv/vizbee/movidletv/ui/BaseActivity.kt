package tv.vizbee.movidletv.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected fun navigate(context: Context, activityTo: Class<*>, shouldFinish: Boolean = true) {
        val intent = Intent(context, activityTo)
        startActivity(intent)
        if (shouldFinish) {
            finish()
        }
    }
}
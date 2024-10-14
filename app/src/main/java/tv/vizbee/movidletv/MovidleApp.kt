package tv.vizbee.movidletv

import android.app.Application
import tv.vizbee.movidletv.vizbee.VizbeeWrapper

class MovidleApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize VizbeeWrapper
        VizbeeWrapper.initialize(this)
    }
}

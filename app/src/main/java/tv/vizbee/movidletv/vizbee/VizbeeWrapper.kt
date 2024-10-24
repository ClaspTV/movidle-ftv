package tv.vizbee.movidletv.vizbee

import android.app.Application
import android.content.Context
import android.util.Log
import tv.vizbee.screen.api.Vizbee
import tv.vizbee.screen.api.adapter.VizbeeAppAdapter


/**
 * #VizbeeGuide Do not modify this file.
 *
 * This class is an entry point to the Vizbee integration. It has a utility method to initialise the
 * Vizbee SDKs: the Continuity SDK and the HomeSSO SDK. It has methods to know if Vizbee is enabled
 * by the app and some easy to access adapter objects via extension methods.
 */
object VizbeeWrapper {
    private const val LOG_TAG = "VizbeeWrapper"
    private var isVizbeeEnabled: Boolean = false

    // region class methods
    //------
    // Initialisation
    //------
    /**
     * Initializes Vizbee SDK with the app ID assigned for your app.
     * @param app Application
     */
    fun initialize(app: Application) {

        isVizbeeEnabled = isVizbeeEnabled(app)
        if (!isVizbeeEnabled) {
            Log.i(
                "VizbeeWrapper",
                "Vizbee is not enabled. Not initialising. Define your_app_vizbee_app_id for app to enable Vizbee."
            )
            return
        }

        // Initialise Vizbee Continuity SDK
        Vizbee.getInstance().enableVerboseLogging()
        Vizbee.getInstance().initialize(app, getVizbeeAppId(app), VizbeeAppAdapter())

        VizbeeXWrapper.connectVizbeeXBiCast()
    }

    //------
    // Helpers
    //------

    /**
     * This method returns true if Vizbee is enabled by the app. Returning false will disable all the Vizbee integration.
     *
     * @return true if Vizbee should be enabled in the app integration.
     */
    private fun isVizbeeEnabled(context: Context): Boolean {
        val vizbeeAppId = getVizbeeAppId(context)
        return vizbeeAppId.isNotEmpty()
    }

    /**
     * @return returns the vizbee_app_id defined the resources.
     */
    private fun getVizbeeAppId(context: Context): String {
        val identifier = context.resources.getIdentifier(
            "vizbee_app_id",
            "string",
            context.packageName
        )
        if (identifier == 0) {
            return ""
        }
        val appId = context.getString(identifier)
        Log.i(LOG_TAG, appId)
        return appId
    }

    fun clearVizbeeX() {
        PlayerManager.clear()
        VizbeeXWrapper.disconnect()
    }
    // endregion
}
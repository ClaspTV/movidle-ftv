package tv.vizbee.movidletv.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import org.json.JSONObject
import tv.vizbee.movidletv.ui.viewmodel.AppViewModel
import tv.vizbee.movidletv.ui.viewmodel.PlayerViewModel
import tv.vizbee.movidletv.ui.viewmodel.ScoreViewModel
import tv.vizbee.screen.api.session.model.device.VizbeeDevice

abstract class BaseFragment<B : ViewBinding> : Fragment() {
    // Doing the following two steps to make the _binding nullable. In OnDestroyView, we set it to null
    private var _binding: B? = null
    protected val binding get() = _binding!!

    protected val appViewModel: AppViewModel by activityViewModels()
    protected val playerViewModel: PlayerViewModel by activityViewModels()
    protected val scoreViewModel: ScoreViewModel by activityViewModels()

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LOG_TAG, "onCreateView: ${this::class.java.simpleName}")
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Setting the _binding to null to avoid memory leaks
        _binding = null
    }

    protected fun navigate(navDirections: NavDirections, shouldFinish: Boolean = true) {
        findNavController().navigate(navDirections)

        if (shouldFinish) {
            findNavController().popBackStack()
        }
    }

    // Actions
    open fun onDeviceChangeAction(device: VizbeeDevice?) {
        // Child class will implement this if needed
    }

    open fun onStartActivityAction(messageType: String, payload: JSONObject) {
        // Child class will implement this if needed
    }

    open fun onResetUIAction() {
        // Child class will implement this if needed
    }

    open fun onScoreUpdate(payload: JSONObject) {
        // Child class will implement this if needed
    }

    companion object {
        private const val LOG_TAG = "BaseFragment"
    }
}
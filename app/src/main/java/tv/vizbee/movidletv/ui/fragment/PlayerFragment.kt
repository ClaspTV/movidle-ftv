package tv.vizbee.movidletv.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import tv.vizbee.movidletv.databinding.FragmentPlayerBinding

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }
}
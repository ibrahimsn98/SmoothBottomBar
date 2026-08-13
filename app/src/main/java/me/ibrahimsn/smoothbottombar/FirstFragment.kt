package me.ibrahimsn.smoothbottombar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView

/**
 * A simple [Fragment] subclass.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView=view.findViewById<TextView>(R.id.textView)
        textView.setOnClickListener {
            (requireActivity() as MainActivity).setSelectedItem(2)
            (requireActivity() as MainActivity).removeBadge(2)
        }

        // Posted because the hosting MainActivity's view binding is still being
        // assigned while this fragment's view is created (it's nested inside the
        // same inflate() call), so it isn't safe to touch yet.
        view.post {
            (requireActivity() as MainActivity).setBadge(2)
            (requireActivity() as MainActivity).setBadge(0)
        }
    }

}

package com.example.butterflydetector.ui.transectwalks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.butterflydetector.databinding.FragmentTransectwalksBinding

class TransectwalksFragment : Fragment() {

    private var _binding: FragmentTransectwalksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transectwalksViewModel =
            ViewModelProvider(this)[TransectwalksViewModel::class.java]

        _binding = FragmentTransectwalksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTransectwalks
        transectwalksViewModel.text.observe(viewLifecycleOwner) { text ->
            textView.text = text
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

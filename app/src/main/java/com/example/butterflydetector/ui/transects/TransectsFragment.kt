package com.example.butterflydetector.ui.transects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.butterflydetector.databinding.FragmentTransectsBinding

class TransectsFragment : Fragment() {

    private var _binding: FragmentTransectsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val transectsViewModel =
            ViewModelProvider(this).get(TransectsViewModel::class.java)

        _binding = FragmentTransectsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTransects
        transectsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

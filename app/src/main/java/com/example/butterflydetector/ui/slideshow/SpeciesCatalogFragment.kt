package com.example.butterflydetector.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.butterflydetector.databinding.FragmentSpeciescatalogBinding

class SpeciesCatalogFragment : Fragment() {

    private var _binding: FragmentSpeciescatalogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val speciesCatalogViewModel =
            ViewModelProvider(this).get(SpeciesCatalogViewModel::class.java)

        _binding = FragmentSpeciescatalogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        speciesCatalogViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
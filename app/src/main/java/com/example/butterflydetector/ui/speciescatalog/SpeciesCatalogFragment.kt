package com.example.butterflydetector.ui.speciescatalog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.butterflydetector.databinding.FragmentSpeciescatalogBinding
import com.example.butterflydetector.model.Butterfly

class SpeciesCatalogFragment : Fragment() {

    private var _binding: FragmentSpeciescatalogBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SpeciesCatalogViewModel
    private lateinit var adapter: ButterflyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SpeciesCatalogViewModel::class.java]
        _binding = FragmentSpeciescatalogBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSpeciesFilter()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ButterflyAdapter(emptyList()) { butterfly ->
            showButterflyInfo(butterfly)
        }

        binding.butterflyRecyclerView.layoutManager = GridLayoutManager(context, 4)
        binding.butterflyRecyclerView.adapter = adapter
    }

    private fun setupSpeciesFilter() {
        val speciesAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            viewModel.getSpeciesList()
        )

        binding.speciesFilterDropdown.setAdapter(speciesAdapter)
        binding.speciesFilterDropdown.setText("All Species", false)

        binding.speciesFilterDropdown.setOnItemClickListener { _, _, position, _ ->
            val selectedSpecies = viewModel.getSpeciesList()[position]
            viewModel.filterBySpecies(selectedSpecies)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredButterflies.observe(viewLifecycleOwner) { butterflies ->
            adapter.updateButterflies(butterflies)
        }
    }

    private fun showButterflyInfo(butterfly: Butterfly) {
        AlertDialog.Builder(requireContext())
            .setTitle(butterfly.name)
            .setMessage("""
                Species: ${butterfly.species}
                
                Description: ${butterfly.description}
                
                Habitat: ${butterfly.habitat}
                
                Wingspan: ${butterfly.wingspan}
                
                Flight Period: ${butterfly.flightPeriod}
            """.trimIndent())
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.butterflydetector.ui.speciescatalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.butterflydetector.model.Butterfly
import com.example.butterflydetector.data.ButterflyRepository

class SpeciesCatalogViewModel : ViewModel() {

    private val _butterflies = MutableLiveData<List<Butterfly>>()
    val butterflies: LiveData<List<Butterfly>> = _butterflies

    private val _filteredButterflies = MutableLiveData<List<Butterfly>>()
    val filteredButterflies: LiveData<List<Butterfly>> = _filteredButterflies

    private val _selectedSpecies = MutableLiveData<String>()
    val selectedSpecies: LiveData<String> = _selectedSpecies

    private val allButterflies = ButterflyRepository.getAllButterflies()

    init {
        _butterflies.value = allButterflies
        _filteredButterflies.value = allButterflies
        _selectedSpecies.value = "All Species"
    }

    fun filterBySpecies(species: String) {
        _selectedSpecies.value = species
        _filteredButterflies.value = if (species == "All Species") {
            allButterflies
        } else {
            allButterflies.filter { it.species == species }
        }
    }

    fun getSpeciesList(): List<String> {
        return ButterflyRepository.getSpeciesList()
    }
}

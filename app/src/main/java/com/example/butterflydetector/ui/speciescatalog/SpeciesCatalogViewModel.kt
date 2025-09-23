package com.example.butterflydetector.ui.speciescatalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.butterflydetector.model.Butterfly
import com.example.butterflydetector.data.ButterflyRepository

class SpeciesCatalogViewModel : ViewModel() {

    private val allButterflies: List<Butterfly> = ButterflyRepository.getAllButterflies()

    private val _filteredButterflies = MutableLiveData<List<Butterfly>>(allButterflies)
    val filteredButterflies: LiveData<List<Butterfly>> = _filteredButterflies

    fun getSpeciesList(): List<String> {
        // nur die Arten ohne all species, da ohne filter automatisch alle angezeigt werden
        return allButterflies.map { it.species }
            .distinct()
            .sorted()
    }

    fun filterButterflies(selectedSpecies: List<String>, onlyFavorites: Boolean) {
        _filteredButterflies.value = allButterflies.filter { butterfly ->
            val speciesMatch = selectedSpecies.isEmpty() || butterfly.species in selectedSpecies
            val favoriteMatch = !onlyFavorites || butterfly.isFavorite
            speciesMatch && favoriteMatch
        }
    }


    fun filterByMultipleSpecies(species: List<String>) {
        _filteredButterflies.value = if (species.isEmpty()) {
            allButterflies
        } else {
            allButterflies.filter { it.species in species }
        }
    }
}

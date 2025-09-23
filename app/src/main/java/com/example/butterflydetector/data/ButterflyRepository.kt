package com.example.butterflydetector.data

import com.example.butterflydetector.R
import com.example.butterflydetector.model.Butterfly

object ButterflyRepository {
    fun getAllButterflies(): List<Butterfly> {
        return listOf(
            Butterfly(1, "Monarch Butterfly", "Species 1", R.drawable.butterfly_monarch,
                "The Monarch butterfly is known for its incredible migration journey.",
                "Gardens, fields, and meadows", "8.9-10.2 cm", "March to October"),
            Butterfly(2, "Swallowtail", "Species 1", R.drawable.butterfly_swallowtail,
                "Large, colorful butterflies with distinctive tail-like extensions.",
                "Gardens and woodlands", "7.4-14 cm", "April to September"),
            Butterfly(3, "Blue Morpho", "Species 2", R.drawable.butterfly_bluemorpho,
                "Brilliant blue wings that shimmer in the sunlight.",
                "Tropical rainforests", "12.7-20.3 cm", "Year-round", isPhotographed = true),
            Butterfly(4, "Painted Lady", "Species 2", R.drawable.butterfly_paintedlady,
                "One of the most widespread butterflies in the world.",
                "Gardens, fields, and roadsides", "5.1-7.3 cm", "March to October"),
            Butterfly(5, "Red Admiral", "Species 3", R.drawable.butterfly_redadmiral,
                "Dark wings with red bands and white spots.",
                "Gardens, parks, and woodland edges", "4.5-6.0 cm", "March to October", isPhotographed = true),
            Butterfly(6, "Cabbage White", "Species 3", R.drawable.butterfly_cabbagewhite,
                "Small white butterfly commonly found in gardens.",
                "Gardens and agricultural areas", "3.2-5.1 cm", "March to November"),
            Butterfly(7, "Tiger Swallowtail", "Species 4", R.drawable.butterfly_tigerswallowtail,
                "Large yellow butterfly with black tiger stripes.",
                "Deciduous forests and gardens", "7.9-14 cm", "March to November"),
            Butterfly(8, "Zebra Longwing", "Species 4", R.drawable.butterfly_zebralongwing,
                "Black wings with yellow stripes, state butterfly of Florida.",
                "Tropical and subtropical areas", "7.2-10.0 cm", "Year-round"),
            Butterfly(9, "Gulf Fritillary", "Species 5", android.R.drawable.ic_menu_gallery,
                "Orange butterfly with black markings and silver spots.",
                "Open areas and gardens", "6.5-9.5 cm", "Year-round in warm climates"),
            Butterfly(10, "Mourning Cloak", "Species 5", android.R.drawable.ic_menu_gallery,
                "Dark wings with yellow borders, one of the longest-lived butterflies.",
                "Woodlands and parks", "6.2-7.5 cm", "March to October"),
            Butterfly(11, "Question Mark", "Species 6", android.R.drawable.ic_menu_gallery,
                "Orange and black butterfly with a silver question mark on underwing.",
                "Woodlands and gardens", "4.5-7.0 cm", "March to October"),
            Butterfly(12, "Comma Butterfly", "Species 6", android.R.drawable.ic_menu_gallery,
                "Orange butterfly with black spots and a white comma mark.",
                "Woodlands and gardens", "4.5-6.4 cm", "March to October"),
            Butterfly(13, "Cloudless Sulphur", "Species 7", android.R.drawable.ic_menu_gallery,
                "Large yellow butterfly that migrates long distances.",
                "Open areas and gardens", "5.6-7.4 cm", "Year-round in warm areas"),
            Butterfly(14, "Orange Sulphur", "Species 7", android.R.drawable.ic_menu_gallery,
                "Small orange butterfly commonly found in fields.",
                "Fields and meadows", "3.2-5.4 cm", "April to October"),
            Butterfly(15, "Spicebush Swallowtail", "Species 1", android.R.drawable.ic_menu_gallery,
                "Dark butterfly with blue and orange spots.",
                "Deciduous forests", "7.0-10.5 cm", "April to October"),
            Butterfly(16, "Black Swallowtail", "Species 2", android.R.drawable.ic_menu_gallery,
                "Black butterfly with yellow and blue markings.",
                "Gardens and fields", "6.9-8.4 cm", "April to October")
        )
    }

    fun getSpeciesList(): List<String> {
        return listOf("All Species", "Species 1", "Species 2", "Species 3", "Species 4", "Species 5", "Species 6", "Species 7")
    }
}

package com.example.butterflydetector.ui.speciescatalog

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.butterflydetector.R
import com.example.butterflydetector.model.Butterfly
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.ImageView
import android.widget.TextView

class ButterflyAdapter(
    private var butterflies: List<Butterfly>,
    private val onInfoClick: (Butterfly) -> Unit
) : RecyclerView.Adapter<ButterflyAdapter.ButterflyViewHolder>() {

    class ButterflyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.butterfly_image)
        val nameText: TextView = view.findViewById(R.id.butterfly_name)
        val infoButton: ImageButton = view.findViewById(R.id.info_button)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButterflyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_butterfly, parent, false)
        return ButterflyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButterflyViewHolder, position: Int) {
        val butterfly = butterflies[position]

        // Bild & Name setzen
        holder.imageView.setImageResource(butterfly.imageResId)
        holder.nameText.text = butterfly.name

        if (!butterfly.isPhotographed) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)  // 0 = schwarzweiß
            val filter = ColorMatrixColorFilter(matrix)
            holder.imageView.colorFilter = filter
        } else {
            holder.imageView.colorFilter = null
        }

        // Info-Button Klick
        holder.infoButton.setOnClickListener {
            onInfoClick(butterfly)
        }

        // Favoriten-Button Klick
        var isFavorite = false
        holder.favoriteButton.setOnClickListener {
            butterfly.isFavorite = !butterfly.isFavorite


            val icon = if (butterfly.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_border

            holder.favoriteButton.setImageResource(icon)
            }

        // Initial Icon setzen (wichtig für RecyclerView Recycling)
        val initialIcon = if (butterfly.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_border

        holder.favoriteButton.setImageResource(initialIcon)
        }

    override fun getItemCount() = butterflies.size

    fun updateButterflies(newButterflies: List<Butterfly>) {
        butterflies = newButterflies
        notifyDataSetChanged()
    }
}

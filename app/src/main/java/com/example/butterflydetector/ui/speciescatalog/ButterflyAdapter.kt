package com.example.butterflydetector.ui.speciescatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.butterflydetector.R
import com.example.butterflydetector.model.Butterfly

class ButterflyAdapter(
    private var butterflies: List<Butterfly>,
    private val onInfoClick: (Butterfly) -> Unit
) : RecyclerView.Adapter<ButterflyAdapter.ButterflyViewHolder>() {

    class ButterflyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.butterfly_image)
        val nameText: TextView = view.findViewById(R.id.butterfly_name)
        val infoButton: ImageButton = view.findViewById(R.id.info_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButterflyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_butterfly, parent, false)
        return ButterflyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ButterflyViewHolder, position: Int) {
        val butterfly = butterflies[position]

        holder.imageView.setImageResource(butterfly.imageResId)
        holder.nameText.text = butterfly.name

        holder.infoButton.setOnClickListener {
            onInfoClick(butterfly)
        }
    }

    override fun getItemCount() = butterflies.size

    fun updateButterflies(newButterflies: List<Butterfly>) {
        butterflies = newButterflies
        notifyDataSetChanged()
    }
}

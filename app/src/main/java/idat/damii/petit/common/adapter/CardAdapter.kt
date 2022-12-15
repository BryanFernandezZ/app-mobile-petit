package idat.damii.petit.common.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import idat.damii.petit.R

class CardAdapter: RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    val titles = arrayOf("Programar una cita", "Adoptar una Mascota")
    val images = intArrayOf(R.drawable.ic_reservar_cita, R.drawable.ic_baseline_pets_24)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = titles[position]
        holder.image.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var image: ImageView

        init {
            title = itemView.findViewById(R.id.titleCard)
            image = itemView.findViewById(R.id.imageCard)
        }
    }
}
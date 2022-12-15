package idat.damii.petit.common.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import idat.damii.petit.R
import idat.damii.petit.model.Mascota
import idat.damii.petit.view.CuestionarioAdopcionActivity
import idat.damii.petit.view.MascotaActivity

class MascotaAdapter : RecyclerView.Adapter<MascotaAdapter.ViewHolder> {

    var mascotas: List<Mascota>
    var context: Context

    constructor(context: Context, mascotas: List<Mascota>) {
        this.mascotas = mascotas
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota_item_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardMascota.setOnClickListener {
            val intent: Intent = Intent(context, CuestionarioAdopcionActivity::class.java).apply {
                putExtra("id", mascotas[position].id)
            }

            context.startActivity(intent)
        }

        holder.imvInfo.setOnClickListener {
            mostrarDetalleMascota(position)
        }

        Glide.with(context)
            .asDrawable()
            .load(mascotas[position].photo)
            .into(holder.imagenMascota)

        holder.nombreMascota.text = "${mascotas[position].name} - ${mascotas[position].age}"
    }

    private fun mostrarDetalleMascota(pos: Int) {
        var detalle = "Sexo: ${mascotas[pos].sex}\n" +
                "Raza: ${mascotas[pos].race}\n"
//                "Edad: ${mascotas[pos].age}"

        AlertDialog.Builder(context)
            .setTitle("Datos adicionales")
            .setMessage(detalle).show()
    }

    override fun getItemCount(): Int {
        return mascotas.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardMascota: MaterialCardView
        var imvInfo: ImageView
        var imagenMascota: ImageView
        var nombreMascota: TextView

        init {
            cardMascota = itemView.findViewById(R.id.cardMascota)
            imvInfo = itemView.findViewById(R.id.imvInfo)
            imagenMascota = itemView.findViewById(R.id.imvFotoMascota)
            nombreMascota = itemView.findViewById(R.id.txvNombreMascota)
        }
    }
}
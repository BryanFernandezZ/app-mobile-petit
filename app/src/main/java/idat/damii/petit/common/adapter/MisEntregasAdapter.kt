package idat.damii.petit.common.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import idat.damii.petit.R
import idat.damii.petit.model.Ubicacion
import idat.damii.petit.view.MapsActivity

class MisEntregasAdapter : RecyclerView.Adapter<MisEntregasAdapter.ViewHolder> {

    var context: Context
    var listaUbicaciones: List<Ubicacion>

    constructor(context: Context, listaUbicaciones: List<Ubicacion>) {
        this.context = context
        this.listaUbicaciones = listaUbicaciones
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardUbicacion: MaterialCardView
        var txvCliente: TextView
        var txvMascota: TextView

        init {
            cardUbicacion = itemView.findViewById(R.id.cardUbicacion)
            txvCliente = itemView.findViewById(R.id.txvClienteEnvio)
            txvMascota = itemView.findViewById(R.id.txvMascotaEnvio)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.ubicacion_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardUbicacion.setOnClickListener {
            val intent = Intent(context, MapsActivity::class.java).apply {
                putExtra("latitud", listaUbicaciones[position].latitude)
                putExtra("longitud", listaUbicaciones[position].length)
            }

            context.startActivity(intent)
        }

        holder.txvCliente.text = listaUbicaciones[position].cliente
        holder.txvMascota.text = listaUbicaciones[position].name
    }

    override fun getItemCount(): Int {
        return listaUbicaciones.size
    }
}
package idat.damii.petit.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import idat.damii.petit.R
import idat.damii.petit.retrofit.response.MiCitasResponse
import java.time.LocalDateTime

class MisCitasAdapter : RecyclerView.Adapter<MisCitasAdapter.ViewHolder> {

    var context: Context
    var misCitas: List<MiCitasResponse>

    constructor(context: Context, misCitas: List<MiCitasResponse>) {
        this.context = context
        this.misCitas = misCitas
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var citaEstado: TextView
        var citaTipoServicio: TextView
        var citaFechaAtencion: TextView

        init {
            citaEstado = itemView.findViewById(R.id.citaEstado)
            citaTipoServicio = itemView.findViewById(R.id.citaTipoServicio)
            citaFechaAtencion = itemView.findViewById(R.id.citaFechaAtencion)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.cita_template, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.citaEstado.text = misCitas[position].state.state

        var fecha: LocalDateTime = LocalDateTime.parse(misCitas[position].dateAttention)
        var texto =
            "${fecha.dayOfMonth}/${fecha.monthValue}/${fecha.year} - ${fecha.hour}:00"


        holder.citaFechaAtencion.text = texto
        holder.citaTipoServicio.text = misCitas[position].serviceType.serviceType
    }

    override fun getItemCount(): Int {
        return misCitas.size
    }
}
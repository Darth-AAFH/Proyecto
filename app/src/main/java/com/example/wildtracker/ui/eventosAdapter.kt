package com.example.wildtracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.metasdiseno.view.*

class eventosAdapter(private val mContext: Context, private val listaEventos: List<eventos>) :
    ArrayAdapter<eventos>(mContext, 0, listaEventos) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.eventosdiseno, parent, false)
        val evento = listaEventos[position]

        layout.textViewNombre.text = evento.nombre
        layout.textViewDescripcion.text = evento.descripcion
        layout.imageViewDibujo.setImageResource(evento.imagen)

        return layout
    }

}
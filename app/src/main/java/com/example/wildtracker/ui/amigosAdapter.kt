package com.example.wildtracker.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.amigosdiseno.view.*
import kotlinx.android.synthetic.main.metasdiseno.view.textViewNombre

class amigosAdapter (private val mContext: Context, private val listaAmigos: List<amigos>) : ArrayAdapter<amigos>(mContext, 0, listaAmigos) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.amigosdiseno, parent, false)
        val amigo = listaAmigos[position]

        layout.textViewNombre.text = amigo.nombre
        layout.textViewEjercicios.text = "Ejercicios: " + amigo.ejercicios
        layout.textViewNivel.text = "Nivel: " + (amigo.nivel).toString()
        layout.imageViewInsignia.setImageResource(amigo.imagen)

        return layout
    }

}
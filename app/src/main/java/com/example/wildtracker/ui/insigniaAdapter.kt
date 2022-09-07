package com.example.wildtracker.ui

import android.widget.ArrayAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.insigniasdiseno.view.*

class insigniaAdapter(private val mContext: Context, private val listaRutinas: List<insignias>) : ArrayAdapter<insignias>(mContext, 0, listaRutinas) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.insigniasdiseno, parent, false)

        val rutina = listaRutinas[position]

        layout.textViewNombre.text = rutina.nombre
        layout.textViewNivel.text = "Nivel: " + rutina.nivel.toString()
        layout.imageViewInsignia.setImageResource(rutina.imagen)
        var num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
        var nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]

        layout.setOnClickListener {

            System.exit(-1)


        }




        return layout
    }
}
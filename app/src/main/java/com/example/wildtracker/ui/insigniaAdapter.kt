package com.example.wildtracker.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.insigniasdiseno.view.*

class insigniaAdapter(private val mContext: Context, private val listaInsignias: List<insignias>) :
    ArrayAdapter<insignias>(mContext, 0, listaInsignias) {
    companion object {
        private const val REQUEST_CODE = 42
        var nombreCajaFotos = ""
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.insigniasdiseno, parent, false)
        val intent = Intent(mContext, FotosActivity::class.java)
        val insignia = listaInsignias[position]

        if (insignia.rutina) {
            layout.textViewNombre.text = insignia.nombre
            layout.textViewNivel.text = "Nivel: " + insignia.nivel.toString()
            layout.imageViewDibujo.setImageResource(insignia.imagen1)
            //var num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            //var nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]

            layout.setOnClickListener {
                sequenceOf(
                    Toast.makeText(mContext, "Rutina: ${insignia.nombre}", Toast.LENGTH_LONG).show()
                )
                nombreCajaFotos = insignia.nombre
                Utils.startActivity(mContext, FotosActivity::class.java, insignia.nombre)
            }
        } else {
            layout.textViewNombre.setVisibility(View.INVISIBLE)
            layout.textViewNivel.setVisibility(View.INVISIBLE)
            layout.imageViewDibujo.setVisibility(View.INVISIBLE)

            if (insignia.imagen2 == 0) {
                layout.imageViewDibujo1.setImageResource(insignia.imagen1)
                layout.imageViewDibujo1.setVisibility(View.VISIBLE)
            } else {
                layout.imageViewDibujo2.setImageResource(insignia.imagen1)
                layout.imageViewDibujo3.setImageResource(insignia.imagen2)
                layout.imageViewDibujo2.setVisibility(View.VISIBLE)
                layout.imageViewDibujo3.setVisibility(View.VISIBLE)
            }
        }

        return layout
    }

    class Utils {

        companion object {
            fun startActivity(context: Context, clazz: Class<*>, nombre: String) {

                val intent = Intent(context, clazz)
                intent.putExtra(
                    "Rutina",
                    nombre
                ) //Manda el nombre de la rutina mediante el intent, se cacha en el activirty fotos

                // start your next activity

                context.startActivity(intent)

            }
        }

    }
}

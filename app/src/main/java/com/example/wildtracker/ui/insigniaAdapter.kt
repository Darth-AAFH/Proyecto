package com.example.wildtracker.ui

import android.widget.ArrayAdapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.insigniasdiseno.view.*

class insigniaAdapter(private val mContext: Context, private val listaRutinas: List<insignias>) : ArrayAdapter<insignias>(mContext, 0, listaRutinas) {
    companion object {
        private const val REQUEST_CODE = 42
      var nombreCajaFotos =""
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.insigniasdiseno, parent, false)
        val intent = Intent(mContext, FotosActivity::class.java)
        val rutina = listaRutinas[position]


        layout.textViewNombre.text = rutina.nombre
        layout.textViewNivel.text = "Nivel: " + rutina.nivel.toString()
        layout.imageViewInsignia.setImageResource(rutina.imagen)
        var num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
        var nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]

        layout.setOnClickListener {
            sequenceOf(Toast.makeText(mContext, "Rutina: ${rutina.nombre}", Toast.LENGTH_LONG).show())
            nombreCajaFotos = rutina.nombre
            Utils.startActivity(mContext,FotosActivity::class.java,rutina.nombre)
        }


        return layout
    }
    class Utils {

        companion object {
            fun startActivity(context: Context, clazz: Class<*>, nombre: String) {

                val intent = Intent(context, clazz)
                intent.putExtra("Rutina",nombre) //Manda el nombre de la rutina mediante el intent, se cacha en el activirty fotos

                // start your next activity

                context.startActivity(intent)

            }
        }

    }
}

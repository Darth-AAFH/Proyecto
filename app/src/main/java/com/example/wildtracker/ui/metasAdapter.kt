package com.example.wildtracker.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.metasdiseno.view.*

class metasAdapter(private val mContext: Context, private val listaMetas: List<metas>) : ArrayAdapter<metas>(mContext, 0, listaMetas) {
    companion object {
        private const val REQUEST_CODE = 42
        var nombreCajaFotos =""
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(mContext).inflate(R.layout.metasdiseno, parent, false)
        val intent = Intent(mContext, FotosActivity::class.java)
        val meta = listaMetas[position]


        layout.textViewNombre.text = meta.nombre
        layout.textViewMeta.text = "Meta: " + meta.meta
        layout.textViewDescripcion.text = "Se hace: " + meta.descripcion
        layout.imageViewDibujo.setImageResource(meta.imagen)

        layout.setOnClickListener {
            sequenceOf(Toast.makeText(mContext, "Meta: ${meta.nombre}", Toast.LENGTH_LONG).show())
            nombreCajaFotos = meta.nombre
            Utils.startActivity(mContext,FotosActivity::class.java, meta.nombre)
        }

        return layout
    }
    class Utils {

        companion object {
            fun startActivity(context: Context, clazz: Class<*>, nombre: String) {

                val intent = Intent(context, clazz)
                intent.putExtra("Meta",nombre) //Manda el nombre de la rutina mediante el intent, se cacha en el activirty fotos

                context.startActivity(intent)

            }
        }

    }
}
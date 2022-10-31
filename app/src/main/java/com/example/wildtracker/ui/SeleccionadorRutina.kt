package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.wildtracker.R

class SeleccionadorRutina : AppCompatActivity() {

    var listViewRutinas2: ListView?= null

    private fun CargarListas(){ //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)

            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)

            MainActivity.validadorAcomodo = false
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinas)
        listViewRutinas2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionador_rutina)

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)

        CargarListas()
        Toast.makeText(this, "Seleccione un rutina", Toast.LENGTH_SHORT).show()

        listViewRutinas2!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            val nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]
            val ejercicios = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[3]
            val nivelAux = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[2]

            val arreglo: Array<String?>
            arreglo = nivelAux.split(" ").toTypedArray()
            val nivel = arreglo[1]!!.toInt()


            Toast.makeText(this, "Se ha seleccionado la rutina: "+num+", nombre: "+nombre, Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "ejercicios: "+ejercicios+", nivel: "+nivel, Toast.LENGTH_SHORT).show()

            Toast.makeText(this, "Se añadio correctamente la rutina", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SeleccionadorRutina, SeguimientoActivity::class.java)
            startActivity(intent)
        }
    }
}
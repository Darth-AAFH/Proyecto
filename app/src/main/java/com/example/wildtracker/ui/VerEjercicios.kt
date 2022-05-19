package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wildtracker.R

class VerEjercicios : AppCompatActivity() {

    var listViewEjercicios: ListView?= null

    var listaEjercicios = ArrayList<String>()

    var cadena = "["

    private fun CargarTabla(){
        for(i in MainActivity.listaEjercicios){
            val arreglo = i.split(" ").toTypedArray()
            val id = arreglo[0].toInt()
            if(id > 15) {
                listaEjercicios.add(i)
            }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaEjercicios)
        listViewEjercicios!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicios)

        listViewEjercicios = findViewById(R.id.listViewEjercicios)

        if(!MainActivity.listaRutinas.isEmpty()) {
            for (i in 0..MainActivity.listaRutinas.size - 1) {
                cadena += MainActivity.listaRutinas[i].split(" | ").toTypedArray()[3] //agrega los ejercicios
                cadena += "," //y una coma
            }
        }

        var contador = 0
        for(i in 0 until cadena.length){
            contador += 1
        }
        cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

        val arreglo: Array<String?>
        arreglo = cadena.split(",").toTypedArray() //toma los ids de los ejercicios

        CargarTabla()
        Toast.makeText(this, "Click para editar ejercicio", Toast.LENGTH_SHORT).show()

        listViewEjercicios!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[0]
            var validadorEdicion = true

            for(i in arreglo){ //lo compara con todos los ejercicios de las rutinas
                if(num == i){ //si está en una rutina
                    validadorEdicion = false //no lo podra editar
                }
            }

            if(validadorEdicion) {
                val nombre = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[1]
                val tipo = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[2]
                val peso = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[3]

                val intent = Intent(this@VerEjercicios, EditorEjercicios::class.java)
                intent.putExtra("Num", num)
                intent.putExtra("Nombre", nombre)
                intent.putExtra("Tipo", tipo)
                intent.putExtra("Peso", peso)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No se puede editar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
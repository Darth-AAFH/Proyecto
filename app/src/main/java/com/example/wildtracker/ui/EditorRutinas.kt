package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import java.util.ArrayList
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("NAME_SHADOWING")
class EditorRutinas : AppCompatActivity() {

    var editTextNombre4: EditText ?= null
    private var buttonGuardar2: Button?= null
    private var buttonBorrar2: Button?= null
    var listViewEjerciciosHechos2: ListView?= null
    var listViewEjerciciosRutina2: ListView?= null

    var listado2 = ArrayList<String>()
    var datos = ArrayList<String>()
    var contadorMax = 0

    private val db = FirebaseFirestore.getInstance()
    var num = 0; var nombre: String? = null; var ejercicios: String? = null; var nivel = 0

    private fun CargarEjercicios() { //Funcion que trae los ejercicios
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaEjercicios)
        listViewEjerciciosHechos2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private fun CargarEjerciciosDeRutina(arreglo: Array<String?>) { //Funcion que trae la tabla
        contadorMax = arreglo.size
        for(i in 0 until arreglo.size) {
            for(j in 0 until MainActivity.listaEjercicios.size) {
                val lineaEjercicio = MainActivity.listaEjercicios[j]
                val arregloLinea: Array<String?> = lineaEjercicio.split(" ").toTypedArray()
                val idEjercicio = arregloLinea[0]!!.toInt()

                if (arreglo[i]!!.toInt() == idEjercicio) {
                    datos.add(lineaEjercicio)
                }
            }
        }

        listado2 = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
        listViewEjerciciosRutina2!!.setAdapter(adapter) //La rutina se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_rutinas)

        val b = intent.extras //b toma el id de la rutina a editar
        if (b != null) {
            num = b.getInt("Num")
            nombre = b.getString("Nombre")
            ejercicios = b.getString("Ejercicios")
            nivel = b.getInt("Nivel")
        }

        editTextNombre4 = findViewById<View>(R.id.editTextNombre4) as EditText
        buttonGuardar2 = findViewById(R.id.buttonGuardar2)
        buttonBorrar2 = findViewById(R.id.buttonBorrar2)
        listViewEjerciciosHechos2 = findViewById(R.id.listViewEjerciciosHechos2)
        listViewEjerciciosRutina2 = findViewById(R.id.listViewEjerciciosRutina2)

        CargarEjercicios()

        editTextNombre4!!.setText(nombre)

        val arreglo: Array<String?>
        arreglo = ejercicios!!.split(",").toTypedArray() //toma los ids de los ejercicios
        CargarEjerciciosDeRutina(arreglo)

        if(nivel != 0){
            Toast.makeText(this, "Si modifica la rutina el nivel regresara a 0", Toast.LENGTH_LONG).show()
        }

        buttonGuardar2!!.setOnClickListener{
            val cambioNombre = editTextNombre4!!.text.toString()
            if(cambioNombre == ""){ //Si el nombre esta vacio lo hara notar
                Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
            }else {
                if(guardar(num, cambioNombre)) {
                    val intent = Intent(this@EditorRutinas, PlantillasActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        buttonBorrar2!!.setOnClickListener{
            borrar(num)
            val intent = Intent(this@EditorRutinas, PlantillasActivity::class.java)
            startActivity(intent)
        }

        listViewEjerciciosRutina2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            var linea: String
            linea = this.listado2[position].split(" ").toTypedArray()[0]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[1]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[2]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[3]

            val posicion = listado2.indexOf(linea) //Toma la posición del ejercicio en el array list

            listado2.removeAt(posicion) //Remueve el ejercicio del array list
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
            listViewEjerciciosRutina2!!.setAdapter(adapter)

            contadorMax -= 1
        }

        listViewEjerciciosHechos2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(contadorMax >= 10){ //un validador para que solo hayan max 10 ejercicios
                Toast.makeText(this, "Solo se pueden agregar 10 ejercicios a la rutina", Toast.LENGTH_SHORT).show()
            }else {
                var linea: String
                linea = MainActivity.listaEjercicios[position].split(" ").toTypedArray()[0]; linea += " | " //va a tomar el indice
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[1]; linea += " | " //nombre
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[2]; linea += " | " //tipo
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[3] //y peso del ejercicio seleccionado

                datos.add(linea) //y lo va a añadir a
                listado2 = datos //el listado de los ejercicios de rutina
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
                listViewEjerciciosRutina2!!.setAdapter(adapter) //después lo va a poner en la listView

                contadorMax += 1
            }
        }
    }

    private fun guardar(Id: Int, Nombre: String): Boolean{
        var confirmacion = false

        var cadena: String //Variables para tomar los datos
        val arreglo: Array<String?>

        cadena = listado2.toString() //Toma la lista de los ejercicios
        arreglo = cadena.split(" ").toTypedArray() //arreglo tiene toda la lista separada por espacios

        cadena = arreglo[0].toString() //guarda el primer indice de los ejercicios
        cadena += ","
        for (i in 0 until arreglo.size) {//recorre todo el arreglo
            if(arreglo[i]!!.isDigitsOnly()){ //si uno de los datos es numero
                cadena += arreglo[i] //lo añade a la cadena
                cadena += ","
            }
        }

        var contador = 0
        for(i in 0 until cadena.length){
            contador += 1
        }
        cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

        if(cadena == "]"){
            Toast.makeText(this, "La rutina debe contener al menos un ejercicio", Toast.LENGTH_SHORT).show()
        }else {
            MainActivity.user?.let{ usuario ->
                db.collection("users").document(usuario).collection("rutinas").document(Id.toString()).set(
                    hashMapOf(
                        "id" to Id,
                        "nombre" to Nombre,
                        "ejercicios" to cadena,
                        "nivel" to 0,
                        "xp" to 0,
                        "horas" to 0,
                        "minutos" to 0,
                        "segundos" to 0
                    )
                )
            }

            val linea: String; val cadenaCambio: String
            linea = num.toString() + " | " + nombre + " | Nivel: " + nivel.toString() + " | " + ejercicios
            cadenaCambio = Id.toString() + " | " + Nombre + " | Nivel: 0 | " + cadena

            val posicion: Int
            posicion = MainActivity.listaRutinas.indexOf(linea)
            MainActivity.listaRutinas.set(posicion, cadenaCambio)

            Toast.makeText(this, "Se ha modificado la rutina", Toast.LENGTH_SHORT).show()
            confirmacion = true
        }

        return confirmacion
    }

    private fun borrar(Id: Int) {
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("rutinas").document(Id.toString()).delete()
        }

        val linea: String
        linea = num.toString() + " | " + nombre + " | Nivel: " + nivel.toString() + " | " + ejercicios //toma la rutina como está escrito

        val posicion: Int
        posicion = MainActivity.listaRutinas.indexOf(linea) //busca su posición en la lista
        MainActivity.listaRutinas.removeAt(posicion) //y lo borra

        //hace los mismo para la lista de vista
        MainActivity.listaRutinasVista.removeAt(posicion)

        Toast.makeText(this, "Se ha BORRADO la rutina", Toast.LENGTH_SHORT).show()
    }

}
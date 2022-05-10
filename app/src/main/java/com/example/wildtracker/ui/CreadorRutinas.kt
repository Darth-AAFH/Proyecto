package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import java.util.*
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

class CreadorRutinas : AppCompatActivity() {//////////////////////////////////////////////////////

    var editTextNombre3: EditText ?= null
    private var buttonCrear2: Button?= null
    var listViewEjerciciosHechos: ListView?= null
    var listViewEjerciciosRutina: ListView?= null

    var listado: ArrayList<String>? = null
    var listado2 = ArrayList<String>()
    var datos = ArrayList<String>()
    var contadorMaxEjer = 0

    var arregloRutinas = Array<rutina?>(51){null}
    var validadorVacia = true

    var listaEjercicios = ArrayList<String>()
    var contadorMaxRut = 0; var idFinalRut = 0

    private val db = FirebaseFirestore.getInstance()

    private fun CargarTabla() { //Funcion que trae la tabla
        listaEjercicios.sort()
        listado = listaEjercicios
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewEjerciciosHechos!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_rutinas)

        val b = intent.extras
        if (b != null) {
            listaEjercicios = b.getStringArrayList("LE") as ArrayList<String>
            contadorMaxRut = b.getInt("ContadorMaxRut")
            idFinalRut = b.getInt("IdFinalRut")
        }

        editTextNombre3 = findViewById<View>(R.id.editTextNombre3) as EditText
        buttonCrear2 = findViewById(R.id.buttonCrear2)
        listViewEjerciciosHechos = findViewById(R.id.listViewEjerciciosHechos)
        listViewEjerciciosRutina = findViewById(R.id.listViewEjerciciosRutina)

        Toast.makeText(this, "Click para añadir a la rutina", Toast.LENGTH_SHORT).show()
        CargarTabla()

        buttonCrear2!!.setOnClickListener{
            val nombre = editTextNombre3!!.text.toString()
            if(crear(nombre)){
                if(validadorVacia == true) {
                    val intent = Intent(this@CreadorRutinas, PlantillasActivity::class.java)
                    startActivity(intent)
                }
            }else {
                Toast.makeText(this, "Se ha alcanzado el numero maximo de rutinas", Toast.LENGTH_SHORT).show()
            }
        }

        listViewEjerciciosRutina!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            var linea: String
            linea = this.listado2[position].split(" ").toTypedArray()[0]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[1]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[2]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[3]

            val posicion = listado2.indexOf(linea) //Toma la posición del ejercicio en el array list

            listado2.removeAt(posicion) //Remueve el ejercicio del array list
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
            listViewEjerciciosRutina!!.setAdapter(adapter)

            contadorMaxEjer -= 1
        }

        listViewEjerciciosHechos!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(contadorMaxEjer >= 10){ //un validador para que solo hayan max 10 ejercicios
                Toast.makeText(this, "Solo se pueden agregar 10 ejercicios a la rutina", Toast.LENGTH_SHORT).show()
            }else {
                var linea: String
                linea = this.listado!![position].split(" ").toTypedArray()[0]; linea += " | " //va a tomar el indice
                linea += this.listado!![position].split(" | ").toTypedArray()[1]; linea += " | " //nombre
                linea += this.listado!![position].split(" | ").toTypedArray()[2]; linea += " | " //tipo
                linea += this.listado!![position].split(" | ").toTypedArray()[3] //y peso del ejercicio seleccionado

                datos.add(linea) //y lo va a añadir a
                listado2 = datos //el listado de los ejercicios de rutina
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
                listViewEjerciciosRutina!!.setAdapter(adapter) //después lo va a poner en la listView

                contadorMaxEjer += 1
            }
        }
    }

    private fun crear(Nombre: String): Boolean{
        var confirmacion = false
        if(contadorMaxRut <= 50){
            var nombre = Nombre

            if(nombre == "")
                nombre = "Rutina " + (idFinalRut + 1)

            var cadena: String //Variables para tomar los datos
            val arreglo: Array<String?>

            cadena = listado2.toString() //Toma la lista de los ejercicios
            arreglo = cadena.split(" ").toTypedArray() //arreglo tiene toda la lista separada por espacios

            cadena = arreglo[0].toString() //guarda el primer indice de los ejercicios
            cadena += ","
            var contador = 0
            for (i in 0 until arreglo.size) {//recorre todo el arreglo
                contador += 1
                if(arreglo[i]!!.isDigitsOnly()){ //si uno de los datos es numero
                    cadena += arreglo[i] //lo añade a la cadena
                    cadena += ","
                }
            }

            contador = 0
            for(i in 0 until cadena.length){
                contador += 1
            }
            cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

            validadorVacia = true
            if(cadena == "]"){
                Toast.makeText(this, "No se puede crear una rutina sin ejercicios", Toast.LENGTH_SHORT).show()
                validadorVacia = false
            }else {
                arregloRutinas[contadorMaxRut] = rutina(idFinalRut + 1, nombre, cadena)
                guardarBD(arregloRutinas[contadorMaxRut]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

    private fun guardarBD(Rutina: rutina) {
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("rutinas")
                .document(Rutina.id.toString()).set(
                    hashMapOf(
                        "id" to Rutina.id,
                        "nombre" to Rutina.nombre,
                        "ejercicios" to Rutina.ejercicios,
                        "nivel" to 0
                    )
                )
        }
        Toast.makeText(this, "Se ha guardado el ejercicio", Toast.LENGTH_SHORT).show()
    }

}
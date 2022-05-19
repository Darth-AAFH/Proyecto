package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("NAME_SHADOWING")
class EditorEjercicios : AppCompatActivity() {
    //
    var editTextNombre2: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso2: Switch ?= null
    private var buttonGuardar: Button?= null; private var buttonBorrar: Button?= null

    var num = ""
    var nombre: String? = null; var tipo: String? = null; var peso: String? = null

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_ejercicios)

        val b = intent.extras //b toma los datos enviados del Listado
        if (b != null) { //Si existen datos
            num = b.getString("Num").toString() //Los guarda en estas variables
            nombre = b.getString("Nombre")
            tipo = b.getString("Tipo")
            peso = b.getString("Peso")
        }

        editTextNombre2 = findViewById<View>(R.id.editTextNombre2) as EditText
        switchPeso2 = findViewById<View>(R.id.switchPeso2) as Switch
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonBorrar = findViewById(R.id.buttonBorrar)
        val spinnerTipos2: Spinner = findViewById(R.id.spinnerTipos2)

        val listaSpinner = listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSpinner)
        spinnerTipos2.adapter = adaptadorSpinner

        editTextNombre2!!.setText(nombre)
        if(tipo == "Piernas") spinnerTipos2.setSelection(0)
        if(tipo == "Abdomen") spinnerTipos2.setSelection(1)
        if(tipo == "Pecho") spinnerTipos2.setSelection(2)
        if(tipo == "Espalda") spinnerTipos2.setSelection(3)
        if(tipo == "Brazos") spinnerTipos2.setSelection(4)
        if(tipo == "Hombros") spinnerTipos2.setSelection(5)
        if(tipo == "Otro") spinnerTipos2.setSelection(6)
        if(peso == "Con peso") switchPeso2!!.setChecked(true)
        if(peso == "Sin peso") switchPeso2!!.setChecked(false)

        buttonGuardar!!.setOnClickListener{
            val cambioNombre = editTextNombre2!!.text.toString(); val cambioTipo = spinnerTipos2.selectedItem.toString(); val cambioPeso = switchPeso2!!.isChecked()
            if(guardar(num, cambioNombre, cambioTipo, cambioPeso)){
                val intent = Intent(this@EditorEjercicios, PlantillasActivity::class.java)
                startActivity(intent)
            }
        }

        buttonBorrar!!.setOnClickListener{
            borrar(num)
            val intent = Intent(this@EditorEjercicios, PlantillasActivity::class.java)
            startActivity(intent)
        }
    }

    private fun guardar(Id: String, Nombre: String, Tipo: String, Peso: Boolean): Boolean{
        if(Nombre == ""){ //Si el nombre esta vacio lo hara notar
            Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
            return false
        }else {
            val arreglo: Array<String?>
            arreglo = Nombre.split(" ").toTypedArray() //va a tomar el nuevo nombre

            var validadorNombre = true
            for (i in 0 until arreglo.size) {//recorre todo el nombre
                if(arreglo[i]!!.isDigitsOnly()) { //si uno de los datos es numero lo hara notar
                    Toast.makeText(this, "El nombre de un ejercicio no puede contener numeros", Toast.LENGTH_SHORT).show()
                    validadorNombre = false
                }
            }

            if(validadorNombre == true){
                MainActivity.user?.let{ usuario ->
                    db.collection("users").document(usuario).collection("ejercicios").document(Id).set(
                        hashMapOf(
                            "id" to Id.toInt(),
                            "nombre" to Nombre,
                            "tipo" to Tipo,
                            "peso" to Peso
                        )
                    )
                }

                val linea: String; val cadenaCambio: String
                linea = num + " | " + nombre + " | " + tipo + " | " + peso //toma el ejercicio
                cadenaCambio = Id + " | " + Nombre + " | " + Tipo + " | " + Peso

                val posicion: Int
                posicion = MainActivity.listaEjercicios.indexOf(linea) //lo busca en la lista
                MainActivity.listaEjercicios.set(posicion, cadenaCambio) //y lo cambia

                Toast.makeText(this, "Se ha modificado el ejercicio", Toast.LENGTH_SHORT).show()
                return true
            }else{
                return false
            }
        }
    }

    private fun borrar(Id: String) {
        MainActivity.user?.let{ usuario -> //abre la base de datos
            db.collection("users").document(usuario).collection("ejercicios").document(Id).delete() //y borra el ejercicio seleccionado
        }

        val linea: String
        linea = num + " | " + nombre + " | " + tipo + " | " + peso //toma el ejercicio

        val posicion: Int
        posicion = MainActivity.listaEjercicios.indexOf(linea) //lo busca en la lista
        MainActivity.listaEjercicios.removeAt(posicion) //y lo borra

        Toast.makeText(this, "Se ha BORRADO el ejercicio", Toast.LENGTH_SHORT).show() //manda mensaje de confirmación
    }

}
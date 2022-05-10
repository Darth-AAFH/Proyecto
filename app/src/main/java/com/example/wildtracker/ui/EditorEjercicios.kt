package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("NAME_SHADOWING")
class EditorEjercicios : AppCompatActivity() {//////////////////////////////////////////////////////

    var editTextNombre2: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso2: Switch ?= null
    private var buttonGuardar: Button?= null; private var buttonBorrar: Button?= null

    var num = 0
    var nombre: String? = null; var tipo: String? = null; var peso: String? = null

    private val db = FirebaseFirestore.getInstance()
    var validadorGuardar = true
    var cadena: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_ejercicios)

        val b = intent.extras //b toma los datos enviados del Listado
        if (b != null) { //Si existen datos
            num = b.getInt("Num") //Los guarda en estas variables
            nombre = b.getString("Nombre")
            tipo = b.getString("Tipo")
            peso = b.getString("Peso")
            cadena = b.getString("Cadena")
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

        var contador = 0
        for(i in 0 until cadena!!.length){ //cuenta la cantidad de ejercicios que hay
            contador += 1
        }
        if(contador != 1) { //si hay por lo menos un ejercicio
            cadena = cadena!!.substring(1, contador - 1) //quita el '[' y la última coma
        }

        if(contador >= 3) { //contador es igual a 3 si hay por lo menos un ejercicio
            val arreglo: Array<String?>
            arreglo = cadena!!.split(",")
                .toTypedArray() //arreglo tiene todos los ejercicios de todas las rutinas

            for (i in 0 until arreglo.size) {//recorre todo el arreglo
                if (num == arreglo[i]!!.toInt()) { //si el ejercicio que se desea borrar esta en los de las rutinas
                    validadorGuardar = false
                }
            }
        }

        buttonGuardar!!.setOnClickListener{
            if(validadorGuardar == true) {
                val cambioNombre = editTextNombre2!!.text.toString(); val cambioTipo = spinnerTipos2.selectedItem.toString(); val cambioPeso = switchPeso2!!.isChecked()
                if(guardar(num, cambioNombre, cambioTipo, cambioPeso)){
                    finish() //hacer que vuelva hasta plantillas y no a ver ejercicio
                }
            }else{
                Toast.makeText(this, "No se puede editar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
            }
        }//////////////////////////////////////////////////////

        buttonBorrar!!.setOnClickListener{
            if(validadorGuardar == true) {
                borrar(num)
                finish() //hacer que vuelva hasta plantillas y no a ver ejercicio
            }else{
                Toast.makeText(this, "No se puede borrar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
            }
        }//////////////////////////////////////////////////////
    }

    private fun guardar(Id: Int, Nombre: String, Tipo: String, Peso: Boolean): Boolean{
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
                    db.collection("users").document(usuario).collection("ejercicios").document(Id.toString()).set(
                        hashMapOf(
                            "id" to Id,
                            "nombre" to Nombre,
                            "tipo" to Tipo,
                            "peso" to Peso
                        )
                    )
                }

                Toast.makeText(this, "Se ha modificado el ejercicio (incluso si no lo nota)", Toast.LENGTH_SHORT).show()
                return true
            }else{
                return false
            }
        }
    }

    private fun borrar(Id: Int) {
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios").document(Id.toString()).delete()
        }

        Toast.makeText(this, "Se ha BORRADO el ejercicio (incluso si no lo nota)", Toast.LENGTH_SHORT).show()
    }

}
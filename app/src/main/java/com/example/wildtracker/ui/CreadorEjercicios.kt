package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

class CreadorEjercicios : AppCompatActivity() {

    var editTextNombre: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso: Switch ?= null
    private var buttonCrear: Button?= null; private var buttonEditar: Button ?= null

    var arregloEjercicios = Array<ejercicio?>(66){null}
    var validadorNombre = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_ejercicios)

        editTextNombre = findViewById<View>(R.id.editTextNombre) as EditText
        switchPeso = findViewById<View>(R.id.switchPeso) as Switch
        buttonCrear = findViewById(R.id.buttonCrear)
        buttonEditar = findViewById(R.id.buttonEditar)
        val spinnerTipos: Spinner = findViewById(R.id.spinnerTipos)

        val lista0 = listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptador0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, lista0)
        spinnerTipos.adapter = adaptador0

        buttonCrear!!.setOnClickListener{
            /*val nombre = editTextNombre!!.text.toString(); val tipo = spinnerTipos.selectedItem.toString(); val peso = switchPeso!!.isChecked()
            if(crear(nombre, tipo, peso)){
                if(validadorNombre) {
                    finish()
                }
            }else {
                Toast.makeText(this, "Se ha alcanzado el numero maximo de ejercicios", Toast.LENGTH_SHORT).show()
            }

             */
            crear("Lagartijas", "Brazos", false)
        }

        buttonEditar!!.setOnClickListener{
            //val intent = Intent(this@CreadorEjercicios, VerEjercicios::class.java)
            //startActivity(intent)
            Toast.makeText(this, "El nuevo ejercicio debera tener id: "+aux, Toast.LENGTH_SHORT).show()
        }
    }

    private val db = FirebaseFirestore.getInstance()
    var Nombre2: String? = null
    var ultimoEjercicio = 0
    var aux: String ?= null

    private fun crear(Nombre: String, Tipo: String, validadorPeso: Boolean) {

        /*var contador = 0
        MainActivity.user?.let { usuario ->
            db.collection("users").document(usuario).collection("ejercicios").get().addOnSuccessListener {
                 for(ejercicio in it){
                     contador += 1
                 }
            }
        }
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios")
                .document(contador.toString()).get().addOnSuccessListener {
                aux = it.get("id") as String?
            }
        }*/


        //escribir
        var id = 1
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios").document(id.toString()).set(
                hashMapOf(
                    "id" to id,
                    "nombre" to Nombre,
                    "tipo" to Tipo,
                    "peso" to validadorPeso
                )
            )
        }


        /*//tomar datos
        var id = 2
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios")
                .document(id.toString()).get().addOnSuccessListener {
                    Nombre2 = it.get("nombre") as String?

                }
        }
        */

    }

    /*private fun crear(Nombre: String, Tipo: String, validadorPeso: Boolean): Boolean {
        var contadorMax = 1; var idFinal = 0

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Tipo, Peso from Ejercicios" //Se puede solo con el ID?
        val c = db.rawQuery(sql, null) //Se crea un cursor
        if (c.moveToFirst()) {
            do {
                contadorMax += 1 ////////Toma la cantidad de ejercicios que hayan
                idFinal = c.getInt(0) ///Toma el id del ultimo ejercicio
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        var confirmacion = false
        if(contadorMax <= 65){//////////////numero máx de ejercicios que el usuario puede crear (50)
            var nombre = Nombre; val tipo = Tipo; val peso = validadorPeso

            if(nombre == "")
                nombre = "Ejercicio" + (idFinal - 14)

            val arreglo: Array<String?>
            arreglo = nombre.split(" ").toTypedArray()

            validadorNombre = true
            for (i in 0 until arreglo.size) {//recorre todo el nombre
                if(arreglo[i]!!.isDigitsOnly()) { //si uno de los datos es numero
                    Toast.makeText(this, "El nombre de un ejercicio no puede contener numeros", Toast.LENGTH_SHORT).show()
                    validadorNombre = false
                }
            }

            if(validadorNombre == true){
                arregloEjercicios[contadorMax] = ejercicio(idFinal+1, nombre, tipo, peso)
                guardarLocal(arregloEjercicios[contadorMax]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

     */

    private fun guardarLocal(Ejercicio: ejercicio) {
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

        try {
            val c = ContentValues() //Se llena con los valores tomados de las editText
            c.put("Id", Ejercicio.id)
            c.put("Nombre", Ejercicio.nombre)
            c.put("Tipo", Ejercicio.tipo)
            c.put("Peso", Ejercicio.peso)
            db.insert("EJERCICIOS", null, c)
            db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
            Toast.makeText(this, "Se ha guardado el ejercicio", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ha habido un error", Toast.LENGTH_SHORT).show()
        }
    }

}
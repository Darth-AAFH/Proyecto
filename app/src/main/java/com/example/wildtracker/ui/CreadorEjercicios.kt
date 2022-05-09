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

class CreadorEjercicios : AppCompatActivity() {

    var editTextNombre: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso: Switch ?= null
    private var buttonCrear: Button?= null; private var buttonEditar: Button ?= null

    var arregloEjercicios = Array<ejercicio?>(66){null}
    var validadorNombre = true

    private val db = FirebaseFirestore.getInstance()
    var contadorMax = 0; var idFinal = 0; var idAux = 0

    val listaEjercicios = ArrayList<String>()
    var LEAux = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_ejercicios)

        editTextNombre = findViewById<View>(R.id.editTextNombre) as EditText
        switchPeso = findViewById<View>(R.id.switchPeso) as Switch
        buttonCrear = findViewById(R.id.buttonCrear)
        buttonEditar = findViewById(R.id.buttonEditar)
        val spinnerTipos: Spinner = findViewById(R.id.spinnerTipos)

        val listaSpinner = listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSpinner)
        spinnerTipos.adapter = adaptadorSpinner

        MainActivity.user?.let { usuario ->
            db.collection("users").document(usuario).collection("ejercicios") //abre la base de datos
                .get().addOnSuccessListener {
                    for(ejercicio in it){ //para cada ejercicio
                        contadorMax += 1 //cuenta cuantos ejercicios hay
                        idAux = (ejercicio.get("id") as Long).toInt() //toma el id
                        if(idFinal < idAux){ //si es un id mayor
                            idFinal = (ejercicio.get("id") as Long).toInt() //lo va a guardar como el id final
                        }

                        LEAux = ejercicio.get("nombre").toString() //toma el nombre del ejercicio
                        listaEjercicios.add(LEAux) //y lo guarda en la lista de ejrcicios
                    }
                }
        }

        buttonCrear!!.setOnClickListener{
            val nombre = editTextNombre!!.text.toString(); val tipo = spinnerTipos.selectedItem.toString(); val peso = switchPeso!!.isChecked()
            if(crear(nombre, tipo, peso)){
                if(validadorNombre) {
                    finish()
                }
            }else {
                Toast.makeText(this, "Se ha alcanzado el numero maximo de ejercicios", Toast.LENGTH_SHORT).show()
            }
        }

        buttonEditar!!.setOnClickListener{
            val intent = Intent(this@CreadorEjercicios, VerEjercicios::class.java)
            intent.putExtra("LE", listaEjercicios)
            startActivity(intent)
        }
    }

    private fun crear(Nombre: String, Tipo: String, validadorPeso: Boolean): Boolean{
        var confirmacion = false
        if(contadorMax <= 65){//////////////numero máx de ejercicios que el usuario puede crear (50)
            var nombre = Nombre

            if(nombre == ""){
                val idF = idFinal
                nombre = "Ejercicio" + (idF - 14)
            }

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
                arregloEjercicios[contadorMax] = ejercicio(idFinal+1, nombre, Tipo, validadorPeso)
                guardarBD(arregloEjercicios[contadorMax]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

    private fun guardarBD(Ejercicio: ejercicio) {
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios")
                .document(Ejercicio.id.toString()).set(
                hashMapOf(
                    "id" to Ejercicio.id,
                    "nombre" to Ejercicio.nombre,
                    "tipo" to Ejercicio.tipo,
                    "peso" to Ejercicio.peso
                )
            )
        }
        Toast.makeText(this, "Se ha guardado el ejercicio", Toast.LENGTH_SHORT).show()
    }

    /*
    private fun crear(Nombre: String, Tipo: String, validadorPeso: Boolean): Boolean {
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

     */

}
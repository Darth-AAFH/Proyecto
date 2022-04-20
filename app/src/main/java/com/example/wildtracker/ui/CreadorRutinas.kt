package com.example.wildtracker.ui

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import java.util.*
import com.example.wildtracker.R

class CreadorRutinas : AppCompatActivity() {

    var editTextNombre3: EditText ?= null
    private var buttonCrear2: Button?= null
    var listViewEjerciciosHechos: ListView?= null
    var listViewEjerciciosRutina: ListView?= null

    var listado: ArrayList<String>? = null
    var listado2 = ArrayList<String>()
    var datos = ArrayList<String>()
    var contadorMax = 0

    var arregloRutinas = Array<rutina?>(51){null}
    var validadorVacia = true

    private fun CargarTabla() { //Funcion que trae la tabla
        val datos1 = ArrayList<String>()

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Tipo, Peso from Ejercicios"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        if (c.moveToFirst()) {
            do { //Mientras se haya movido de posicion va a tomar todos los datos de esa fila
                val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getInt(3)
                datos1.add(linea)
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        listado = datos1
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewEjerciciosHechos!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_rutinas)

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
            val indice = listado2[position].split(" ").toTypedArray()[0].toInt() //Toma el id del ejercicio
            var cadena = ""

            val helper = LocalDB(this, "Demo", null, 1) //Abre la base de datos
            val db: SQLiteDatabase = helper.getWritableDatabase()

            val sql ="select Id, Nombre, Tipo, Peso from Ejercicios where Id = "+indice
            val c = db.rawQuery(sql, null)
            if (c.moveToFirst()) {
                val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getInt(3)
                cadena = linea //Toma la linea completa del ejercicio
            }
            c.close()
            db.close() //Cierra la base de datos

            val posicion = listado2.indexOf(cadena) //Toma la posición del ejercicio en el array list

            listado2.removeAt(posicion) //Remueve el ejercicio del array list
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
            listViewEjerciciosRutina!!.setAdapter(adapter)

            contadorMax -= 1
        }

        listViewEjerciciosHechos!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(contadorMax >= 10){
                Toast.makeText(this, "Solo se pueden agregar 10 ejercicios a la rutina", Toast.LENGTH_SHORT).show()
            }else {
                val indice = listado!![position].split(" ").toTypedArray()[0].toInt()

                val helper = LocalDB(this, "Demo", null, 1)
                val db: SQLiteDatabase = helper.getWritableDatabase()

                val sql = "select Id, Nombre, Tipo, Peso from Ejercicios where Id = " + indice
                val c = db.rawQuery(sql, null)
                if (c.moveToFirst()) {
                    val linea =
                        c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getInt(
                            3
                        )
                    datos.add(linea)
                }
                c.close()
                db.close()

                listado2 = datos
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
                listViewEjerciciosRutina!!.setAdapter(adapter)

                contadorMax += 1
            }
        }
    }

    private fun crear(Nombre: String): Boolean{
        var contadorMax = 1; var idFinal = 0

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id from Rutinas"
        val c = db.rawQuery(sql, null) //Se crea un cursor
        if (c.moveToFirst()) {
            do {
                contadorMax += 1 ////////Toma la cantidad de rutinas que hayan
                idFinal = c.getInt(0) ///Toma el id de la última rutina
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        var confirmacion = false
        if(contadorMax <= 50){
            var nombre = Nombre

            if(nombre == "")
                nombre = "Rutina " + (idFinal + 1)

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
                arregloRutinas[contadorMax] = rutina(idFinal + 1, nombre, cadena)
                guardarLocal(arregloRutinas[contadorMax]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

    private fun guardarLocal(Rutina: rutina) {
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

        try {
            val c = ContentValues() //Se llena con los valores tomados de las editText
            c.put("Id", Rutina.id)
            c.put("Nombre", Rutina.nombre)
            c.put("Ejercicios", Rutina.ejercicios)
            db.insert("RUTINAS", null, c)
            db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
            Toast.makeText(this, "Se ha guardado la rutina", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Ha habido un error", Toast.LENGTH_SHORT).show()
        }
    }

}
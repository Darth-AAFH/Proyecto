package com.example.wildtracker.ui

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import java.util.ArrayList
import com.example.wildtracker.R

@Suppress("NAME_SHADOWING")
class EditorRutinas : AppCompatActivity() {

    var editTextNombre4: EditText ?= null
    private var buttonGuardar2: Button?= null
    private var buttonBorrar2: Button?= null
    var listViewEjerciciosHechos2: ListView?= null
    var listViewEjerciciosRutina2: ListView?= null

    var listado: ArrayList<String>? = null
    var listado2 = ArrayList<String>()
    var datos = ArrayList<String>()
    var contadorMax = 0

    var num = 0

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
        listViewEjerciciosHechos2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private fun CargarRutina(arreglo: Array<String?>) { //Funcion que trae la tabla
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Tipo, Peso from Ejercicios"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        for (i in 0 until arreglo.size) {//recorre todo el arreglo
            c.moveToFirst()
            do {
                if (c.getInt(0) == arreglo[i]!!.toInt()) { //si un ejercicio de la lista completa esta en la de la rutina
                    val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getInt(3)
                    datos.add(linea) //Lo va a añadir a la linea de la listView
                    contadorMax += 1
                }
            } while (c.moveToNext())
        }
        c.close()
        db.close()

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
        }

        editTextNombre4 = findViewById<View>(R.id.editTextNombre4) as EditText
        buttonGuardar2 = findViewById(R.id.buttonGuardar2)
        buttonBorrar2 = findViewById(R.id.buttonBorrar2)
        listViewEjerciciosHechos2 = findViewById(R.id.listViewEjerciciosHechos2)
        listViewEjerciciosRutina2 = findViewById(R.id.listViewEjerciciosRutina2)

        CargarTabla()

        var nombre = ""; var ejercicios = ""

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase()

        val sql ="select Nombre, Ejercicios from Rutinas where Id = "+num
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            nombre =  c.getString(0)
            ejercicios =  c.getString(1)
        }
        c.close()
        db.close()

        editTextNombre4!!.setText(nombre)

        val arreglo: Array<String?>
        arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios
        CargarRutina(arreglo)

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
            listViewEjerciciosRutina2!!.setAdapter(adapter)

            contadorMax -= 1
        }

        listViewEjerciciosHechos2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(contadorMax >= 10){
                Toast.makeText(this, "Una rutina solo puede tener 10 ejercicios", Toast.LENGTH_SHORT).show()
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
                listViewEjerciciosRutina2!!.setAdapter(adapter)

                contadorMax += 1
            }
        }
    }

    private fun guardar(Id: Int, Nombre: String): Boolean{
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

        var confirmacion = false
        if(cadena == "]"){
            Toast.makeText(this, "La rutina debe contener al menos un ejercicio", Toast.LENGTH_SHORT).show()
        }else {
            val helper = LocalDB(this, "Demo", null, 1)
            val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

            val sql = "update Rutinas set Nombre='" + Nombre + "',Ejercicios='" + cadena + "' where Id="+Id///////
            db.execSQL(sql) //Se actualizan los datos por los nuevos que introdujo el usuario
            db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
            Toast.makeText(this, "Se ha modificado la rutina", Toast.LENGTH_SHORT).show()
            confirmacion = true
        }

        return confirmacion
    }

    private fun borrar(Id: Int) {
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

        val sql = "delete from Rutinas where Id="+Id //Se hace la consulta para borrar el registro
        db.execSQL(sql) //Se ejecuta la consulta
        db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
        Toast.makeText(this, "Se ha BORRADO la rutina", Toast.LENGTH_SHORT).show()
    }

}
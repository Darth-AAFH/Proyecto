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

    var num = 0; var nombre: String? = null; var ejercicios: String? = null; var nivel = 0

    var listaEjercicios = ArrayList<String>()

    private fun CargarEjercicios() { //Funcion que trae los ejercicios
        listado = MainActivity.listaEjercicios1
        listado!!.addAll(MainActivity.listaEjercicios2)
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewEjerciciosHechos2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private fun CargarEjerciciosDeRutina(arreglo: Array<String?>) { //Funcion que trae la tabla
        ////////////////////////////////////////////////////////////////////////////////////////////
        //var cadenaAux = MainActivity.listaEjercicios1.toString() //guardar toda la lista de ejercicios
        //cadenaAux += MainActivity.listaEjercicios2.toString()
        //var arregloLE = cadenaAux.split(" ")!!.toTypedArray() //la separa por caracteres

        //for(i in arregloLE){

        //}
        //tomar un dato de la lista y separarlo por cadenas y comparar el primer dato que seria el id
        for(i in 0 until arreglo.size) {
            for(j in 0 until listado!!.size) {
                var cadenaAux = listado!![i]
                val arregloAux: Array<String?> = ejercicios!!.split(" ")!!.toTypedArray()
                var idAux = arregloAux[0]!!.toInt()

                if (idAux == arreglo[j]!!.toInt()) {
                    datos.add(cadenaAux)
                }
            }
        }

        listado2 = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
        listViewEjerciciosRutina2!!.setAdapter(adapter) //La rutina se adapta en la text view

            //si es igual pues añadir la cadenaAux

        /*for(i in 0 until arreglo.size) {
            for(j in 0 until listado!!.size){
                if(arreglo[i] == ){
                    //listado!!.get(j)
                }
            }
        }

         */

        ////////////////////////////////////////////////////////////////////////////////////////////
        /*
        var cadenaAux = MainActivity.listaEjercicios1.toString() //guardar solo los numeros con un for y un if is digit
        var arregloLE = cadenaAux.split(" ")!!.toTypedArray()
        var contadorAux = 0
        for (i in 0 until arreglo.size) {
            for(j in 0 until arregloLE.size) {
                if ((arreglo[i]!!.toString()) == arregloLE[j]!!.toString()){
                    //val linea = MainActivity.listaEjercicios.get()
                    //datos.add(linea) //Lo va a añadir a la linea de la listView
                    contadorMax += 1
                }
            }
            contadorAux += 1
        }

        listado2 = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
        listViewEjerciciosRutina2!!.setAdapter(adapter) //La rutina se adapta en la text view

         */
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
        arreglo = ejercicios!!.split(",")!!.toTypedArray() //toma los ids de los ejercicios
        CargarEjerciciosDeRutina(arreglo)

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
                linea = this.listado!![position].split(" ").toTypedArray()[0]; linea += " | " //va a tomar el indice
                linea += this.listado!![position].split(" | ").toTypedArray()[1]; linea += " | " //nombre
                linea += this.listado!![position].split(" | ").toTypedArray()[2]; linea += " | " //tipo
                linea += this.listado!![position].split(" | ").toTypedArray()[3] //y peso del ejercicio seleccionado

                datos.add(linea) //y lo va a añadir a
                listado2 = datos //el listado de los ejercicios de rutina
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
                listViewEjerciciosRutina2!!.setAdapter(adapter) //después lo va a poner en la listView

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
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

    var listado: java.util.ArrayList<String>? = null

    /*private fun CargarTabla(){
        val datos1 = ArrayList<String>()

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Tipo, Peso from Ejercicios"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        if (c.moveToFirst()) {
            do { //Mientras se haya movido de posicion va a tomar todos los datos de esa fila
                val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2) + " | " + c.getInt(3)
                if(c.getInt(0) > 15) { //Validador para no mostrar los ejericicios dados de alta por defecto
                    datos1.add(linea)
                }
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        listado = datos1
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewEjercicios!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

     */

    var listaEjercicios = ArrayList<String>()

    private fun CargarTabla(){
        listaEjercicios.sort()
        listado = listaEjercicios
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewEjercicios!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicios)

        val b = intent.extras
        if (b != null) {
            listaEjercicios = b.getStringArrayList("LE") as ArrayList<String>
        }

        listViewEjercicios = findViewById(R.id.listViewEjercicios)

        Toast.makeText(this, "Click para editar ejercicio", Toast.LENGTH_SHORT).show()
        CargarTabla()

        listViewEjercicios!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val num = this.listado!![position].split(" ").toTypedArray()[0].toInt()
            val intent = Intent(this@VerEjercicios, EditorEjercicios::class.java)
            intent.putExtra("Num", num)
            startActivity(intent)
        }
    }

}
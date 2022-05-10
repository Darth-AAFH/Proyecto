package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore

class VerEjercicios : AppCompatActivity() {

    var listViewEjercicios: ListView?= null

    var listado: java.util.ArrayList<String>? = null
    var listaEjercicios = ArrayList<String>()

    private val db = FirebaseFirestore.getInstance()
    var cadena = "["

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

        MainActivity.user?.let { usuario -> //para verificar que el ejercicio no esté en alguna rutina
            db.collection("users").document(usuario).collection("rutinas") //abre la base de datos
                .get().addOnSuccessListener {
                    for(rutina in it){ //para cada rutina
                        cadena += rutina.get("ejercicios").toString() //toma los ids de los ejercicios
                        cadena += ","
                    }
                }
        }
        CargarTabla()
        Toast.makeText(this, "Click para editar ejercicio", Toast.LENGTH_SHORT).show()

        listViewEjercicios!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val num = this.listado!![position].split(" ").toTypedArray()[0].toInt()
            val nombre = this.listado!![position].split(" | ").toTypedArray()[1]
            val tipo = this.listado!![position].split(" | ").toTypedArray()[2]
            val peso = this.listado!![position].split(" | ").toTypedArray()[3]

            val intent = Intent(this@VerEjercicios, EditorEjercicios::class.java)
            intent.putExtra("Num", num)
            intent.putExtra("Nombre", nombre)
            intent.putExtra("Tipo", tipo)
            intent.putExtra("Peso", peso)
            intent.putExtra("Cadena", cadena)
            startActivity(intent)
        }
    }

}
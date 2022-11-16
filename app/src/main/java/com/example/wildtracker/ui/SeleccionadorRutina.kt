package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SeleccionadorRutina : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    var listViewRutinas2: ListView?= null

    var dia = 0; var mes  = 0; var ano = 0

    private fun CargarListas(){ //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)

            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)

            MainActivity.validadorAcomodo = false
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinas)
        listViewRutinas2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionador_rutina)

        val b = intent.extras //b toma la fecha en que se trabajara la rutina
        if (b != null) {
            dia = b.getInt("dia")
            mes = b.getInt("mes")
            ano = b.getInt("ano")
        }

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)

        CargarListas()
        Toast.makeText(this, "Seleccione un rutina", Toast.LENGTH_SHORT).show()

        listViewRutinas2!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            val nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]
            val nivelAux = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[2]

            val arreglo: Array<String?>
            arreglo = nivelAux.split(" ").toTypedArray()

            val fecha = dia.toString() + "-" + mes.toString() + "-" + ano.toString()

            MainActivity.user?.let{ usuario ->
                db.collection("users").document(usuario).collection("rutinasAtrabajar")
                    .document(fecha).set(
                        hashMapOf(
                            "dia" to dia,
                            "mes" to mes,
                            "ano" to ano,
                            "idRutina" to num,
                            "nombre" to nombre
                        )
                    )
            }

            var sdf = SimpleDateFormat("dd")
            val diaHoy = sdf.format(Date()) //se obtiene el dia actual
            sdf = SimpleDateFormat("MM")
            val mesHoy = sdf.format(Date()) //se obtiene el mes actual
            sdf = SimpleDateFormat("yyyy")
            val anoHoy = sdf.format(Date()) //se obiene el año actual
            var fechaHoy: String

            val diaHoy2 = diaHoy.toInt()
            val mesHoy2 = mesHoy.toInt()
            fechaHoy = diaHoy2.toString() + "-" + mesHoy2.toString() + "-" + anoHoy

            if(fecha == fechaHoy) {
                MainActivity.listaRutinasATrabajar.clear()
                var cadena = num.toString() + " | " + nombre + " | Fecha: " + dia.toString() + "-" + mes.toString() + "-" + ano.toString()
                MainActivity.listaRutinasATrabajar.add(cadena)
            }

            Toast.makeText(this, "Se añadio correctamente la rutina", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SeleccionadorRutina, SeguimientoActivity::class.java)
            startActivity(intent)
        }
    }
}
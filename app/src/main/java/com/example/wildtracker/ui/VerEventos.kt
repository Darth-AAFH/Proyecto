package com.example.wildtracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.R
import kotlinx.android.synthetic.main.activity_plantillas.*
import kotlinx.android.synthetic.main.activity_ver_eventos.*

class VerEventos : AppCompatActivity() {

    var dia = 0; var mes = 0; var ano = 0

    var listaEventosVista = listOf<eventos>()

    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_eventos)

        val b = intent.extras //b toma los datos mandados de la otra activity
        if (b != null) {
            dia = b.getInt("Dia")
            mes = b.getInt("Mes")
            ano = b.getInt("Ano")
        }

        initToolbar()

        cargarEventos()
        mostrarLista()
    }

    fun cargarEventos(){
        var cont = 0

        for(i in MainActivity.listaEventos1){
            if(i == (dia.toString() + "-" + mes.toString() + "-" + ano.toString())){
                val nombre = MainActivity.listaRutinasATrabajarAux[cont].split(" | ").toTypedArray()[1]
                val descripcion = MainActivity.listaRutinasATrabajarAux[cont].split(" | ").toTypedArray()[2]

                var acomodo: eventos
                acomodo = eventos(nombre, descripcion, R.drawable.rutina)

                val listaEventosVistaAux = listOf(acomodo)
                listaEventosVista += listaEventosVistaAux
            }
            cont++
        }

        cont = 0
        for(i in MainActivity.listaEventos2){
            val arreglo: Array<String?>
            arreglo = i.split(",").toTypedArray() //toma las fechas separadas

            for(j in arreglo){
                if(j == (dia.toString() + "-" + mes.toString() + "-" + ano.toString())){
                    val nombre = MainActivity.listaAllMetas[cont].split(" | ").toTypedArray()[0]
                    val descripcion = MainActivity.listaAllMetas[cont].split(" | ").toTypedArray()[2]

                    var acomodo: eventos
                    acomodo = eventos(nombre, descripcion, R.drawable.excersice_icon)

                    val listaEventosVistaAux = listOf(acomodo)
                    listaEventosVista += listaEventosVistaAux
                }
            }

            cont++
        }

    }

    fun mostrarLista(){
        if(listaEventosVista.isEmpty()){
            textViewAyudaEventos.visibility = View.VISIBLE
        }

        val adapter = eventosAdapter(this, listaEventosVista)
        listViewEventos!!.adapter = adapter
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = dia.toString() + "-" + mes.toString() + "-" + ano.toString()
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerlayout)!!
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }
}
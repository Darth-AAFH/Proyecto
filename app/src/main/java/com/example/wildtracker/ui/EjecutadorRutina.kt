package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wildtracker.R
import java.util.*
import kotlin.math.roundToInt

@Suppress("NAME_SHADOWING")
class EjecutadorRutina : AppCompatActivity() {

    var textViewActividadEnFoco: TextView?= null; var textViewReloj: TextView?= null
    var buttonParar: Button?= null; var buttonPausar: Button?= null; var buttonSaltar: Button?= null
    var listViewEjerciciosPorHacer: ListView?= null; var buttonSiguiente: Button?= null

    var listado = ArrayList<String>()
    var datos = ArrayList<String>()

    lateinit var timer: Timer
    lateinit var trabajoTimer: TimerTask
    var tiempo = 0.0
    var pausar = false; var firstclick = false; var parar = false
    var final = false

    var num = 0
    var puntos = 0

    private fun CargarRutina(arreglo: Array<String?>) { //Funcion que trae la rutina
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre from Ejercicios"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        for (i in 0 until arreglo.size) { //recorre todo el arreglo
            c.moveToFirst()
            do{
                if (arreglo[i]!!.toInt() == c.getInt(0)) { //si un ejercicio de la lista completa esta en la de la rutina
                    val linea = c.getString(1)
                    datos.add(linea) //Lo va a añadir a la linea de la listView
                }
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        datos.add(" ")
        listado = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
        listViewEjerciciosPorHacer!!.setAdapter(adapter) //La rutina se adapta en la text view
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejecutador_rutina)

        val b = intent.extras //b toma el id de la rutina a editar
        if (b != null) {
            num = b.getInt("Num")
        }

        textViewActividadEnFoco = findViewById(R.id.textViewActividadEnFoco)
        textViewReloj = findViewById(R.id.textViewReloj)
        buttonParar = findViewById(R.id.buttonParar)
        buttonPausar = findViewById(R.id.buttonPausar)
        buttonSaltar = findViewById(R.id.buttonSaltar)
        listViewEjerciciosPorHacer = findViewById(R.id.listViewEjerciciosPorHacer)
        buttonSiguiente = findViewById(R.id.buttonSiguiente)

        var ejercicios = ""

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase()

        val sql ="select Ejercicios from Rutinas where Id = "+num
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            ejercicios =  c.getString(0)
        }
        c.close()
        db.close()

        val arreglo: Array<String?>
        arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios
        CargarRutina(arreglo)
        Toast.makeText(this, "Presione > para iniciar", Toast.LENGTH_SHORT).show()
        timer = Timer()

        buttonParar!!.setOnClickListener{
            if(firstclick == true && parar == false) {
                if(pausar == true)
                    trabajoTimer.cancel()
                val alertaParar = AlertDialog.Builder(this)

                alertaParar.setTitle("Detener Rutina")
                alertaParar.setMessage("¿Quiere detener la rutina?")

                alertaParar.setPositiveButton("Sí") { dialogInterface, i ->
                    trabajoTimer.cancel()
                    parar = true
                    fin(false)
                }

                alertaParar.setNegativeButton("Cancelar") { dialogInterface, i ->
                    if(pausar == true)
                        inciarTimer()
                }
                alertaParar.show()
            }
        }

        buttonPausar!!.setOnClickListener{
            if(firstclick == false){
                val ejercicio1 = listado[0]
                textViewActividadEnFoco!!.setText(""+ejercicio1)
                listado.removeAt(0) //Remueve el primer ejercicio de la lista
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                listViewEjerciciosPorHacer!!.setAdapter(adapter)

                if(listado[0] == " ") {
                    buttonSiguiente!!.setText("Terminar")
                    final = true
                }

                firstclick = true
            }

            if(parar != true) {
                if (pausar == false) {
                    buttonPausar!!.background = getDrawable(R.drawable.ic_pause)
                    inciarTimer()
                    pausar = true
                } else {
                    buttonPausar!!.background = getDrawable(R.drawable.ic_play)
                    trabajoTimer.cancel()
                    pausar = false
                }
            }
        }

        buttonSaltar!!.setOnClickListener{
            if(firstclick == true && parar == false) {
                if(pausar == true)
                    trabajoTimer.cancel()

                val alertaSaltar = AlertDialog.Builder(this)
                alertaSaltar.setTitle("Saltar Ejercicio")
                alertaSaltar.setMessage("¿Quiere saltar el ejercicio?")

                alertaSaltar.setPositiveButton("Sí") { dialogInterface, i ->
                    val alertaIncompleto = AlertDialog.Builder(this)
                    alertaIncompleto.setTitle("Ejercicio Incompleto")
                    alertaIncompleto.setMessage("¿Comenzó el ejercicio?")

                    alertaIncompleto.setPositiveButton("Sí") { dialogInterface, i ->
                        puntos += 1
                        if(final != true) {
                            val ejercicio = listado[0]
                            textViewActividadEnFoco!!.setText("" + ejercicio)
                            listado.removeAt(0) //Remueve el primer ejercicio de la lista
                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                            listViewEjerciciosPorHacer!!.setAdapter(adapter)

                            if (listado[0] == " ") {
                                buttonSiguiente!!.setText("Terminar")
                                final = true
                            }

                            if(pausar == true)
                                inciarTimer()
                        }else {
                            parar = true
                            trabajoTimer.cancel()
                            fin(true)
                        }
                    }

                    alertaIncompleto.setNegativeButton("No") { dialogInterface, i ->
                        if(final != true) {
                            val ejercicio = listado[0]
                            textViewActividadEnFoco!!.setText("" + ejercicio)
                            listado.removeAt(0) //Remueve el primer ejercicio de la lista
                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                            listViewEjerciciosPorHacer!!.setAdapter(adapter)

                            if (listado[0] == " ") {
                                buttonSiguiente!!.setText("Terminar")
                                final = true
                            }

                            if(pausar == true)
                                inciarTimer()
                        }else{
                            parar = true
                            trabajoTimer.cancel()
                            fin(true)
                        }
                    }
                    alertaIncompleto.show()
                }

                alertaSaltar.setNegativeButton("Cancelar") { dialogInterface, i ->
                    if(pausar == true)
                        inciarTimer()
                }
                alertaSaltar.show()
            }
        }

        buttonSiguiente!!.setOnClickListener{
            if(firstclick == true && parar == false){
                puntos += 2

                if(final == false) {
                    val ejercicio = listado[0]
                    textViewActividadEnFoco!!.setText("" + ejercicio)
                    listado.removeAt(0) //Remueve el primer ejercicio de la lista
                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                    listViewEjerciciosPorHacer!!.setAdapter(adapter)

                    if (listado[0] == " ") {
                        buttonSiguiente!!.setText("Terminar")
                        final = true
                    }
                }else{
                    parar = true
                    trabajoTimer.cancel()
                    fin(true)
                }
            }
        }
    }

    private fun inciarTimer() {
        trabajoTimer = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tiempo++
                    textViewReloj!!.setText(tiempoATexto())
                }
            }
        }
        timer.scheduleAtFixedRate(trabajoTimer, 0, 1000)
    }

    private fun tiempoATexto(): String{
        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60
        return formatTime(horas, minutos, segundos)
    }

    private fun formatTime (horas: Int, minutos: Int, segundos: Int): String {
        return String.format("%02d", horas) + " : " + String.format("%02d",minutos) + " : " + String.format("%02d", segundos)
    }

    private fun fin(completado: Boolean){
        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60

        if(completado == true)
            Toast.makeText(this, "Felicidades, completó la rutina!!", Toast.LENGTH_LONG).show()
        Toast.makeText(this, "Usted obtuvo: "+puntos+" puntos", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "y: "+horas+" horas, "+minutos+" minutos y "+segundos+" segundos", Toast.LENGTH_SHORT).show()


    }///////////////////////mandar tiempo y puntos a la base de datos

}

package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wildtracker.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
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

    private val db = FirebaseFirestore.getInstance()
    var num = 0; var nombre  = ""; var puntos = 0; var xp = 0; var nivel = 0; var ejercicios = ""
    var terminar2 = false
    var horasE = 0; var minutosE = 0; var segundosE = 0; var puntosE = 0 //E de extras

    private fun CargarRutina(arreglo: Array<String?>) { //Funcion que trae la rutina
        for(i in 0 until arreglo.size) { //va a recorrer los ejercicios de la rutina
            for (j in MainActivity.listaEjercicios) { //para todos los ejercicios
                val id = j.split(" ").toTypedArray()[0] //toma el id
                if(arreglo[i] == id){ //si esta el ejercicio en la rutina
                    val nombre = j.split(" | ").toTypedArray()[1] //va a tomar el nombre
                    datos.add(nombre) //y lo agrega para la list view
                }
            }
        }

        for(i in MainActivity.listaRutinas){
            var arreglo: Array<String?>
            arreglo = i.split(" ").toTypedArray()
            if(arreglo[0]!!.toInt() == num) {
                val cadena = i.split("Nivel:").toTypedArray()[1]
                arreglo[0] = cadena.split(" ").toTypedArray()[1]
                nivel = arreglo[0]!!.toInt()
            }
        }

        datos.add(" ")
        listado = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
        listViewEjerciciosPorHacer!!.setAdapter(adapter) //La rutina se adapta en la text view
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejecutador_rutina)

        val b = intent.extras //b toma el id de la rutina a editar
        if (b != null) {
            num = b.getInt("Num")
            nombre = b.getString("Nombre").toString()
            xp = b.getInt("XP")
        }

        textViewActividadEnFoco = findViewById(R.id.textViewActividadEnFoco)
        textViewReloj = findViewById(R.id.textViewReloj)
        buttonParar = findViewById(R.id.buttonParar)
        buttonPausar = findViewById(R.id.buttonPausar)
        buttonSaltar = findViewById(R.id.buttonSaltar)
        listViewEjerciciosPorHacer = findViewById(R.id.listViewEjerciciosPorHacer)
        buttonSiguiente = findViewById(R.id.buttonSiguiente)

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        MainActivity.user?.let { usuario -> //para traer el tiempo del día
            db.collection("users").document(usuario).collection("tiempos") //abre la base de datos
                .get().addOnSuccessListener {
                    for(tiempo in it){ //para cada rutina

                        val idFecha = (tiempo.get("idFecha")).toString() //toma el id
                        if(currentDate == idFecha){ //si es igual a la fecha actual
                            puntosE = (tiempo.get("puntos") as Long).toInt()
                            horasE = (tiempo.get("horas") as Long).toInt() //va a traer los datos de la bd
                            minutosE = (tiempo.get("minutos") as Long).toInt()
                            segundosE = (tiempo.get("segundos") as Long).toInt()
                        }

                    }
                }
        }

        for(i in MainActivity.listaRutinas){ //recorre todas las rutinas
            val id = i.split(" ").toTypedArray()[0] //toma el id
            if(id == num.toString()){ //al encontrar la seleccionada
                ejercicios = i.split(" | ").toTypedArray()[3] //tomara los ejercicios de esta
            }
        }

        val arreglo: Array<String?>
        arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios
        CargarRutina(arreglo) //carga la rutina

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
            if(terminar2){
                terminar()
                terminar2 = false
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

    @SuppressLint("SetTextI18n")
    private fun fin(completado: Boolean){
        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60

        if(completado == true) { //en caso de SI completar la rutina
            Toast.makeText(this, "Felicidades, completó la rutina!!", Toast.LENGTH_LONG).show() //se mandara mensaje de felicidades

            xp += 1 //se le suma uno de experiencia
            if((2*nivel)+1 == xp && nivel != 100){ //si la xp llega iguala a lo que pide el nivel
                xp = 0 //se reinicia la xp
                MainActivity.user?.let{ usuario ->
                    db.collection("users").document(usuario).collection("rutinas").document(num.toString()).set(
                        hashMapOf( //y se actualiza el ejercicio con un nivel más
                            "id" to num,
                            "nombre" to nombre,
                            "nivel" to nivel+1,
                            "ejercicios" to ejercicios,
                            "xp" to 0
                        )
                    )
                }

                var linea: String
                linea = num.toString() + " | " + nombre + " | Nivel: "+ nivel + " | " + ejercicios //se toma la linea de rutina

                val posicion: Int
                posicion = MainActivity.listaRutinas.indexOf(linea) //se obtiene la posición de la rutina en la lista

                val nuevoNivel = nivel + 1
                linea = num.toString() + " | " + nombre + " | Nivel: "+ nuevoNivel + " | " + ejercicios
                MainActivity.listaRutinas.set(posicion, linea) //se actualiza con el nuevo nivel

                Toast.makeText(this, "Felicidades, la rutina subio a nivel "+ nuevoNivel +"!!!!", Toast.LENGTH_LONG).show() //se le hace saber al usuario que subio el nivel
                if(nuevoNivel == 100)
                    Toast.makeText(this, "Felicidades ha halcanzado el nivel máximo en la rútina, gracias por ejercitarse <3", Toast.LENGTH_LONG).show()
            }else {
                MainActivity.user?.let { usuario -> //si no se tiene la xp suficiente para subir de nivel
                    db.collection("users").document(usuario).collection("rutinas")
                        .document(num.toString()).set(
                        hashMapOf( //se guardan todos los datos en la base de datos como ya estaban pero sumando uno en xp
                            "id" to num,
                            "nombre" to nombre,
                            "nivel" to nivel,
                            "ejercicios" to ejercicios,
                            "xp" to xp
                        )
                    )
                }
            }

            val alertaEjExtra = AlertDialog.Builder(this) //se crea la alarma

            alertaEjExtra.setTitle("Puntos extra!") //y se ponen los textos para preguntar si quiere un ejercicio extra
            alertaEjExtra.setMessage("¿Quiere hacer un ejercicio extra?")

            alertaEjExtra.setPositiveButton("Sí") { dialogInterface, i -> //en caso de que sí
                val idRandom = (0..15).random() //toma un numero random

                for (j in MainActivity.listaEjercicios) { //para todos los ejercicios
                    val id = j.split(" ").toTypedArray()[0] //toma el id
                    if(idRandom == id.toInt()){ //si esta el ejercicio en la rutina
                        val nombre = j.split(" | ").toTypedArray()[1] //va a tomar el nombre
                        textViewActividadEnFoco!!.setText("" + nombre) //y lo va a poner en la actividad en foco
                    }
                }

                inciarTimer()
                terminar2 = true
            }

            alertaEjExtra.setNegativeButton("No") { dialogInterface, i -> //en caso de que no
                mandarPuntos(puntos, horas, minutos, segundos)

                val intent = Intent(this@EjecutadorRutina, EjercicioActivity::class.java)
                startActivity(intent) //te va a devolver a ejercicio
            }
            alertaEjExtra.show() //se muestra la alerta
        }else{ //en caso que no
            val intent = Intent(this@EjecutadorRutina, EjercicioActivity::class.java)
            startActivity(intent) //te va a devolver a ejercicio

            mandarPuntos(puntos, horas, minutos, segundos)
        }
    }

    private fun terminar() {
        trabajoTimer.cancel()
        puntos *= 2

        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60

        mandarPuntos(puntos, horas, minutos, segundos)

        val intent = Intent(this@EjecutadorRutina, EjercicioActivity::class.java)
        startActivity(intent) //te va a devolver a ejercicio
    }

    @SuppressLint("SimpleDateFormat")
    private fun mandarPuntos(puntos: Int, horasF: Int, minutosF: Int, segundosF: Int){
        puntosE += puntos

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

        horasE += horasF
        minutosE += minutosF
        segundosE += segundosF

        while(segundosE >= 60){
            segundosE -= 60
            minutosE += 1
        }

        while(minutosE >= 60){
            minutosE -= 60
            horasE += 1
        }

        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("tiempos")
                .document(currentDate).set(
                    hashMapOf(
                        "puntos" to puntosE,
                        "idFecha" to currentDate,
                        "horas" to horasE,
                        "minutos" to minutosE,
                        "segundos" to segundosE
                    )
                )
        }
    }

}

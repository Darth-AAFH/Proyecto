package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wildtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


@Suppress("NAME_SHADOWING")
class EjecutadorRutina : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private lateinit var builder: AlertDialog.Builder //Dialogo de alerta para interactuar en el activity
    private lateinit var VideosEjercicios: RecyclerView
    private var youtubeVideos = Vector<youTubeVideos>()
    var textViewActividadEnFoco: TextView? = null;
    var textViewReloj: TextView? = null
    var buttonParar: Button? = null;
    var buttonPausar: Button? = null;
    var buttonSaltar: Button? = null
    var listViewEjerciciosPorHacer: ListView? = null;
    var buttonSiguiente: Button? = null;
    var buttonAyuda: Button? = null
    private lateinit var photofile: File
    var listado = ArrayList<String>()
    var datos = ArrayList<String>()

    companion object {
        const val REQUEST_CODE = 42
        var Ismeta = false
        var AlertaMostrado = false

    }

    lateinit var timer: Timer
    lateinit var trabajoTimer: TimerTask
    var tiempo = 0.0
    var pausar = false;
    var firstclick = false;
    var parar = false
    var final = false

    private val db = FirebaseFirestore.getInstance()
    var num = 0;
    var nombre = "";
    var puntos = 0;
    var xp = 0;
    var nivel = 0;
    var ejercicios = "";
    var meta = ""
    var fecha = "";
    var duracion = ""
    var ultDia = 0;
    var ultMes = 0;
    var ultAno = 0
    var terminar2 = false
    var horasE = 0;
    var minutosE = 0;
    var segundosE = 0;
    var puntosE = 0 //E de extras
    var horasR = 0;
    var minutosR = 0;
    var segundosR = 0 //R de rutina

    private fun CargarRutina(arreglo: Array<String?>) { //Funcion que trae la rutina
        for (i in 0 until arreglo.size) { //va a recorrer los ejercicios de la rutina
            for (j in MainActivity.listaEjercicios) { //para todos los ejercicios
                val id = j.split(" ").toTypedArray()[0] //toma el id
                if (arreglo[i] == id) { //si esta el ejercicio en la rutina
                    val nombre = j.split(" | ").toTypedArray()[1] //va a tomar el nombre
                    datos.add(nombre) //y lo agrega para la list view
                }
            }
        }

        for (i in MainActivity.listaRutinas) { //para obtener el nivel de la rutina
            var arreglo: Array<String?>
            arreglo = i.split(" ").toTypedArray()
            if (arreglo[0]!!.toInt() == num) {
                val cadena = i.split("Nivel:").toTypedArray()[1]
                arreglo[0] = cadena.split(" ").toTypedArray()[1]
                nivel = arreglo[0]!!.toInt()
            }
        }

        datos.add(" ")
        listado = datos
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
        listViewEjerciciosPorHacer!!.adapter = adapter //La rutina se adapta en la text view
    }

    var Name = "";
    var birthDay = "";
    var email = "";
    var peso = ""
    var puntosTotales: Int = 0

    data class puntosTotalesClass(
        val Name: String? = "",
        val birthDay: String? = "",
        val email: String = "",
        val puntosTotales: Int? = 0,
        val peso: String = ""
    )

    private fun puntosTotalesFun(bool: Boolean) {
        if (bool) {
            var puntosTaux: puntosTotalesClass
            MainActivity.user?.let { usuario -> //para cargar los puntos totales del usuario
                db.collection("users").document(usuario).get().addOnSuccessListener {
                    GlobalScope.launch(Dispatchers.IO) {
                        val puntosTDocument = Firebase.firestore
                            .collection("users")
                            .document(usuario) //se toma la ruta del documento del usuario

                        puntosTaux = puntosTDocument.get().await()
                            .toObject(puntosTotalesClass::class.java)!! //se toma puntosTotales

                        withContext(Dispatchers.Main) {
                            Name = puntosTaux.Name!!
                            birthDay = puntosTaux.birthDay!!
                            email = puntosTaux.email
                            puntosTotales =
                                puntosTaux.puntosTotales!! //se guarda en la variable global
                            peso = puntosTaux.peso
                            if (puntosTotales == null) { //en caso de que sea null se guarda como 0
                                puntosTotales = 0
                            }
                        }
                    }
                }
            }
        } else {
            MainActivity.user?.let { usuario -> //se abre la base de datos para subir los datos
                db.collection("users").document(usuario).update(
                    mapOf(
                        "Name" to Name, //se colocan los datos a mandar
                        "birthDay" to birthDay,
                        "email" to email,
                        "puntosTotales" to puntosTotales,
                        "peso" to peso
                    )
                )
            }
        }
    }

    private fun BorrarRutinaDelDia(fecha: String) {
        if (fecha != "0") {
            val linea: String
            linea = num.toString() + " | " + nombre + " | Fecha:" + fecha

            val posicion: Int
            posicion = MainActivity.listaRutinasATrabajar.indexOf(linea)

            MainActivity.listaRutinasATrabajar.removeAt(posicion + 1)

            val posicion2: Int
            posicion2 = MainActivity.listaRutinasATrabajarAux.indexOf(linea)
            MainActivity.listaRutinasATrabajarAux.removeAt(posicion2 + 1)

            MainActivity.user?.let { usuario ->
                db.collection("users").document(usuario).collection("rutinasAtrabajar")
                    .document(fecha).delete()
            }
        }
    }

    var Peso = false;
    var Repeticion = false;
    var Tiempo = false
    var D1 = false;
    var D2 = false;
    var D3 = false;
    var D4 = false
    var D5 = false;
    var D6 = false;
    var D7 = false
    var diaF = 0;
    var mesF = 0;
    var anoF = 0
    var DatoInicial = 0;
    var DatoFinal = 0
    var diaSeg = 0;
    var mesSeg = 0;
    var anoSeg = 0

    private fun BorrarMetaDelDia(paso: Int) {
        if (paso == 1) {
            MainActivity.user?.let { usuario -> //para cargar la meta
                db.collection("users").document(usuario)
                    .collection("metas")
                    .document(nombre) //abre la base de datos con la meta que ya tenemos
                    .get().addOnSuccessListener {
                        Peso = it.get("peso") as Boolean
                        Repeticion = it.get("repeticion") as Boolean
                        Tiempo = it.get("tiempo") as Boolean
                        D1 = it.get("lunes") as Boolean
                        D2 = it.get("martes") as Boolean
                        D3 = it.get("miercoles") as Boolean
                        D4 = it.get("jueves") as Boolean
                        D5 = it.get("viernes") as Boolean
                        D6 = it.get("sabado") as Boolean
                        D7 = it.get("domingo") as Boolean
                        diaF = (it.get("diaFinal") as Long).toInt()
                        mesF = (it.get("mesFinal") as Long).toInt()
                        anoF = (it.get("anoFinal") as Long).toInt()
                        DatoInicial = (it.get("datoInicial") as Long).toInt()
                        DatoFinal = (it.get("datoFinal") as Long).toInt()
                        diaSeg = (it.get("diaSeg") as Long).toInt()
                        mesSeg = (it.get("mesSeg") as Long).toInt()
                        anoSeg = (it.get("anoSeg") as Long).toInt()
                    }
            }
        } else {
            MainActivity.user?.let { usuario ->
                db.collection("users").document(usuario).collection("metas")
                    .document(nombre).set(
                        hashMapOf(
                            "nombre" to nombre,
                            "peso" to Peso,
                            "repeticion" to Repeticion,
                            "tiempo" to Tiempo,
                            "lunes" to D1,
                            "martes" to D2,
                            "miercoles" to D3,
                            "jueves" to D4,
                            "viernes" to D5,
                            "sabado" to D6,
                            "domingo" to D7,
                            "diaFinal" to diaF,
                            "mesFinal" to mesF,
                            "anoFinal" to anoF,
                            "datoInicial" to DatoInicial,
                            "datoFinal" to DatoFinal,
                            "diaSeg" to diaSeg, //dia de seguimiento (para llevar un orden a los datos que se le suman)
                            "mesSeg" to mesSeg,
                            "anoSeg" to anoSeg,
                            "ultDia" to ultDia, //ultima fecha en que se trabajo la meta (esto para que no la repita dos veces en un día)
                            "ultMes" to ultMes,
                            "ultAno" to ultAno
                        )
                    )
            }

            var linea: String //linea de texto para borrar la meta de la lista de metas
            linea = nombre + " | " //se le agrega el nombre

            if (D1) {
                linea += "lun "
            } //se le agregan los dias a trabajar
            if (D2) {
                linea += "mar "
            }; if (D3) {
                linea += "mier "
            }; if (D4) {
                linea += "juev "
            }
            if (D5) {
                linea += "vier "
            }; if (D6) {
                linea += "sab "
            }; if (D7) {
                linea += "dom "
            }

            linea += "| " //se le agraga texto de formato

            if (Peso) { //con un texto que diferencie peso o repeticiones
                linea += "Levantar: " + DatoInicial + "kg"
            } else {
                if (Repeticion) {
                    linea += "Repeticiones: " + DatoInicial
                } else {
                    linea += "Completar: "

                    var minutos = DatoInicial
                    var horas = 0

                    while (minutos >= 60) { //se obtienen las horas
                        minutos -= 60
                        horas += 1
                    }

                    if (horas != 0) {
                        linea += horas //se le agrega el tiempo con horas
                        linea += "hr "
                    }
                    linea += minutos //se le agregan los minutos
                    linea += "min"
                }
            }

            //se le agrega la fecha de finalizacion
            linea += " | Fecha de finalización: "
            linea += diaF; linea += "-"; linea += mesF; linea += "-"; linea += anoF

            val posicion: Int
            posicion = MainActivity.listaMetas.indexOf(linea) //la busca en la lista
            MainActivity.listaMetas.removeAt(posicion) //y la borra
            MainActivity.listaMetasDates.removeAt(posicion)
        }
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejecutador_rutina)
        createNotificationChannel2()
        VideosEjercicios = findViewById(R.id.VideosEjercicios)
        VideosEjercicios.layoutManager = LinearLayoutManager(this)
        val BotonMostrar: Button = (findViewById(R.id.buttonMostrarVideo))

        youtubeVideos.add(
            youTubeVideos(
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www" +
                        ".youtube.com/embed/VRKdOsad3HQ\" frameborder=\"0\" allowfullscreen></iframe>"
            )
        )

        val videoAdapter = VideoAdapter(youtubeVideos)
        BotonMostrar.setOnClickListener {
            if (VideosEjercicios.isVisible) {
                VideosEjercicios.isVisible = false
            } else {
                VideosEjercicios.isVisible = true
                VideosEjercicios.adapter = videoAdapter
            }

        }


        val b = intent.extras //b toma el id de la rutina a trabajar
        if (b != null) {
            num = b.getInt("Num")
            nombre = b.getString("Nombre").toString()
            xp = b.getInt("XP")
            fecha = b.getString("Fecha").toString()
            meta = b.getString("Meta").toString()
            ultDia = b.getInt("Dia")
            ultMes = b.getInt("Mes")
            ultAno = b.getInt("Ano")
            duracion = b.getString("Tiempo").toString()
        }

        textViewActividadEnFoco = findViewById(R.id.textViewActividadEnFoco)
        textViewReloj = findViewById(R.id.textViewReloj)
        buttonParar = findViewById(R.id.buttonParar)
        buttonPausar = findViewById(R.id.buttonPausar)
        buttonSaltar = findViewById(R.id.buttonSaltar)
        listViewEjerciciosPorHacer = findViewById(R.id.listViewEjerciciosPorHacer)
        buttonSiguiente = findViewById(R.id.buttonSiguiente)
        buttonAyuda = findViewById(R.id.buttonAyuda)
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())
        MainActivity.user?.let { usuario -> //para traer el tiempo del día
            db.collection("users").document(usuario).collection("tiempos") //abre la base de datos
                .get().addOnSuccessListener {
                    for (tiempo in it) { //para cada fecha

                        val idFecha = (tiempo.get("idFecha")).toString() //toma el id
                        if (currentDate == idFecha) { //si es igual a la fecha actual
                            puntosE = (tiempo.get("puntos") as Long).toInt()
                            horasE =
                                (tiempo.get("horas") as Long).toInt() //va a traer los datos de la bd
                            minutosE = (tiempo.get("minutos") as Long).toInt()
                            segundosE = (tiempo.get("segundos") as Long).toInt()
                        }

                    }
                }
        }

        MainActivity.user?.let { usuario -> //para traer el tiempo de la rutina
            db.collection("users").document(usuario).collection("rutinas") //abre la base de datos
                .get().addOnSuccessListener {
                    for (rutinas in it) { //para cada rutina

                        val idRutina = (rutinas.get("id")).toString() //toma el id de la rutina
                        if (num.toString() == idRutina) { //hasta encontrar el de la rutina del que se esta tranajando
                            horasR =
                                (rutinas.get("horas") as Long).toInt() //va a traer los datos de la bd
                            minutosR = (rutinas.get("minutos") as Long).toInt()
                            segundosR = (rutinas.get("segundos") as Long).toInt()
                        }

                    }
                }
        }

        while (segundosR >= 60) {
            segundosR -= 60
            minutosR += 1
        }

        while (minutosR >= 60) {
            minutosR -= 60
            horasR += 1
        }

        for (i in MainActivity.listaRutinas) { //recorre todas las rutinas
            val id = i.split(" ").toTypedArray()[0] //toma el id
            if (id == num.toString()) { //al encontrar la seleccionada
                ejercicios = i.split(" | ").toTypedArray()[3] //tomara los ejercicios de esta
            }
        }

        val arreglo: Array<String?>
        arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios
        CargarRutina(arreglo) //carga la rutina

        puntosTotalesFun(true)

        if (num != -1) {
            BorrarRutinaDelDia(fecha)
        } else {
            BorrarMetaDelDia(1)
            textViewActividadEnFoco!!.text = nombre + ", " + meta
            listado.add(" ")
        }

        Toast.makeText(this, "Presione > para iniciar", Toast.LENGTH_SHORT).show()
        timer = Timer()

        buttonParar!!.setOnClickListener {
            if (firstclick == true && parar == false) {
                if (pausar == true)
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
                    if (pausar == true)
                        inciarTimer()
                }
                alertaParar.show()
            }
        }

        buttonPausar!!.setOnClickListener {
            if (firstclick == false) {
                if (num != -1) {
                    val ejercicio1 = listado[0]
                    textViewActividadEnFoco!!.text = "" + ejercicio1
                    listado.removeAt(0) //Remueve el primer ejercicio de la lista
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                    listViewEjerciciosPorHacer!!.adapter = adapter
                }

                if (listado[0] == " ") {
                    buttonSiguiente!!.text = "Terminar"
                    final = true
                }

                firstclick = true
            }

            if (parar != true) {
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

        buttonSaltar!!.setOnClickListener {
            if (firstclick == true && parar == false) {
                if (pausar == true)
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
                        if (final != true) {
                            val ejercicio = listado[0]
                            textViewActividadEnFoco!!.text = "" + ejercicio
                            listado.removeAt(0) //Remueve el primer ejercicio de la lista
                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                this,
                                android.R.layout.simple_list_item_1,
                                listado
                            )
                            listViewEjerciciosPorHacer!!.adapter = adapter

                            if (listado[0] == " ") {
                                buttonSiguiente!!.text = "Terminar"
                                final = true
                            }

                            if (pausar == true)
                                inciarTimer()
                        } else {
                            parar = true
                            trabajoTimer.cancel()
                            fin(true)
                        }
                    }

                    alertaIncompleto.setNegativeButton("No") { dialogInterface, i ->
                        if (final != true) {
                            val ejercicio = listado[0]
                            textViewActividadEnFoco!!.text = "" + ejercicio
                            listado.removeAt(0) //Remueve el primer ejercicio de la lista
                            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                                this,
                                android.R.layout.simple_list_item_1,
                                listado
                            )
                            listViewEjerciciosPorHacer!!.adapter = adapter

                            if (listado[0] == " ") {
                                buttonSiguiente!!.text = "Terminar"
                                final = true
                            }

                            if (pausar == true)
                                inciarTimer()
                        } else {
                            parar = true
                            trabajoTimer.cancel()
                            fin(true)
                        }
                    }
                    alertaIncompleto.show()
                }

                alertaSaltar.setNegativeButton("Cancelar") { dialogInterface, i ->
                    if (pausar == true)
                        inciarTimer()
                }
                alertaSaltar.show()
            }
        }


        buttonSiguiente!!.setOnClickListener {
            if (firstclick == true && parar == false) {
                puntos += 2

                if (final == false) {
                    val ejercicio = listado[0]
                    textViewActividadEnFoco!!.text = "" + ejercicio

                    //Funcion para cargar video del siguiente ejercicio aqui
                    cargarVideo(ejercicio)

                    listado.removeAt(0) //Remueve el primer ejercicio de la lista
                    val adapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado)
                    listViewEjerciciosPorHacer!!.adapter = adapter

                    if (listado[0] == " ") {
                        buttonSiguiente!!.text = "Terminar"
                        final = true
                    }
                } else {
                    parar = true
                    trabajoTimer.cancel()
                    fin(true)
                }
            }
            if (terminar2) {
                //Añadir a firebase el dato del ultimo día trabajado de ejercicio
                terminar()
                terminar2 = false
            }
        }


        buttonAyuda!!.setOnClickListener {

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val myScrollView: View = inflater.inflate(R.layout.ayuda_layout, null, false)

            // Initializing a blank textview so that we can just append a text later

            val progresDialog = ProgressDialog(this)
            // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
            var perfilGet: String
            progresDialog.setMessage("Cargando Datos")
            progresDialog.setCancelable(false)
            progresDialog.show()

            builder = AlertDialog.Builder(this)
            builder.setView(R.layout.ayuda_layout)
            builder.setTitle("Ayuda")
                .setMessage("Guia de iconos")
                .setCancelable(true)
                .setNegativeButton("Ok") { dialogInterface, it -> //dialogInterface.cancel()
                    dialogInterface.dismiss()
                }
                .show()
//Toast.makeText(this,perfil,Toast.LENGTH_SHORT).show()
            progresDialog.dismiss()


        }


    }

    private fun createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = ("Chanel1")
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Chanel1", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notificacionTrabajarOtrosEjercicios() {
        val intent = Intent(this, CreadorRutinas::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "Chanel1")
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("Sugerencia de rutina")
            .setContentText("Realizó la rutina 15 veces, le recomendamos trabajar otros ejercicios")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(6, builder.build())
        }
    }


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    private fun cargarVideo(ejercicio: String) {
        youtubeVideos.removeAllElements()
        when (ejercicio) {
            "Sentadillas" -> youtubeVideos.add(
                youTubeVideos(
                    "<iframe width=\"100%\" height=\"100%\" src=\"https://www" +
                            ".youtube.com/embed/VRKdOsad3HQ\" frameborder=\"0\" allowfullscreen></iframe>"
                )
            )
            "Saltos de tijera" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/2P2_TjzqGLQ?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Elevación de talones" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/igRyr2jWRTs?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Abdominales" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/CwhxepX7aR8?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Plancha" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/OuFDY0fwlvk?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Escaladores" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/lD_gfTofg4A?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Dominadas" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/A2thchjoWkI?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Press de pecho" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/NfJqRwAlZY8?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Peso muerto" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/gBY5Se4apXc?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Punches" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/b0ZeY-j5T1w?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Dips de tríceps" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/EtPHEAOIxUU?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Press de hombros" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/UFKqIoAbUBg?controls=0&amp;start=9\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Elevaciones laterales" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/dT6Q3NHtSjw?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Flexiones" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/nOFk-PYAvwI?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
            "Burpees" -> youtubeVideos.add(youTubeVideos("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/auBLPXO8Fww?controls=0\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"))
        }
    }

    private fun inciarTimer() {
        trabajoTimer = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    tiempo++
                    textViewReloj!!.text = tiempoATexto()

                    //para las metas con tiempo
                    if (duracion != "0") {
                        if (tiempoATexto() == duracion) { //al llegar al tiempo seleccionado
                            final = true
                            parar = true
                            trabajoTimer.cancel()
                            fin(true) //se parara la rutina
                        }
                    }

                }
            }
        }
        timer.scheduleAtFixedRate(trabajoTimer, 0, 1000)
    }

    private fun tiempoATexto(): String {
        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60
        return formatTime(horas, minutos, segundos)
    }

    private fun formatTime(horas: Int, minutos: Int, segundos: Int): String {
        return String.format("%02d", horas) + " : " + String.format(
            "%02d",
            minutos
        ) + " : " + String.format("%02d", segundos)
    }

    @SuppressLint("SetTextI18n")
    private fun fin(completado: Boolean) {
        if (num != -1) {
            val redondeo = tiempo.roundToInt()
            val horas = redondeo % 86400 / 3600
            val minutos = redondeo % 86400 % 3600 / 60
            val segundos = redondeo % 86400 % 3600 % 60

            if (completado == true) { //en caso de SI completar la rutina
                Toast.makeText(this, "Felicidades, completó la rutina!!", Toast.LENGTH_LONG)
                    .show() //se mandara mensaje de felicidades

                xp += 1 //se le suma uno de experiencia

                if (nivel == 3 && xp == 6) {
                    Toast.makeText(
                        this,
                        "Realizó la rutina 15 veces, le recomendamos trabajar otros ejercicios",
                        Toast.LENGTH_LONG
                    ).show() //se mandara mensaje de felicidades
                    notificacionTrabajarOtrosEjercicios()
                }

                if ((2 * nivel) + 1 == xp && nivel != 100) { //si la xp llega iguala a lo que pide el nivel
                    xp = 0 //se reinicia la xp
                    MainActivity.user?.let { usuario ->
                        db.collection("users").document(usuario).collection("rutinas")
                            .document(num.toString()).set(
                                hashMapOf( //y se actualiza el ejercicio con un nivel más
                                    "id" to num,
                                    "nombre" to nombre,
                                    "nivel" to nivel + 1,
                                    "ejercicios" to ejercicios,
                                    "xp" to 0,
                                    "horas" to horasR + horas,
                                    "minutos" to minutosR + minutos,
                                    "segundos" to segundosR + segundos
                                )
                            )
                    }

                    var linea: String
                    linea =
                        num.toString() + " | " + nombre + " | Nivel: " + nivel + " | " + ejercicios //se toma la linea de rutina

                    val posicion: Int
                    posicion =
                        MainActivity.listaRutinas.indexOf(linea) //se obtiene la posición de la rutina en la lista

                    val nuevoNivel = nivel + 1
                    linea =
                        num.toString() + " | " + nombre + " | Nivel: " + nuevoNivel + " | " + ejercicios
                    MainActivity.listaRutinas.set(posicion, linea) //se actualiza con el nuevo nivel

                    val linea2 = num.toString() + " | " + nombre + " | Nivel: " + nuevoNivel
                    MainActivity.listaRutinasVista.set(
                        posicion,
                        linea2
                    ) //se actualiza con el nuevo nivel

                    Toast.makeText(
                        this,
                        "Felicidades, la rutina subio a nivel " + nuevoNivel + "!!!!",
                        Toast.LENGTH_LONG
                    ).show() //se le hace saber al usuario que subio el nivel
                    if (nuevoNivel == 100)
                        Toast.makeText(
                            this,
                            "Felicidades ha halcanzado el nivel máximo en la rútina, gracias por ejercitarse <3",
                            Toast.LENGTH_LONG
                        ).show()
                } else {
                    MainActivity.user?.let { usuario -> //si no se tiene la xp suficiente para subir de nivel
                        db.collection("users").document(usuario).collection("rutinas")
                            .document(num.toString()).set(
                                hashMapOf( //se guardan todos los datos en la base de datos como ya estaban pero sumando uno en xp
                                    "id" to num,
                                    "nombre" to nombre,
                                    "nivel" to nivel,
                                    "ejercicios" to ejercicios,
                                    "xp" to xp,
                                    "horas" to horasR + horas,
                                    "minutos" to minutosR + minutos,
                                    "segundos" to segundosR + segundos
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
                        if (idRandom == id.toInt()) { //si esta el ejercicio en la rutina
                            val nombre = j.split(" | ").toTypedArray()[1] //va a tomar el nombre
                            textViewActividadEnFoco!!.text =
                                "" + nombre //y lo va a poner en la actividad en foco
                        }
                    }

                    inciarTimer()
                    terminar2 = true
                }
                alertaEjExtra.setNegativeButton("No") { dialogInterface, i -> //en caso de que no
                    foto(puntos, horas, minutos, segundos)
                }

                alertaEjExtra.show() //se muestra la alerta
            } else { //en caso que no
                foto(puntos, horas, minutos, segundos)
            }
        } else {
            if (completado == true) {
                Toast.makeText(this, "Felicidades, completó la meta!!", Toast.LENGTH_LONG).show()
                fotoMeta(nombre)
            }

        }
        if (AlertaMostrado) {

        }

    }

    private fun terminar() {
        trabajoTimer.cancel()
        puntos *= 2

        val redondeo = tiempo.roundToInt()
        val horas = redondeo % 86400 / 3600
        val minutos = redondeo % 86400 % 3600 / 60
        val segundos = redondeo % 86400 % 3600 % 60
        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd")
        MainActivity.user?.let { usuario ->
            db.collection("users").document(MainActivity.user!!).collection("tiempos2")
                .document("currentDate").set(
                    hashMapOf(
                        "Fecha" to date
                    )
                )
        }

        if (num != -1) {
            foto(puntos, horas, minutos, segundos)
        } else {
            mandarPuntos(16, horas, minutos, segundos)
            val intent = Intent(
                this@EjecutadorRutina,
                EjercicioActivity::class.java
            ) // Cuando se termina te manda a los ejercicios
            startActivity(intent)
        }
    }

    private fun foto(puntos: Int, horas: Int, minutos: Int, segundos: Int) {
        val alertaFoto = AlertDialog.Builder(this) //Alerta para la foto

        alertaFoto.setTitle("Registro de entrenamiento") //Se ponen los textos para preguntar si quiere un ejercicio extra
        alertaFoto.setMessage("¿Deseas tomarte una foto como registro de ejercicio para la rutina $nombre?")
        //nombre como ruta para la rutina en firebase
        alertaFoto.setPositiveButton("Si") { dialogInterface, i ->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //Hacer validacion de la toma de foto para analizar si ya existe una foto

            //Formato original de la foto
            //  photofile = getPhotoFile("foto_${nombre}_${SimpleDateFormat("yyyMMdd").format(Date())}")

            photofile = getPhotoFile("foto_${nombre}_1-")

            val fileProvider =
                FileProvider.getUriForFile(this, "com.example.wildtracker.fileprovider", photofile)

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }

            mandarPuntos(puntos, horas, minutos, segundos)
        }
        alertaFoto.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.cancel()

            mandarPuntos(puntos, horas, minutos, segundos)
            val intent = Intent(
                this@EjecutadorRutina,
                EjercicioActivity::class.java
            ) // Cuando se termina te manda a los ejercicios
            startActivity(intent)
        }

        alertaFoto.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun mandarPuntos(
        puntos: Int,
        horasF: Int,
        minutosF: Int,
        segundosF: Int
    ) { //F de finales
        puntosE += puntos
        puntosTotales += puntos
        puntosTotalesFun(false)

        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())

        horasE += horasF
        minutosE += minutosF
        segundosE += segundosF

        while (segundosE >= 60) {
            segundosE -= 60
            minutosE += 1
        }

        while (minutosE >= 60) {
            minutosE -= 60
            horasE += 1
        }

        MainActivity.user?.let { usuario ->
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
        MainActivity.user?.let { usuario ->
            db.collection("users").document(usuario).collection("UltimaFechaTrabajada")
                .document("UltimaFechaTrabajada").set(
                    hashMapOf(
                        "UltimaFechaTrabajada" to currentDate,
                    )
                )
        }
    }

    private fun getPhotoFile(fileName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photofile.absolutePath)
            val fileProvider =
                FileProvider.getUriForFile(this, "com.example.wildtracker.fileprovider", photofile)
            uploadFile(fileProvider)
            // foto?.setImageBitmap(takenImage)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun uploadFile(takenImage: Uri) {

        val userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (takenImage != null) {
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()
            //Se guarda la rutina en la ruta de cada rutina

// Child references can also take paths
// spaceRef now points to "images/space.jpg
// imagesRef still points to "images"
            storage = Firebase.storage
            var storageRef = storage.reference

            var imagesRef: StorageReference? = storageRef.child("images")
            var spaceRef =
                storageRef.child("UsersTakenPictures/$userID/Rutina_$nombre/${photofile.name}")
            val FotoSeparada = photofile.name.split("-").toTypedArray()
            // Toast.makeText(this,"SEPARADA:${FotoSeparada[0]}",Toast.LENGTH_SHORT).show()
            if (Ismeta) {
                listAllFilesMetas(userID, FotoSeparada[0], takenImage)
            } else {
                listAllFiles(userID, FotoSeparada[0], takenImage)
            }
            if (num == -1) {
                BorrarMetaDelDia(2)
            }
            terminar()


            /*var imageRef =
                FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_$nombre/${photofile.name}")
            imageRef.putFile(takenImage)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                  //  Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                }*/

            //Toast.makeText(this, "Subida", Toast.LENGTH_LONG).show()
        }

        val intent = Intent(
            this@EjecutadorRutina,
            EjercicioActivity::class.java
        ) // Cuando se termina te manda a los ejercicios
        startActivity(intent)
    }


    fun listAllFiles(userID: String, name: String, takenImage: Uri) {
        val storage = FirebaseStorage.getInstance()
        // Listamos las fotos en firebase

        val listRef = storage.reference.child("UsersTakenPictures/$userID/Rutina_$nombre/")
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                var Renombrar = false
                var FotoFB = ""

                for (item in listResult.items) {
                    // All the items under listRef.
                    val FotoFirebaseSeparada = (item.name.split("-").toTypedArray())
                    //Toast.makeText(this,"SEPARADA:${FotoFirebaseSeparada[0]}",Toast.LENGTH_SHORT).show()

                    var FotoFB = FotoFirebaseSeparada[0]
                    if (FotoFB == name) {
                        //   Toast.makeText(this,"YA EXISTE UN ARCHIVO",Toast.LENGTH_SHORT).show()
                        Renombrar = true
                    }

                    // Toast.makeText(this,"Foto item:"+FotoFirebaseSeparada[0],Toast.LENGTH_SHORT).show()
                }
                if (Renombrar) {
                    FotoFB = ("foto_${nombre}_2")
                    Toast.makeText(
                        this,
                        "Se ha actualizado la foto de registro de actividad",
                        Toast.LENGTH_SHORT
                    ).show()
                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_$nombre/${FotoFB}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->

                        }
                        .addOnFailureListener { p0 ->
                        }
                        .addOnProgressListener { p0 ->
                        }
                } else {
                    val FotoListInicial = (photofile.name.split("-").toTypedArray())
                    val FotoInicial = FotoListInicial[0]

                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_$nombre/${FotoInicial}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->

                            Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT)
                                .show()
                            //  Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                        }
                        .addOnFailureListener { p0 ->

                            Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener {

                Toast.makeText(this, "No se que paso", Toast.LENGTH_SHORT).show()
            }


    }

    private fun fotoMeta(nombreMeta: String) {
        val alertaFoto = AlertDialog.Builder(this) //Alerta para la foto

        alertaFoto.setTitle("Registro de entrenamiento") //Se ponen los textos para preguntar si quiere un ejercicio extra
        alertaFoto.setMessage("¿Deseas tomarte una foto como registro de ejercicio para la meta ${nombreMeta}?")
        //nombre como ruta para la rutina en firebase
        alertaFoto.setPositiveButton("Si") { dialogInterface, i ->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //Hacer validacion de la toma de foto para analizar si ya existe una foto
            //Formato original de la foto
            //  photofile = getPhotoFile("foto_${nombre}_${SimpleDateFormat("yyyMMdd").format(Date())}")

            photofile = getPhotoFile("foto_${nombreMeta}_1-")
            Ismeta = true
            val fileProvider =
                FileProvider.getUriForFile(this, "com.example.wildtracker.fileprovider", photofile)

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CODE)
                AlertaMostrado = true
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }

            // mandarPuntos(puntos, horas, minutos, segundos)
        }
        alertaFoto.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.cancel()
            if (num == -1) {
                BorrarMetaDelDia(2)
            }
            terminar()
            // mandarPuntos(puntos, horas, minutos, segundos)
            val intent = Intent(
                this@EjecutadorRutina,
                EjercicioActivity::class.java
            ) // Cuando se termina te manda a los ejercicios
            startActivity(intent)

        }

        alertaFoto.show()


    }


    fun listAllFilesMetas(userID: String, name: String, takenImage: Uri) {
        val storage = FirebaseStorage.getInstance()
        // Listamos las fotos en firebase


        val listRef = storage.reference.child("UsersTakenPictures/$userID/Meta_$nombre/")
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                var Renombrar = false
                var FotoFB = ""
                for (item in listResult.items) {
                    // All the items under listRef.
                    val FotoFirebaseSeparada = (item.name.split("-").toTypedArray())
                    //Toast.makeText(this,"SEPARADA:${FotoFirebaseSeparada[0]}",Toast.LENGTH_SHORT).show()

                    var FotoFB = FotoFirebaseSeparada[0]
                    if (FotoFB == name) {
                        //   Toast.makeText(this,"YA EXISTE UN ARCHIVO",Toast.LENGTH_SHORT).show()
                        Renombrar = true
                    }

                    // Toast.makeText(this,"Foto item:"+FotoFirebaseSeparada[0],Toast.LENGTH_SHORT).show()
                }
                if (Renombrar) {
                    FotoFB = ("foto_${nombre}_2")
                    Toast.makeText(
                        this,
                        "Se ha actualizado la foto de registro de actividad",
                        Toast.LENGTH_SHORT
                    ).show()
                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_$nombre/${FotoFB}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->


                        }
                        .addOnFailureListener { p0 ->
                        }
                        .addOnProgressListener { p0 ->
                        }
                } else {
                    val FotoListInicial = (photofile.name.split("-").toTypedArray())
                    val FotoInicial = FotoListInicial[0]

                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_$nombre/${FotoInicial}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->

                            Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT)
                                .show()
                            //  Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                        }
                        .addOnFailureListener { p0 ->

                            Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener {

                Toast.makeText(this, "No se que paso", Toast.LENGTH_SHORT).show()
            }


    }


}
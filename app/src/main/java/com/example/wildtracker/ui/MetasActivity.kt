package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_metas.*
import java.io.File
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.*

class MetasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var photofile: File
    private lateinit var storage: FirebaseStorage
    private var nombreMeta:String = ""


    var editTextNombreMeta: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso: Switch?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchRepeticion: Switch?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchTiempo: Switch?= null
    var editTextInicio: EditText ?= null; var editTextFinal: EditText ?= null
    var d1: CheckBox ?= null; var d2: CheckBox ?= null; var d3: CheckBox ?= null; var d4: CheckBox ?= null
    var d5: CheckBox ?= null; var d6: CheckBox ?= null; var d7: CheckBox ?= null
    private var buttonGuardar: Button?= null

    private val db = FirebaseFirestore.getInstance()

    var dia = 0; var mes = 0; var ano = 0
    var D1 = false; var D2 = false; var D3 = false; var D4 = false; var D5 = false; var D6 = false; var D7 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)
        initToolbar()
        initNavigationView()
        createNotificationChannel()

        editTextNombreMeta = findViewById<View>(R.id.editTextNombreMeta) as EditText
        switchPeso = findViewById<View>(R.id.idSwitch1) as Switch
        switchRepeticion = findViewById<View>(R.id.idSwitch2) as Switch
        switchTiempo = findViewById<View>(R.id.idSwitch3) as Switch
        editTextInicio = findViewById<View>(R.id.editTextInicio) as EditText
        editTextFinal = findViewById<View>(R.id.editTextFinal) as EditText
        d1 = findViewById<CheckBox>(R.id.check1); d2 = findViewById<CheckBox>(R.id.check2)
        d3 = findViewById<CheckBox>(R.id.check3); d4 = findViewById<CheckBox>(R.id.check4)
        d5 = findViewById<CheckBox>(R.id.check5); d6 = findViewById<CheckBox>(R.id.check6)
        d7 = findViewById<CheckBox>(R.id.check7)
        buttonGuardar = findViewById(R.id.buttonGuardarMeta)

        editTextDate.setOnClickListener{ tomarFecha() }

        switchPeso!!.isChecked = true

        buttonGuardar!!.setOnClickListener{
            val nombre = editTextNombreMeta!!.text.toString(); val peso = switchPeso!!.isChecked
            val repeticion = switchRepeticion!!.isChecked; val tiempo = switchTiempo!!.isChecked

            var datoInicial = 0; var datoFinal = 0

            if(editTextInicio!!.text.toString() != ""){
                datoInicial = (editTextInicio!!.text.toString()).toInt()
            }
            if(editTextFinal!!.text.toString() != ""){
                datoFinal = (editTextFinal!!.text.toString()).toInt()
            }

            D1 = d1!!.isChecked; D2 = d2!!.isChecked; D3 = d3!!.isChecked; D4 = d4!!.isChecked; D5 = d5!!.isChecked; D6 = d6!!.isChecked; D7 = d7!!.isChecked

            if(datoFinal <= datoInicial) {
                Toast.makeText(this, "El dato final debe ser mayor al inicial", Toast.LENGTH_SHORT).show()
            }else{
                if(nombre == ""){
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }else{
                    if(datoFinal == 0 || datoInicial == 0){
                        Toast.makeText(this, "El dato no puede ser igual a 0", Toast.LENGTH_SHORT).show()
                    }else{
                        if(D1 == false && D2 == false && D3 == false && D4 == false && D5 == false && D6 == false && D7 == false){
                            Toast.makeText(this, "Debe de seleccionar por lo menos un día", Toast.LENGTH_LONG).show()
                        }else{
                            var sdf = SimpleDateFormat("dd")
                            val diaHoy = sdf.format(Date()) //se obtiene el dia actual
                            sdf = SimpleDateFormat("MM")
                            val mesHoy = sdf.format(Date()) //se obtiene el mes actual
                            sdf = SimpleDateFormat("yyyy")
                            val anoHoy = sdf.format(Date()) //se obiene el año actual

                            if(anoHoy.toInt() > ano){
                                Toast.makeText(this, "La fecha de finalizacion seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                            }else{
                                if(anoHoy.toInt() == ano){
                                    if(mesHoy.toInt() > mes){
                                        Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                                    }else{
                                        if(mesHoy.toInt() == mes){
                                            if(diaHoy.toInt() > dia){
                                               Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                                           }else{
                                               guardarMeta(nombre, peso, repeticion, tiempo, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                                          }
                                        }else{
                                            guardarMeta(nombre, peso, repeticion, tiempo, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                                        }
                                    }
                                }else{
                                    guardarMeta(nombre, peso, repeticion, tiempo, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                                }
                            }
                        }
                    }
                }
            }
            nombreMeta=nombre
        }

        switchPeso!!.setOnClickListener {
            switchRepeticion!!.isChecked = true
            editTextInicio!!.hint = "Cantidad de repeticiones iniciales"
            editTextFinal!!.hint = "Cantidad de repeticiones finales"

            if(switchPeso!!.isChecked){ //si se activa va a poner los otros dos switches en falso
                switchRepeticion!!.isChecked = false
                switchTiempo!!.isChecked = false
                editTextInicio!!.hint = "Peso inicial (kg)"
                editTextFinal!!.hint = "Peso inicial (kg)"
            }
        }

        switchRepeticion!!.setOnClickListener {
            switchTiempo!!.isChecked = true
            editTextInicio!!.hint = "Tiempo inicial (min)"
            editTextFinal!!.hint = "Tiempo final (min)"

            if(switchRepeticion!!.isChecked){
                switchPeso!!.isChecked = false
                switchTiempo!!.isChecked = false
                editTextInicio!!.hint = "Cantidad de repeticiones iniciales"
                editTextFinal!!.hint = "Cantidad de repeticiones finales"
            }
        }

        switchTiempo!!.setOnClickListener {
            switchPeso!!.isChecked = true
            editTextInicio!!.hint = "Peso inicial (kg)"
            editTextFinal!!.hint = "Peso inicial (kg)"

            if(switchTiempo!!.isChecked){
                switchPeso!!.isChecked = false
                switchRepeticion!!.isChecked = false
                editTextInicio!!.hint = "Tiempo inicial (min)"
                editTextFinal!!.hint = "Tiempo final (min)"
            }
        }
    }

    private fun tomarFecha() {
        val fechaFinal = SeleccionadorFecha{ day, month, year -> acomodarFecha(day, month, year) }
        fechaFinal.show(supportFragmentManager, "datePicker")
    }

    @SuppressLint("SetTextI18n")
    fun acomodarFecha(day: Int, month: Int, year: Int) {
        var month2 = month + 1
        editTextDate.setText("Fecha de finalización: $day del mes $month2 de $year")
        dia = day; mes = month2; ano = year
    }

    private fun guardarMeta(Nombre: String, Peso: Boolean, Repeticion: Boolean, Tiempo: Boolean, DatoInicial: Int, DatoFinal: Int, diaHoy: Int, mesHoy: Int, anoHoy: Int) {
        //guarda la meta en la bd
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("metas")
                .document(Nombre).set(
                    hashMapOf(
                        "nombre" to Nombre,
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
                        "diaFinal" to dia,
                        "mesFinal" to mes,
                        "anoFinal" to ano,
                        "datoInicial" to DatoInicial,
                        "datoFinal" to DatoFinal,
                        "diaSeg" to diaHoy, //dia de seguimiento (para llevar un orden a los datos que se le suman)
                        "mesSeg" to mesHoy,
                        "anoSeg" to anoHoy,
                        "ultDia" to diaHoy - 1, //ultima fecha en que se trabajo la meta (esto para que no la repita dos veces en un día)
                        "ultMes" to mesHoy,
                        "ultAno" to anoHoy
                    )
                )
        }

        Toast.makeText(this, "Se ha guardado la meta", Toast.LENGTH_LONG).show()

        guardarMetaCalendario(diaHoy, mesHoy, anoHoy, dia, mes, ano, D1, D2, D3, D4, D5, D6, D7)

        //guarda la meta en la lista (si es que se tiene que trabajar hoy)
        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt()
        val mesHoy = mesHoy2.toInt()
        val anoHoy = anoHoy2.toInt()

        val diaSemHoy = diaSemana(diaHoy, mesHoy, anoHoy) //se obtiene el numero de dia de la semana (lunes = 1, martes = 2, miercoles = 3, etc)

        var lun = 0; var mar = 0; var mier = 0; var juev = 0
        var vier = 0; var sab = 0; var dom = 0

        if(D1) lun = 1; if(D2) mar = 2; if(D3) mier = 3; if(D4) juev = 4
        if(D5) vier = 5; if(D6) sab = 6; if(D7) dom = 7

        if(diaSemHoy == lun || diaSemHoy == mar || diaSemHoy == mier || diaSemHoy == juev || diaSemHoy == vier || diaSemHoy == sab || diaSemHoy == dom){
            var cadena = Nombre //toma el nombre de la meta
            cadena += " | " //se le agraga texto de formato
            if(D1){cadena += "lun "} //se le agregan los dias a trabajar
            if(D2){cadena += "mar "}
            if(D3){cadena += "mier "}
            if(D4){cadena += "juev "}
            if(D5){cadena += "vier "}
            if(D6){cadena += "sab "}
            if(D7){cadena += "dom "}
            cadena += "| " //se le agraga texto de formato

            //se le agrega las repeticiones, peso o tiempo a trabajar
            if(Peso){ //con un texto que diferencie peso, repeticiones o tiempo
                cadena += "Levantar: "
                cadena += DatoInicial //se le agrega las repeticiones, peso o tiempo a trabajar
                cadena += "kg"
            }
            if(Repeticion){ //con un texto que diferencie peso, repeticiones o tiempo
                cadena += "Repeticiones: "
                cadena += DatoInicial //se le agrega las repeticiones, peso o tiempo a trabajar
            }
            if(Tiempo){ //con un texto que diferencie peso, repeticiones o tiempo
                cadena += "Completar: "

                var minutos = DatoInicial
                var horas = 0

                while(minutos >= 60){ //se obtienen las horas
                    minutos -= 60
                    horas += 1
                }

                if(horas != 0){
                    cadena += horas //se le agrega el tiempo con horas
                    cadena += "hr "
                }
                cadena += minutos //se le agregan los minutos
                cadena += "min"
            }
            //se le agrega la fecha de finalizacion
            cadena += " | Fecha de finalización: "
            cadena += dia; cadena += "-"; cadena += mes; cadena += "-"; cadena += ano

            MainActivity.listaMetas.add(cadena)
            MainActivity.listaMetasDates.add((diaHoy - 1).toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString())
            //Tomarse foto de inicio de la meta
        }

        var cadena = Nombre //toma el nombre de la meta
        cadena += " | " //se le agraga texto de formato
        if(D1){cadena += "lun "} //se le agregan los dias a trabajar
        if(D2){cadena += "mar "}
        if(D3){cadena += "mier "}
        if(D4){cadena += "juev "}
        if(D5){cadena += "vier "}
        if(D6){cadena += "sab "}
        if(D7){cadena += "dom "}
        cadena += "| " //se le agraga texto de formato

        //se le agrega las repeticiones, peso o tiempo a trabajar
        if(Peso){ //con un texto que diferencie peso, repeticiones o tiempo
            cadena += "Levantar: "
            cadena += DatoFinal //se le agrega las repeticiones, peso o tiempo a trabajar
            cadena += "kg"
        }
        if(Repeticion){ //con un texto que diferencie peso, repeticiones o tiempo
            cadena += "Repeticiones: "
            cadena += DatoFinal //se le agrega las repeticiones, peso o tiempo a trabajar
        }
        if(Tiempo){ //con un texto que diferencie peso, repeticiones o tiempo
            cadena += "Completar: "

            var minutos2 = DatoFinal
            var horas2 = 0

            while(minutos2 >= 60){ //se obtienen las horas
                minutos2 -= 60
                horas2 += 1
            }

            if(horas2 != 0){
                cadena += horas2 //se le agrega el tiempo con horas
                cadena += "hr "
            }
            cadena += minutos2 //se le agregan los minutos
            cadena += "min"
        }
        //se le agrega la fecha de finalizacion
        cadena += " | Fecha de finalización: "
        cadena += dia; cadena += "-"; cadena += mes; cadena += "-"; cadena += ano

        MainActivity.listaAllMetas.add(cadena)
        MainActivity.listaMetasAllDates.add((diaHoy - 1).toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString())

        MainActivity.listaMetasVista.add(cadena)
        MainActivity.listaMetasVistaDates.add((diaHoy - 1).toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString())

        cargarNotificaciones(true)
        //para poner en blanco las cajas de datos
        editTextNombreMeta!!.setText("")
        editTextDate!!.setText("")
        switchPeso!!.isChecked = true
        switchRepeticion!!.isChecked = false
        switchTiempo!!.isChecked = false
        editTextInicio!!.setText("")
        editTextFinal!!.setText("")
        d1!!.isChecked = false; d2!!.isChecked = false; d3!!.isChecked = false
        d4!!.isChecked = false; d5!!.isChecked = false; d6!!.isChecked = false
        d7!!.isChecked = false
        foto()
       /* val intent = Intent(this, EjercicioActivity::class.java)
        startActivity(intent)*/
    }

    fun guardarMetaCalendario(diaHoyAux: Int, mesHoyAux: Int, anoHoyAux: Int, dia: Int, mes: Int, ano: Int, D1: Boolean, D2: Boolean, D3: Boolean, D4: Boolean, D5: Boolean, D6: Boolean, D7: Boolean){
        var diasTotales: Int //primero se obtienen los dias totales
        var cadena = "["

        if(ano == anoHoyAux){ //se comparan los años
            if(mes == mesHoyAux){ //se comparan los meses
                diasTotales = dia - diaHoyAux //y si son los mismos solo se obtiene la diferencia entre los días
            }else{ //si no, se le suman los días del mes inicial
                if(mesHoyAux == 1 || mesHoyAux == 3 || mesHoyAux == 5 || mesHoyAux == 7 || mesHoyAux == 8 || mesHoyAux == 10 || mesHoyAux == 12){
                    diasTotales = 31 - diaHoyAux
                }else{
                    if(mesHoyAux == 2){
                        diasTotales = 28 - diaHoyAux
                    }else{
                        diasTotales = 30 - diaHoyAux
                    }
                }

                var i = mes - 1
                while (mesHoyAux != i) { //se le suman los días de los meses intermedios
                    diasTotales += if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                        31
                    } else {
                        if (i == 2) {
                            28
                        } else {
                            30
                        }
                    }
                    i--
                }

                diasTotales += dia //y se le suman los días del mes final
            }
        }else{ //para años diferentes
            diasTotales = (ano - anoHoyAux)*365 //se obtienen los años en días

            if(mes == mesHoyAux){ //si el mes es igual se resta o suma la diferencia de dias
                if(dia > diaHoyAux){
                    diasTotales += (dia - diaHoyAux)
                } else{
                    diasTotales -= (diaHoyAux - dia)
                }

            }else{
                if(mes > mesHoyAux){ //si el mes es mayor

                    //se le suman los dias del mes inicial
                    if(mesHoyAux == 1 || mesHoyAux == 3 || mesHoyAux == 5 || mesHoyAux == 7 || mesHoyAux == 8 || mesHoyAux == 10 || mesHoyAux == 12){
                        diasTotales = diasTotales + 31 - diaHoyAux
                    }else{
                        if(mesHoyAux == 2){
                            diasTotales = diasTotales + 28 - diaHoyAux
                        }else{
                            diasTotales = diasTotales + 30 - diaHoyAux
                        }
                    }

                    var i = mes - 1
                    while (mesHoyAux != i) { //se le suman los días de los meses intermedios
                        diasTotales += if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                            31
                        } else {
                            if (i == 2) {
                                28
                            } else {
                                30
                            }
                        }
                        i--
                    }

                    diasTotales += dia //y se le suman los días del mes final

                } else{ //y si el mes es menor
                    //se le restan los dias del mes inicial
                    diasTotales -= diaHoyAux

                    var i = mesHoyAux - 1
                    while (mes != i) { //se le restan los días de los meses intermedios
                        diasTotales -= if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) {
                            31
                        } else {
                            if (i == 2) {
                                28
                            } else {
                                30
                            }
                        }
                        i--
                    }

                    //y se le restan los días del mes final
                    if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
                        diasTotales = diasTotales - 31 + dia
                    }else{
                        if(mes == 2){
                            diasTotales = diasTotales - 28 + dia
                        }else{
                            diasTotales = diasTotales - 30 + dia
                        }
                    }
                }
            }
        }

        //se obtiene que dia de la semana es hoy para iniciar a traer las fechas
        var diaSem = diaSemana(diaHoyAux, mesHoyAux, anoHoyAux)
        var diaHoy = diaHoyAux; var mesHoy = mesHoyAux; var anoHoy = anoHoyAux

        for(i in diasTotales downTo 0){
            if(diaSem == 8){ //avanza los dias de la semana y reinicia si pasa el domingo
                diaSem = 1
            }

            //avanza los dias, meses y años
            if(mesHoy == 2){
                if(diaHoy > 28){
                    mesHoy += 1
                    diaHoy = 1
                }
            }else{
                if(mesHoy == 1 || mesHoy == 3 || mesHoy == 5 || mesHoy == 7 || mesHoy == 8 || mesHoy == 10 || mesHoy == 12){
                    if(diaHoy > 31){
                        mesHoy += 1
                        diaHoy = 1
                    }
                }else{
                    if(diaHoy > 30) {
                        mesHoy += 1
                        diaHoy = 1
                    }
                }
            }
            if(mesHoy > 12){
                anoHoy += 1
                mesHoy = 1
            }

            //coloca las fechas a trabajar en la lista de eventos segun corresponda
            if(diaSem == 1 && D1 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 2 && D2 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 3 && D3 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 4 && D4 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 5 && D5 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 6 && D6 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }
            if(diaSem == 7 && D7 == true){
                cadena += diaHoy.toString() + "-" + mesHoy.toString() + "-" + anoHoy.toString()+","
            }

            diaSem += 1
            diaHoy += 1
        }

        var contador = 0
        for(i in 0 until cadena.length){
            contador += 1
        }
        cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

        MainActivity.listaEventos2.add(cadena)
    }

    fun cargarNotificaciones(llamar:Boolean){
        val currentDate = Date()
        // convert date to calendar
        val FechaNotificacion = Calendar.getInstance()
        val FechaFinalizacion = Calendar.getInstance()
        FechaFinalizacion.timeInMillis = currentDate.time
        FechaNotificacion.timeInMillis = currentDate.time
        // manipulate date
        FechaFinalizacion.set(ano+0,mes-1,dia)
        FechaNotificacion.set(ano+0,mes-1,dia-14)//Seteamos la fecha de finalizacion y le restamos 2 semanas

        // convert calendar to date
        // convert calendar to date
        if(llamar){
       // val currentDatePlusOne = FechaNotificacion.time
        val curretnFechaNotificacion =  FechaNotificacion.timeInMillis
        val currentFechaFinalizacion = FechaFinalizacion.timeInMillis
        Log.d("FechaFinalizacion,",format(currentFechaFinalizacion.toString()))
        Log.d("FechaNotificacion,",format(curretnFechaNotificacion.toString()))
        //NotificacionRutinaPendiente(FechaNotificacion.time)
        NotificacionRutinaPendiente(curretnFechaNotificacion)
        }
        Log.d("FechaActual,",format(currentDate.toString()))

    }
    private fun diaSemana(dia: Int, mes: Int, ano: Int): Int {
        val c = Calendar.getInstance()
        c.set(ano, mes, dia)
        val diaSem =  c.get(Calendar.DAY_OF_WEEK)

        if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
            if(diaSem == 5) return 1; if(diaSem == 6) return 2; if(diaSem == 7) return 3
            if(diaSem == 1) return 4; if(diaSem == 2) return 5; if(diaSem == 3) return 6
            if(diaSem == 4) return 7
        }else{
            if(mes == 2){
                if(diaSem == 2) return 1; if(diaSem == 3) return 2; if(diaSem == 4) return 3
                if(diaSem == 5) return 4; if(diaSem == 6) return 5; if(diaSem == 7) return 6
                if(diaSem == 1) return 7
            }else{
                if(diaSem == 4) return 1; if(diaSem == 5) return 2; if(diaSem == 6) return 3
                if(diaSem == 7) return 4; if(diaSem == 1) return 5; if(diaSem == 2) return 6
                if(diaSem == 3) return 7
            }
        }
        return 0
    }

    /////////////////////////////////////////////////////

    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun NotificacionRutinaPendiente(FechaCadudar: Long) {

        val intent = Intent(applicationContext, com.example.wildtracker.ui.Notification::class.java)
        val title = "Rutina por caducar!!"
        val message = "Oye ${PerfilActivity.NombreUsuario} tienes una rutina que caduda pronto, hazla ahora!"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager =  getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = FechaCadudar
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            FechaCadudar,
            pendingIntent
        )
        Log.d("NextNotification",format(time.toString()))
    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance()
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year =calendar.get(Calendar.YEAR)
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Metas"
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerlayout)!!
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initNavigationView() {

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView: View = LayoutInflater.from(this)
            .inflate(R.layout.nav_header_main, navigationView, false)
        //Header para datos del usuario
        navigationView.removeHeaderView(headerView)
        //para actualizar los datos del header
        navigationView.addHeaderView(headerView)

        val tvUser: TextView = headerView.findViewById(R.id.tvUser)
        tvUser.text = MainActivity.user

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_inicio -> callInicioActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            
            R.id.nav_musica ->callMusica()
            R.id.nav_amigos ->callAmigosActivity()
            R.id.Settings->callAjustesActivity()
            R.id.nav_seguimiento->callSeguimientoActivity()
            R.id.nav_solicitudes-> callSolicitudesActivity()


        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    private fun callSolicitudesActivity() {
        val intent = Intent(this, SolicitudesActivity::class.java)
        startActivity(intent)    }
    private fun callAjustesActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
    private fun callAmigosActivity() {
        val intent = Intent(this, Activity_Amigos::class.java)
        startActivity(intent)
    }
    private fun callPerfilActivity() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun callInicioActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun callPlantillasActivity() {
        val intent = Intent(this, PlantillasActivity::class.java)
        startActivity(intent)
    }

    private fun callEjercicioActivity() {
        val intent = Intent(this, EjercicioActivity::class.java)
        startActivity(intent)
    }

    private fun callMapsActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun callSeguimientoActivity() {
        val intent = Intent(this, SeguimientoActivity::class.java)
        startActivity(intent)
    }

    private fun callRankingActivity() {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
    }

    private fun callChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
        startActivity(intent)
    }

    fun signOut() {

        LoginActivity.useremail = ""
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        deleteAppData()
    }
    private fun deleteAppData() {
        try {
            // clearing app data
            val packageName = applicationContext.packageName
            val runtime = Runtime.getRuntime()
            runtime.exec("pm clear $packageName")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun foto() {
        val alertaFoto = AlertDialog.Builder(this) //Alerta para la foto

        alertaFoto.setTitle("Registro de entrenamiento") //Se ponen los textos para preguntar si quiere un ejercicio extra
        alertaFoto.setMessage("¿Deseas tomarte una foto como registro de ejercicio para la meta?")
        //nombre como ruta para la rutina en firebase
        alertaFoto.setPositiveButton("Si"){dialogInterface, i ->
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //Hacer validacion de la toma de foto para analizar si ya existe una foto

            //Formato original de la foto
            //  photofile = getPhotoFile("foto_${nombre}_${SimpleDateFormat("yyyMMdd").format(Date())}")

            photofile = getPhotoFile("foto_${nombreMeta}_1-")

            val fileProvider = FileProvider.getUriForFile(this, "com.example.wildtracker.fileprovider", photofile)

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if (takePictureIntent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(takePictureIntent, EjecutadorRutina.REQUEST_CODE)
            } else {
                Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show()
            }

          //  mandarPuntos(puntos, horas, minutos, segundos)
        }
        alertaFoto.setNegativeButton("No"){dialogInterface, i ->
            dialogInterface.cancel()

            //mandarPuntos(puntos, horas, minutos, segundos)
            val intent = Intent(this, EjercicioActivity::class.java) // Cuando se termina te manda a los ejercicios
            startActivity(intent)
        }

        alertaFoto.show()
    }
    private fun getPhotoFile(fileName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override  fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (requestCode == EjecutadorRutina.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val takenImage = data?.extras?.get("data") as Bitmap
            val takenImage = BitmapFactory.decodeFile(photofile.absolutePath)
            val fileProvider = FileProvider.getUriForFile(this, "com.example.wildtracker.fileprovider", photofile)
            uploadFile(fileProvider)
            // foto?.setImageBitmap(takenImage)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun uploadFile(takenImage: Uri) {

        val userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (takenImage != null) {

            //Se guarda la rutina en la ruta de cada rutina

// Child references can also take paths
// spaceRef now points to "images/space.jpg
// imagesRef still points to "images"
            storage = Firebase.storage
            var storageRef = storage.reference

            var imagesRef: StorageReference? = storageRef.child("images")
            var spaceRef = storageRef.child("UsersTakenPictures/$userID/Meta_${nombreMeta}/${photofile.name}")
            val FotoSeparada = photofile.name.split("-").toTypedArray()
            Toast.makeText(this,"SEPARADA:${FotoSeparada[0]}",Toast.LENGTH_SHORT).show()
            listAllFiles(userID,FotoSeparada[0],takenImage)



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

      /*  val intent = Intent(this, EjercicioActivity::class.java) // Cuando se termina te manda a los ejercicios
        startActivity(intent)*/
    }
    fun listAllFiles(userID: String, name: String, takenImage: Uri) {
        val storage = FirebaseStorage.getInstance()
        // Listamos las fotos en firebase
        var pd = ProgressDialog(this)
        pd.setTitle("Uploading")
        pd.show()

        val listRef = storage.reference.child("UsersTakenPictures/$userID/Meta_${nombreMeta}]/")
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                var Renombrar = false
                var FotoFB =""

                for (item in listResult.items) {
                    // All the items under listRef.
                    val FotoFirebaseSeparada= (item.name.split("-").toTypedArray())
                    //Toast.makeText(this,"SEPARADA:${FotoFirebaseSeparada[0]}",Toast.LENGTH_SHORT).show()

                    var FotoFB = FotoFirebaseSeparada[0]
                    if(FotoFB==name){
                        //   Toast.makeText(this,"YA EXISTE UN ARCHIVO",Toast.LENGTH_SHORT).show()
                        Renombrar=true
                    }

                    // Toast.makeText(this,"Foto item:"+FotoFirebaseSeparada[0],Toast.LENGTH_SHORT).show()
                }
                if(Renombrar){
                    FotoFB = ("foto_${nombreMeta}_2")
                    Toast.makeText(this,"Se ha actualizado la foto de registro de actividad",Toast.LENGTH_SHORT).show()
                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_${nombreMeta}/${FotoFB}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->


                        }
                        .addOnFailureListener { p0 ->
                        }
                        .addOnProgressListener { p0 ->
                        }
                }
                else{
                    val FotoListInicial = (photofile.name.split("-").toTypedArray())
                    val FotoInicial = FotoListInicial[0]

                    var imageRef =
                        FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_${nombreMeta}/${FotoInicial}")
                    imageRef.putFile(takenImage)
                        .addOnSuccessListener { p0 ->

                            Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                            //  Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()
                            pd.dismiss()
                            val intent = Intent(this, EjercicioActivity::class.java)
                            startActivity(intent)

                        }
                        .addOnFailureListener { p0 ->
                        pd.dismiss()
                            Toast.makeText(applicationContext, "Error al subir", Toast.LENGTH_SHORT).show()

                            Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener {

                Toast.makeText(this,"No se que paso",Toast.LENGTH_SHORT).show()
                val FotoListInicial = (photofile.name.split("-").toTypedArray())
                val FotoInicial = FotoListInicial[0]

                var imageRef =
                    FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_${nombreMeta}/${FotoInicial}")
                imageRef.putFile(takenImage)
                    .addOnSuccessListener { p0 ->

                        Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                        //  Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                    }
                    .addOnFailureListener { p0 ->

                        Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                    }
            }

    }





}
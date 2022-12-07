package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.red
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
import kotlinx.android.synthetic.main.activity_ejercicio.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class EjercicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var listViewRutinas2: ListView?= null
    var listViewRutinas3: ListView?= null
    var textViewRutina: TextView?= null
    var buttonIniciar: Button?= null

    private val db = FirebaseFirestore.getInstance()
    var num = 0; var nombre  = ""; var xp: Int? = null
    var nombreRutina =""
    var fecha = ""
    var dia = 0; var mes = 0; var ano = 0
    var meta = ""
    var tiempo = ""

    private fun CargarUltimasFechasDeMetas(opcion: Int): Array<String?>{
        var cadena = "["

        if(opcion == 1) {
            if (!MainActivity.listaMetasAllDates.isEmpty()) { //para tomar las ultimas fechas trabajadas de las metas
                for (i in 0..MainActivity.listaMetasAllDates.size - 1) {
                    cadena += MainActivity.listaMetasAllDates[i]// agrega las fechas
                    cadena += "," //y una coma
                }

                var contador = 0
                for (i in 0 until cadena.length) {
                    contador += 1
                }
                cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma
            }
        }else{
            if (!MainActivity.listaMetasDates.isEmpty()) { //para tomar las ultimas fechas trabajadas de las metas
                for (i in 0..MainActivity.listaMetasDates.size - 1) {
                    cadena += MainActivity.listaMetasDates[i]// agrega las fechas
                    cadena += "," //y una coma
                }

                var contador = 0
                for (i in 0 until cadena.length) {
                    contador += 1
                }
                cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma
            }
        }

        val arreglo: Array<String?>
        arreglo = cadena.split(",").toTypedArray() //toma las fechas separadas

        return arreglo
    }

    private fun CargarListas(){
        //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)
            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)
            MainActivity.listaRutinasVista = MainActivity.listaRutinasVista1
            MainActivity.listaRutinasVista.addAll(MainActivity.listaRutinasVista2)
            MainActivity.validadorAcomodo = false
        }


        ////////////////////////////////////////////////////////////////////////////////////////////

        var arreglo = CargarUltimasFechasDeMetas(1)
        notificacion3semanas(arreglo) //para las notificaciones si una meta no se trabaja hace dos semanas

        arreglo = CargarUltimasFechasDeMetas(1)
        notificacion2semanas(arreglo) //para avisarle que rutinas que no ha trabajado en 3 semanas se borraron

        ////////////////////////////////////////////////////////////////////////////////////////////

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinasVista)
        listViewRutinas2!!.setAdapter(adapter) //La tabla se adapta en la text view

        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinasATrabajar + MainActivity.listaMetas)
        listViewRutinas3!!.adapter = adapter2 //La tabla se adapta en la text view

        if(MainActivity.listaRutinasATrabajar.isEmpty() && MainActivity.listaMetas.isEmpty()){
            textViewAyudaEj2.setVisibility(View.VISIBLE)
        }
        else{
            //
           // NotificacionRutinaPendiente()
            NotificacionRutinaPen()
        }

        if(MainActivity.listaRutinas.isEmpty()){
            textViewAyudaEj1.setVisibility(View.VISIBLE)
        }
    }

    private fun NotificacionRutinaPen() {
        val intent = Intent(this, EjercicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "Chanel1")
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("Recordatorio")
            .setContentText("Tienes pendientes programados hoy, revisa que son!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(2, builder.build())
        }

    }
    private fun NotificacionSinEjercicio3Dias() {
        val intent = Intent(this, EjercicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "Chanel1")
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("Recordatorio")
            .setContentText("Tienes una rutina pendiente por hacer!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(2, builder.build())
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

    fun notificacion2semanas(ultimasFechas: Array<String?>) {
        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt(); val mesHoy = mesHoy2.toInt(); val anoHoy = anoHoy2.toInt()

        var diaNot = diaHoy - 14; var mesNot = mesHoy; var anoNot = anoHoy

        var dia = 0; var mes = 0; var ano = 0
        var contador = -1

        var mandarNot = false

        if (diaNot <= 0) {
            mesNot = mesHoy - 1

            if (mesNot == 0) {
                mesNot = 12
                anoNot = anoHoy - 1
            }

            if (mesNot == 1 || mesNot == 3 || mesNot == 5 || mesNot == 7 || mesNot == 8 || mesNot == 10 || mesNot == 12) {
                diaNot = 31 + diaNot
            } else {
                if (mesNot == 2) {
                    diaNot = 28 + diaNot
                } else {
                    diaNot = 30 + diaNot
                }
            }
        }

        if(ultimasFechas[0] != "[") {
            for (i in ultimasFechas) { //recorre las ultimas fechas trabajadas de la meta
                contador += 1
                dia = ultimasFechas[contador]!!.split("-").toTypedArray()[0].toInt()
                mes = ultimasFechas[contador]!!.split("-").toTypedArray()[1].toInt()
                ano = ultimasFechas[contador]!!.split("-").toTypedArray()[2].toInt()

                if (dia <= diaNot && mes == mesNot && ano == anoNot || mes < mesNot && ano == anoNot || ano < anoNot) {
                    mandarNot = true

                    //borrar meta de listas: all dates, all metas
                    var posicion: Int
                    posicion = MainActivity.listaMetasAllDates.indexOf(dia.toString()+"-"+mes.toString()+"-"+ano.toString())
                    MainActivity.listaMetasAllDates.removeAt(posicion)
                    MainActivity.listaAllMetas.removeAt(posicion)
                }
            }
        }

        if(mandarNot){
            Toast.makeText(this, "Tienes metas pendientes a caducar", Toast.LENGTH_SHORT).show()
            //NotificacionNoHasAvanzado2Semanas()
            NotificacionNoHasAvanzado()
        }

    }

    fun notificacion3semanas(ultimasFechas: Array<String?>) {
        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt(); val mesHoy = mesHoy2.toInt(); val anoHoy = anoHoy2.toInt()

        var diaNot = diaHoy - 21; var mesNot = mesHoy; var anoNot = anoHoy

        var dia = 0; var mes = 0; var ano = 0

        var mandarNot = false

        if (diaNot <= 0) {
            mesNot = mesHoy - 1

            if (mesNot == 0) {
                mesNot = 12
                anoNot = anoHoy - 1
            }

            if (mesNot == 1 || mesNot == 3 || mesNot == 5 || mesNot == 7 || mesNot == 8 || mesNot == 10 || mesNot == 12) {
                diaNot = 31 + diaNot
            } else {
                if (mesNot == 2) {
                    diaNot = 28 + diaNot
                } else {
                    diaNot = 30 + diaNot
                }
            }
        }

        if(ultimasFechas[0] != "[") {
            for (i in ultimasFechas) { //recorre las ultimas fechas trabajadas de la meta
                dia = i!!.split("-").toTypedArray()[0].toInt() //el ultimo dia trabajado de todas las metas
                mes = i!!.split("-").toTypedArray()[1].toInt()
                ano = i!!.split("-").toTypedArray()[2].toInt()

                if (dia < diaNot && mes == mesNot && ano == anoNot || mes < mesNot && ano == anoNot || ano < anoNot) {
                    mandarNot = true

                    //borrar meta de listas: all dates, eventos2, all metas,
                    var cadena = ""

                    var posicion: Int
                    posicion = MainActivity.listaMetasAllDates.indexOf(dia.toString()+"-"+mes.toString()+"-"+ano.toString())
                    MainActivity.listaMetasAllDates.removeAt(posicion)
                    cadena = MainActivity.listaAllMetas[posicion] //toma la meta que se va a borrar
                    MainActivity.listaAllMetas.removeAt(posicion)
                    MainActivity.listaEventos2.removeAt(posicion)

                    //borrar lista metas, metas dates, y de la base de datos
                    var arreglo = CargarUltimasFechasDeMetas(2)
                    if(arreglo[0] != "[") {
                        for (j in arreglo) { //para borrar la meta de las listas y de la base de datos
                            val dia2 = j!!.split("-")
                                .toTypedArray()[0].toInt() //el ultimo dia trabajado de las metas que se hace hoy
                            val mes2 = j!!.split("-").toTypedArray()[1].toInt()
                            val ano2 = j!!.split("-").toTypedArray()[2].toInt()

                            if (dia == dia2 && mes == mes2 && ano == ano2) {
                                posicion =
                                    MainActivity.listaMetasDates.indexOf(dia.toString() + "-" + mes.toString() + "-" + ano.toString())
                                MainActivity.listaMetasDates.removeAt(posicion)
                                MainActivity.listaMetas.removeAt(posicion)
                            }
                        }
                    }

                    //borra la meta de la base de datos
                    if(cadena != "") {
                        cadena = cadena!!.split(" | ").toTypedArray()[0]

                        MainActivity.user?.let{ usuario ->
                            db.collection("users").document(usuario).collection("metas").document(cadena).delete()
                        }
                    }

                    //borra meta de la lista de vista (las que se muestran en seguimiento) y lista metas vista fechas
                    posicion = MainActivity.listaMetasVistaDates.indexOf(dia.toString()+"-"+mes.toString()+"-"+ano.toString())
                    MainActivity.listaMetasVistaDates.removeAt(posicion)
                    MainActivity.listaMetasVista.removeAt(posicion)

                    nombreRutina = cadena
                }
            }
        }

        if(mandarNot){
            Toast.makeText(this, "Se ha borrado una meta que no se trabajaba en 3 semanas", Toast.LENGTH_SHORT).show()
            //Notificacion se ha borrado meta
            //NotificacionRutinaBorrada()
            NotificacionRutinaEliminada(nombreRutina)
        }
    }

    private lateinit var drawer: DrawerLayout
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejercicio)
        initToolbar()
        initNavigationView()
        createNotificationChannel()
        createNotificationChannel2()

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)
        listViewRutinas3 = findViewById(R.id.listViewRutinas3)
        textViewRutina = findViewById(R.id.textViewRutina)
        buttonIniciar = findViewById(R.id.buttonIniciar); buttonIniciar!!.visibility = View.INVISIBLE; buttonIniciar!!.isEnabled =
            false

        Toast.makeText(this, "Seleccione la rutina", Toast.LENGTH_SHORT).show()
        validarUltimoDía()
        CargarListas()

        listViewRutinas2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]
            fecha = "0"; tiempo = "0"

            textViewRutina!!.setText("Rutina seleccionada: "+nombre)

            buttonIniciar!!.visibility = View.VISIBLE; buttonIniciar!!.isEnabled = true

            var idRutina: Int
            MainActivity.user?.let { usuario -> //abre la base de datos
                db.collection("users").document(usuario).collection("rutinas").get().addOnSuccessListener {
                    for(rutinas in it){ //para cada rutina
                        idRutina = (rutinas.get("id") as Long).toInt() //toma el id de la rutina
                        if(idRutina == num){ //al encontrar la seleccionada
                            xp = (rutinas.get("xp") as Long).toInt() //guardara la xp que tiene
                        }
                    }
                }
            }
        }

        listViewRutinas3!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            var aux = ""

            if(MainActivity.listaRutinasATrabajar.isEmpty()) {
                aux = MainActivity.listaMetas[position].split(" | ").toTypedArray()[2]
            }else{
                if(MainActivity.listaMetas.isEmpty()){
                    aux = MainActivity.listaRutinasATrabajar[position].split(" | ").toTypedArray()[2]
                }else{
                    var listaAux = MainActivity.listaRutinasATrabajar + MainActivity.listaMetas
                    aux = listaAux[position].split(" | ").toTypedArray()[2]
                }
            }

            val arreglo: Array<String?>
            arreglo = aux.split(" ").toTypedArray()

            if(arreglo[0].toString() == "Fecha:"){ //para identificar a las rutinas unicas
                num = MainActivity.listaRutinasATrabajar[position].split(" ").toTypedArray()[0].toInt()
                nombre = MainActivity.listaRutinasATrabajar[position].split(" | ").toTypedArray()[1]
                fecha = MainActivity.listaRutinasATrabajar[position].split("Fecha: ").toTypedArray()[1]
                tiempo = "0"

                textViewRutina!!.text = "Rutina seleccionada: "+nombre

                buttonIniciar!!.visibility = View.VISIBLE; buttonIniciar!!.isEnabled = true

                var idRutina: Int
                MainActivity.user?.let { usuario -> //abre la base de datos
                    db.collection("users").document(usuario).collection("rutinas").get().addOnSuccessListener {
                        for(rutinas in it){ //para cada rutina
                            idRutina = (rutinas.get("id") as Long).toInt() //toma el id de la rutina
                            if(idRutina == num){ //al encontrar la seleccionada
                                xp = (rutinas.get("xp") as Long).toInt() //guardara la xp que tiene
                            }
                        }
                    }
                }
            }else{ //para las metas
                var numDif = 0
                if(!MainActivity.listaRutinasATrabajar.isEmpty()){ //en caso de que haya una rutina agragara un numero para cambiar la posicion de la lista (esto pq hay dos listas mostrandose)
                    numDif = 1
                }
                nombre = MainActivity.listaMetas[position - numDif].split(" | ").toTypedArray()[0]
                meta = MainActivity.listaMetas[position - numDif].split(" | ").toTypedArray()[2]

                num = -1; xp = 0; fecha = "0"

                if(arreglo[0].toString() == "Completar:") { //para tomar el tiempo (en caso de que la meta sea por tiempo)
                    var minutos: Int; var horas = 0
                    var minutosAux: String; var horasAux: String

                    tiempo = aux.split("Completar: ").toTypedArray()[1] //toma solo los tiempos

                    if(tiempo.length <= 5){ //si hay menos de 5 caracteres
                        minutos = tiempo.split("min").toTypedArray()[0].toInt() //toma los minutos
                    }else {
                        horasAux = tiempo.split(" ").toTypedArray()[0] //separa las horas y minutos
                        minutosAux = tiempo.split(" ").toTypedArray()[1]
                        horas = horasAux.split("hr").toTypedArray()[0].toInt() //y los toma de manera separada
                        minutos = minutosAux.split("min").toTypedArray()[0].toInt()
                    }

                    //forma la cadena de texto que enviará a ejercicios
                    if(horas == 0){ //comienza con las horas
                        tiempo = "00 : "
                    }else{
                        if(horas < 10){
                            tiempo = "0" + horas.toString() + " : "
                        }else{
                            tiempo = horas.toString() + " : "
                        }
                    }

                    if(minutos < 10){ //y despues con los minutos
                        tiempo += "0" + minutos.toString() + " : 00"
                    }else{
                        tiempo += minutos.toString() + " : 00"
                    }
                }

                var sdf = SimpleDateFormat("dd")
                val diaHoy = sdf.format(Date()) //se obtiene el dia actual
                sdf = SimpleDateFormat("MM")
                val mesHoy = sdf.format(Date()) //se obtiene el mes actual
                sdf = SimpleDateFormat("yyyy")
                val anoHoy = sdf.format(Date()) //se obiene el año actual

                dia = diaHoy.toInt()
                mes = mesHoy.toInt()
                ano = anoHoy.toInt()

                textViewRutina!!.text = "Meta seleccionada: "+nombre

                buttonIniciar!!.visibility = View.VISIBLE; buttonIniciar!!.isEnabled = true
            }
        }

        buttonIniciar!!.setOnClickListener{
            val intent = Intent(this@EjercicioActivity, EjecutadorRutina::class.java)
            intent.putExtra("Num", num)
            intent.putExtra("Nombre", nombre)
            intent.putExtra("XP", xp)
            intent.putExtra("Fecha", fecha) //para las rutinas programadas
            intent.putExtra("Meta", meta) //para las metas
            intent.putExtra("Dia", dia)
            intent.putExtra("Mes", mes)
            intent.putExtra("Ano", ano)
            intent.putExtra("Tiempo", tiempo)
            startActivity(intent)
        }
    }

    private fun validarUltimoDía() {

        db.collection("users").document(MainActivity.user!!).collection("UltimaFechaTrabajada").get().addOnSuccessListener { result ->
            var UltimaFechaTrabajada ="09-05-2002"
            for (document in result) {
                UltimaFechaTrabajada = document.get("UltimaFechaTrabajada").toString()
                Toast.makeText(this,"UltimaFecha=$UltimaFechaTrabajada",Toast.LENGTH_LONG).show()
            }
           // var StringToDate= LocalDate.parse(UltimaFechaTrabajada, DateTimeFormatter.ISO_DATE)
            var string = UltimaFechaTrabajada
            val format = DateTimeFormatter.ofPattern("dd-M-yyyy")
            var date: LocalDate = getDateFromString(string, format)

            var dias3futuro: LocalDate= date.plusDays(3) //Fecha en localdate autosetear alarma con 3 dias ?
            /*Log.d("Futuro", dias3futuro.toString())

            val formatters = DateTimeFormatter.ofPattern("dd-M-yyyy")
            val FechaFutura = dias3futuro.format(formatters) //Fecha en formato dd-m-yyyy

            val sdf = SimpleDateFormat("dd-M-yyyy")
            val currentDate = sdf.format(Date())

            Log.d("Futuro2",FechaFutura.toString())
            if(currentDate == UltimaFechaTrabajada){
                Toast.makeText(this,"MismaFecha",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,"No es la misma fecha",Toast.LENGTH_SHORT).show()

            }
            //Setear alarma en 3 dia*/
            Log.d("UltimoDia",dias3futuro.toString())
            if(dias3futuro.toString()!="2002-05-12"){
            Notificacion3Dias(dias3futuro)
            }


        }
    }

    private fun Notificacion3Dias(dias3futuro: LocalDate) {
        val intent = Intent(applicationContext, com.example.wildtracker.ui.Notification::class.java)
        val title = "Recordatorio"
        val message = "No ha realizado ejercicio en 3 días"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            this@EjercicioActivity,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time =getAlarma3Dias(dias3futuro)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("RutinaProgramada", java.lang.String.format(time.toString()))

    }

    private fun getAlarma3Dias(dias3futuro: LocalDate): Long {
        val calendar = Calendar.getInstance()
        val day = (dias3futuro.dayOfMonth)
        val month = (dias3futuro.monthValue-1)
        val year =(dias3futuro.year)
        calendar.set(year, month, day)
        Log.d("AlarmaSet", calendar.timeInMillis.toString())
        return calendar.timeInMillis
    }

    fun getDateFromString(
        string: String?,
        format: DateTimeFormatter?
    ): LocalDate {
        // Converting the string to date
        // in the specified format

        // Returning the converted date
        return LocalDate.parse(string, format)
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
       // notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun NotificacionNoHasAvanzado2Semanas(){

        val intent = Intent(this, EjercicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "Chanel1")
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("Recordatorio")
            .setContentText("No has progresado en 2 semanas en una rutina °-°!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(3, builder.build())
        }
    }


    private fun NotificacionRutinaEliminada(toString: String) {

        val intent = Intent(this, EjercicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        var builder = NotificationCompat.Builder(this, "Chanel1")
            .setSmallIcon(R.drawable.icon2)
            .setContentTitle("Oye ${PerfilActivity.NombreUsuario}!")
            .setContentText("Se ha eliminado la rutina $toString ")
            .setColor(titleColor.red)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }


    }



    private fun NotificacionNoHasAvanzado() {
        val intent = Intent(applicationContext, com.example.wildtracker.ui.Notification::class.java)
        val title = "Recordatorio"
        val message = "No has progresado en 2 semanas en una rutina °-°"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            this@EjercicioActivity,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time =getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("RutinaProgramada", java.lang.String.format(time.toString()))

    }
    private fun NotificacionRutinaPendiente() {
        val intent = Intent(applicationContext, com.example.wildtracker.ui.Notification::class.java)
        val title = "Recordatorio"
        val message = "Tienes una rutina pendiente por hacer! "
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)


        val pendingIntent = PendingIntent.getBroadcast(
            this@EjercicioActivity,
            2,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time =getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("RutinaProgramada", java.lang.String.format(time.toString()))

    }
    private fun NotificacionRutinaBorrada() {
        val intent = Intent(applicationContext, com.example.wildtracker.ui.Notification::class.java)
        val title = "Rutina Borrada"
        val message = "A falta de seguimiento de ejercicio hemos eliminado una rutina "
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)


        val pendingIntent = PendingIntent.getBroadcast(
            this@EjercicioActivity,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time =getTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
        Log.d("RutinaProgramada", java.lang.String.format(time.toString()))

    }

    private fun getTime(): Long {
        val calendar = Calendar.getInstance()
        val minute = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year =calendar.get(Calendar.YEAR)
        val year2 = calendar.timeInMillis
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }




    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Ejercicio"
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
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_metas -> callMetasActivity()
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

    private fun callMetasActivity() {
        val intent = Intent(this, MetasActivity::class.java)
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

}
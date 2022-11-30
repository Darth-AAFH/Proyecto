package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.example.wildtracker.ui.MainActivity.Companion.listaSeguidores
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PerfilActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance()
    private lateinit var filepath:Uri
    /*var Perfil_birthday = findViewById<EditText>(R.id.Perfil_birthday)
    var Perfil_mail = findViewById<EditText>(R.id.Perfil_mail)
    var Perfil_name = findViewById<EditText>(R.id.Perfil_name)
*/

    companion object{
        lateinit var usernameDb : String
        lateinit var nombreRutinaEliminada :String
        lateinit var NombreUsuario:String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        initToolbar()
        initNavigationView()
        setup()

        ValidadorNombres()

        CargarTiempos()
        CargarRanking()

        CargarEjercicios()
        CargarRutinas()
        CargarRutinasATrabajar()
        CargarMetas()

        CargarSeguidores()
    }

    val listaNombres = ArrayList<String>()

    private fun ValidadorNombres(){
        var cadena: String
        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {
                for (user in it) { //para cada usuario
                    cadena = user.get("Name").toString()
                    listaNombres.add(cadena)
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Perfil"
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
            R.id.nav_metas -> callMetasActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_musica ->callMusica()
            R.id.nav_amigos ->callAmigosActivity()
            R.id.Settings->callAjustesActivity()
            R.id.nav_seguimiento->callSeguimientoActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    private fun callAjustesActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun callAmigosActivity() {
        val intent = Intent(this, Activity_Amigos::class.java)
        startActivity(intent)
    }
    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
        startActivity(intent)
    }

    private fun callPerfilActivity() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    var tiempoAux: Int = 0
    @SuppressLint("SimpleDateFormat")
    private fun CargarTiempos() {
        var sdf = SimpleDateFormat("dd")
        var diaHoy = sdf.format(Date()) //se obtiene e dia actual
        sdf = SimpleDateFormat("MM")
        var mesHoy = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy = sdf.format(Date()) //se obiene el año actual

        MainActivity.diaSemanaHoy = diaSemana(diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt()) //se obtiene el numero de dia de la semana (lunes = 1, martes = 2, miercoles = 3, etc)

        val dias: ArrayList<String> = arrayListOf<String>() //arreglo de string?
        dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add("") //se llena el arreglo de dias con datos vacios

        var contadorAux = 0 //un contador auxiliar para encontrar los demas días

        for (i in 7 downTo 0) { //para el numero de dia de la semana para toda la semana pasada
            var diaAux = diaHoy.toInt() - contadorAux //variable que va a modificarse segun el día, inicia siendo el dia actual menos 0

            if(diaAux == 0){ //en caso de que llegue a cero el día
                val mesAux = mesHoy.toInt() //una variable para cambiarle el valor al mes

                if(mesAux == 3){
                    diaHoy = "28"
                    diaAux = 28
                }else{
                    if(mesAux == 2 || mesAux == 4 || mesAux == 6 || mesAux == 8 || mesAux == 9 || mesAux == 11 || mesAux == 1) {
                        diaHoy = "31"
                        diaAux = 31
                    }else{
                        diaHoy = "30"
                        diaAux = 30
                    }
                }

                if((mesAux - 1) < 10){//fallo para cambio de año
                    mesHoy = "0" + (mesHoy.toInt() - 1).toString()
                }else{
                    mesHoy = (mesHoy.toInt() - 1).toString()
                }
                contadorAux = 0
            }

            if(diaAux <= 10){ //se guardan en el arreglo de días los dias pasados y se les da el formato
                dias[i] = "0" + diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }else {
                dias[i] = diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }

            contadorAux+= 1 //se le agrega al contador un numero más para retorceder más días
        }

        MainActivity.user?.let { usuario -> //trae los tiempos segun el día
            db.collection("users").document(usuario).collection("tiempos") //abre la base de datos
                .get().addOnSuccessListener {
                    for(tiempos in it) { //por cada dia registrado
                        val idFecha = tiempos.get("idFecha") as String //toma la fecha

                        if(idFecha == dias[1]){ //si la fecha es igual al dia lunes guardado
                            MainActivity.dia1 = (tiempos.get("minutos") as Long).toDouble() //guardara el tiempo en la variable del dia

                            tiempoAux = (tiempos.get("horas") as Long).toInt() //de horas a minutos
                            MainActivity.dia1 += tiempoAux * 60

                            tiempoAux = (tiempos.get("segundos") as Long).toInt() //y de segundos a minutos
                            MainActivity.dia1 += tiempoAux / 60
                        }
                        if(idFecha == dias[2]){ //y así con las demas fechas
                            MainActivity.dia2 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia2 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia2 += tiempoAux / 60
                        }
                        if(idFecha == dias[3]){
                            MainActivity.dia3 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia3 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia3 += tiempoAux / 60
                        }
                        if(idFecha == dias[4]){
                            MainActivity.dia4 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia4 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia4 += tiempoAux / 60
                        }
                        if(idFecha == dias[5]){
                            MainActivity.dia5 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia5 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia5 += tiempoAux / 60
                        }
                        if(idFecha == dias[6]){
                            MainActivity.dia6 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia6 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia6 += tiempoAux / 60
                        }
                        if(idFecha == dias[7]){
                            MainActivity.dia7 = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.dia7 += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.dia7 += tiempoAux / 60
                        }
                    }
                }
        }
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

    private fun callInicioActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun CargarEjercicios(){
        var cadena: String; var id: Int
        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario)
                    .collection("ejercicios") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (ejercicio in it) { //para cada ejercicio
                            id = (ejercicio.get("id") as Long).toInt()
                            if(id < 10) {
                                cadena = id.toString() //toma el id del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("nombre").toString() //toma el nombre del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("tipo").toString() //toma el tipo
                                cadena += " | " //le pone un texto para darle orden
                                val pesoAux = ejercicio.get("peso").toString()
                                if (pesoAux == "true") {
                                    cadena += "Con peso"
                                } else {
                                    cadena += "Sin peso"
                                }
                                MainActivity.listaEjercicios1.add(cadena)//y lo guarda en la primer lista
                            }else{
                                cadena = id.toString() //toma el id del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("nombre").toString() //toma el nombre del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("tipo").toString() //toma el tipo
                                cadena += " | " //le pone un texto para darle orden
                                val pesoAux = ejercicio.get("peso").toString()
                                if (pesoAux == "true") {
                                    cadena += "Con peso"
                                } else {
                                    cadena += "Sin peso"
                                }
                                MainActivity.listaEjercicios2.add(cadena) //y guarda en la segunda lista
                            }
                        }
                    }
            }
            MainActivity.listaEjercicios1.sort(); MainActivity.listaEjercicios2.sort()// acomoda las listas
        }
    }
    private fun CargarRutinas(){
        var cadena: String; var id: Int
        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario)
                    .collection("rutinas") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (rutina in it) { //para cada rutina
                            id = (rutina.get("id") as Long).toInt()
                            if(id < 10) {
                                cadena = (rutina.get("id") as Long).toString() //toma el id de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                                cadena += " | Nivel: " //le pone un texto para darle orden
                                cadena += (rutina.get("nivel") as Long).toString() //toma el nivel de la rutina
                                MainActivity.listaRutinasVista1.add(cadena)
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                MainActivity.listaRutinas1.add(cadena)
                            }else{
                                cadena = (rutina.get("id") as Long).toString() //toma el id de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                                cadena += " | Nivel: " //le pone un texto para darle orden
                                cadena += (rutina.get("nivel") as Long).toString() //toma el nivel de la rutina
                                MainActivity.listaRutinasVista2.add(cadena)
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                MainActivity.listaRutinas2.add(cadena)
                            }
                        }
                    }
            }
            MainActivity.listaRutinas1.sort(); MainActivity.listaRutinas2.sort()// acomoda las listas
            MainActivity.listaRutinasVista1.sort(); MainActivity.listaRutinasVista2.sort()// acomoda las listas
        }
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

    private fun CargarRutinasATrabajar(){
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

        var cadena: String; var fecha: String

        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario)
                    .collection("rutinasAtrabajar") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (rutina in it) { //para cada rutina
                            cadena = (rutina.get("idRutina") as Long).toString() //toma el id de la rutina
                            cadena += " | " //le pone un texto para darle orden
                            cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                            cadena += " | Fecha: " //le pone un texto para darle orden
                            cadena += rutina.get("dia").toString()
                            cadena += "-" //le pone un texto para darle orden
                            cadena += rutina.get("mes").toString()
                            cadena += "-" //le pone un texto para darle orden
                            cadena += rutina.get("ano").toString()

                            fecha = rutina.get("dia").toString()
                            fecha += "-" //le pone un texto para darle orden
                            fecha += rutina.get("mes").toString()
                            fecha += "-" //le pone un texto para darle orden
                            fecha += rutina.get("ano").toString()

                            if(fecha == fechaHoy) {
                                MainActivity.listaRutinasATrabajar.add(cadena)
                            }

                            if(borrarRutinasCaducadas((rutina.get("dia") as Long).toInt(), (rutina.get("mes") as Long).toInt(), (rutina.get("ano") as Long).toInt(), diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt(), fecha)){
                                MainActivity.listaRutinasATrabajarAux.add(cadena)
                                MainActivity.listaEventos1.add(fecha)
                            }
                        }
                    }
            }
        }
    }
    private fun borrarRutinasCaducadas(dia: Int, mes: Int, ano: Int, diaHoy: Int, mesHoy: Int, anoHoy: Int, fecha: String): Boolean {
        if(anoHoy >= ano){
            if(anoHoy > ano){
                MainActivity.user?.let{ usuario ->
                    db.collection("users").document(usuario).collection("rutinasAtrabajar").document(fecha).delete()
                    return false
                }
            }else{
                if(mesHoy >= mes){
                    if(mesHoy > mes){
                        MainActivity.user?.let{ usuario ->
                            db.collection("users").document(usuario).collection("rutinasAtrabajar").document(fecha).delete()
                            return false
                        }
                    }else{
                        if(diaHoy > dia){
                            MainActivity.user?.let{ usuario ->
                                db.collection("users").document(usuario).collection("rutinasAtrabajar").document(fecha).delete()
                                return false
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    private fun callSeguimientoActivity() {
        val intent = Intent(this, SeguimientoActivity::class.java)
        startActivity(intent)
    }

    data class userData (
        val Name: String? = "",
        var puntosTotales: Int? = 0
    )

    private fun CargarRanking () {
        MainActivity.listaRanking.clear() //limpia las listas del ranking para poder recargarlas
        MainActivity.listaRanking1.clear(); MainActivity.listaRanking2.clear()
        MainActivity.listaRanking3.clear(); MainActivity.listaRanking4.clear()

        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {

                GlobalScope.launch(Dispatchers.IO) { //para trer los datos correctamente por pausas

                    for (userIt in it) { //para cada usuario
                        val userEmail = userIt.get("email") as String? //va a tomar el correo
                        val nameDocument = Firebase.firestore.collection("users").document(userEmail.toString()) //la ruta en la base de datos
                        val user1 = nameDocument.get().await().toObject(userData::class.java) //y se va a traer los datos

                        withContext(Dispatchers.Main){

                            if((user1!!.puntosTotales)!!.toInt() < 10){
                                if(MainActivity.user == userEmail){ //si es el usuario en uso
                                    MainActivity.listaRanking1.add((user1!!.puntosTotales).toString() + " .- " + user1.Name + " ✰") //lo agrega a la lista con una estrellita a modo de identificador
                                }else{
                                    MainActivity.listaRanking1.add((user1!!.puntosTotales).toString() + " .- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                                }
                            }else{
                                if((user1!!.puntosTotales)!!.toInt() < 100){
                                    if(MainActivity.user == userEmail){ //si es el usuario en uso
                                        MainActivity.listaRanking2.add((user1!!.puntosTotales).toString() + " .- " + user1.Name + " ✰") //lo agrega a la lista con una estrellita a modo de identificador
                                    }else{
                                        MainActivity.listaRanking2.add((user1!!.puntosTotales).toString() + " .- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                                    }
                                }else{
                                    if((user1!!.puntosTotales)!!.toInt() < 1000){
                                        if(MainActivity.user == userEmail){ //si es el usuario en uso
                                            MainActivity.listaRanking3.add((user1!!.puntosTotales).toString() + " .- " + user1.Name + " ✰") //lo agrega a la lista con una estrellita a modo de identificador
                                        }else{
                                            MainActivity.listaRanking3.add((user1!!.puntosTotales).toString() + " .- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                                        }
                                    }else{
                                        if(MainActivity.user == userEmail){ //si es el usuario en uso
                                            MainActivity.listaRanking4.add((user1!!.puntosTotales).toString() + " .- " + user1.Name + " ✰") //lo agrega a la lista con una estrellita a modo de identificador
                                        }else{
                                            MainActivity.listaRanking4.add((user1!!.puntosTotales).toString() + " .- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        MainActivity.listaRanking1.sort(); MainActivity.listaRanking2.sort()// acomoda las listas
        MainActivity.listaRanking3.sort(); MainActivity.listaRanking4.sort()
    }
    private fun CargarSeguidores(){
        listaSeguidores.clear()
        val listaSeguidores = ArrayList<String>()
        var perfilGet =""
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        db.collection("users").document(MainActivity.user!!).collection("Seguidores").get().addOnSuccessListener { result ->
            for (document in result) {

                perfilGet = document.get("Nombre").toString()

                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                   // Toast.makeText(this,perfilGet,Toast.LENGTH_LONG).show()
                    Thread.sleep(1_00)  // wait for 1 second
                    listaSeguidores.add(perfilGet)
                }

            }
            Thread.sleep(1_00)  // wait for 1 second
            progresDialog.dismiss()

        }
    }

    private fun callRankingActivity() {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
    }

    private fun callChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
    private fun CargarMetas(){
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

        var diaF: Int; var mesF: Int; var anoF: Int
        var diasTotales: Int; var diasxSemana = 0; var diasATrabajar: Int

        var datoDeSuma = 0

        var lun = 0; var mar = 0; var mier = 0; var juev = 0; var vier = 0; var sab = 0; var dom = 0

        var cadena: String

        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las metas
                db.collection("users").document(usuario)
                    .collection("metas") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (meta in it) { //para cada meta
                            lun = 0; mar = 0; mier = 0; juev = 0; vier = 0; sab = 0; dom = 0

                            diaF = (meta.get("diaFinal") as Long).toInt()
                            mesF = (meta.get("mesFinal") as Long).toInt()
                            anoF = (meta.get("anoFinal") as Long).toInt()

                            //primero se obtiene la diferencia de dias entre las dos fechas (diasTotales)
                            if(anoF == anoHoy){ //se comparan los años
                                if(mesF == mesHoy){ //se comparan los meses
                                    diasTotales = diaF - diaHoy //y si son los mismos solo se obtiene la diferencia entre los días
                                }else{ //si no, se le suman los días del mes inicial
                                    if(mesHoy == 1 || mesHoy == 3 || mesHoy == 5 || mesHoy == 7 || mesHoy == 8 || mesHoy == 10 || mesHoy == 12){
                                        diasTotales = 31 - diaHoy
                                    }else{
                                        if(mesHoy == 2){
                                            diasTotales = 28 - diaHoy
                                        }else{
                                            diasTotales = 30 - diaHoy
                                        }
                                    }

                                    var i = mesF - 1
                                    while (mesHoy != i) { //se le suman los días de los meses intermedios
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

                                    diasTotales += diaF //y se le suman los días del mes final
                                }
                            }else{ //para años diferentes
                                diasTotales = (anoF - anoHoy)*365 //se obtienen los años en días

                                if(mesF == mesHoy){ //si el mes es igual se resta o suma la diferencia de dias
                                    if(diaF > diaHoy){
                                        diasTotales += (diaF - diaHoy)
                                    } else{
                                        diasTotales -= (diaHoy - diaF)
                                    }

                                }else{
                                    if(mesF > mesHoy){ //si el mes es mayor

                                        //se le suman los dias del mes inicial
                                        if(mesHoy == 1 || mesHoy == 3 || mesHoy == 5 || mesHoy == 7 || mesHoy == 8 || mesHoy == 10 || mesHoy == 12){
                                            diasTotales = diasTotales + 31 - diaHoy
                                        }else{
                                            if(mesHoy == 2){
                                                diasTotales = diasTotales + 28 - diaHoy
                                            }else{
                                                diasTotales = diasTotales + 30 - diaHoy
                                            }
                                        }

                                        var i = mesF - 1
                                        while (mesHoy != i) { //se le suman los días de los meses intermedios
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

                                        diasTotales += diaF //y se le suman los días del mes final

                                    } else{ //y si el mes es menor
                                        //se le restan los dias del mes inicial
                                        diasTotales -= diaHoy

                                        var i = mesHoy - 1
                                        while (mesF != i) { //se le restan los días de los meses intermedios
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
                                        if(mesF == 1 || mesF == 3 || mesF == 5 || mesF == 7 || mesF == 8 || mesF == 10 || mesF == 12){
                                            diasTotales = diasTotales - 31 + diaF
                                        }else{
                                            if(mesF == 2){
                                                diasTotales = diasTotales - 28 + diaF
                                            }else{
                                                diasTotales = diasTotales - 30 + diaF
                                            }
                                        }
                                    }
                                }
                            }

                            //ahora se obtienen los dias que se trabaja por semana (diasxSemana)
                            if(meta.get("lunes") as Boolean){diasxSemana += 1; lun = 1}
                            if(meta.get("martes") as Boolean){diasxSemana += 1; mar = 2}
                            if(meta.get("miercoles") as Boolean){diasxSemana += 1; mier = 3}
                            if(meta.get("jueves") as Boolean){diasxSemana += 1; juev = 4}
                            if(meta.get("viernes") as Boolean){diasxSemana += 1; vier = 5}
                            if(meta.get("sabado") as Boolean){diasxSemana += 1; sab = 6}
                            if(meta.get("domingo") as Boolean){diasxSemana += 1; dom = 7}

                            //los dias a trabajar
                            //diasATrabajar = diasTotales/diasxSemana
                            diasATrabajar = (diasTotales/7)*diasxSemana

                            //el dato para sumar al dato inicial
                            if(diasATrabajar == 0)
                                diasATrabajar = 1
                            datoDeSuma = ((meta.get("datoFinal") as Long).toInt() - (meta.get("datoInicial") as Long).toInt())/diasATrabajar

                            cadena = meta.get("nombre").toString() //toma el nombre de la meta
                            cadena += " | " //se le agraga texto de formato
                            if(meta.get("lunes") as Boolean){cadena += "lun "} //se le agregan los dias a trabajar
                            if(meta.get("martes") as Boolean){cadena += "mar "}
                            if(meta.get("miercoles") as Boolean){cadena += "mier "}
                            if(meta.get("jueves") as Boolean){cadena += "juev "}
                            if(meta.get("viernes") as Boolean){cadena += "vier "}
                            if(meta.get("sabado") as Boolean){cadena += "sab "}
                            if(meta.get("domingo") as Boolean){cadena += "dom "}
                            cadena += "| " //se le agraga texto de formato

                            var suma = true
                            //para ver si es necesario sumarle dato hoy (para que no repita esto el mismo dia varias veces)
                            if((meta.get("diaSeg") as Long).toInt() == diaHoy && (meta.get("mesSeg") as Long).toInt() == mesHoy && (meta.get("anoSeg") as Long).toInt() == anoHoy){
                                suma = false //para no actualizar los datos
                            }

                            var cadena3 = cadena

                            //se le agrega las repeticiones, peso o tiempo a trabajar
                            if(meta.get("peso") as Boolean){ //con un texto que diferencie
                                cadena += "Levantar: "
                                if(suma) {
                                    cadena += ((meta.get("datoInicial") as Long).toInt() + datoDeSuma).toString() //se le agrega las repeticiones o peso a levantar o tiempo
                                }else{
                                    cadena += ((meta.get("datoInicial") as Long).toInt()).toString()
                                }
                                cadena += "kg"
                            }
                            if(meta.get("repeticion") as Boolean){ //con un texto que diferencie
                                cadena += "Repeticiones: "
                                if(suma) {
                                    cadena += ((meta.get("datoInicial") as Long).toInt() + datoDeSuma).toString() //se le agrega las repeticiones o peso a levantar o tiempo
                                }else{
                                    cadena += ((meta.get("datoInicial") as Long).toInt()).toString()
                                }
                            }
                            if(meta.get("tiempo") as Boolean){ //con un texto que diferencie
                                cadena += "Completar: "

                                var minutos = 0
                                var horas = 0

                                if(suma) {
                                    minutos = (meta.get("datoInicial") as Long).toInt() + datoDeSuma
                                }else{
                                    minutos = (meta.get("datoInicial") as Long).toInt()
                                }

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
                            cadena += diaF; cadena += "-"; cadena += mesF; cadena += "-"; cadena += anoF

                            //borrarMetas(diaF, mesF, anoF, meta.id)
                            if(borrarMetas(diaF, mesF, anoF, meta.id)){//dia coincide
                                if(diaSemHoy == lun || diaSemHoy == mar || diaSemHoy == mier || diaSemHoy == juev || diaSemHoy == vier || diaSemHoy == sab || diaSemHoy == dom){
                                    if(suma){ //si no ha actualizado datos hoy
                                    //poner el dia de la actualizacion con la nueva fecha de seguimiento
                                    actualizarMetas(meta.get("nombre").toString(), (datoDeSuma + meta.get("datoInicial") as Long).toInt(), (meta.get("datoFinal") as Long).toInt(),
                                        diaF, mesF, anoF, meta.get("peso") as Boolean, meta.get("repeticion") as Boolean, meta.get("tiempo") as Boolean, meta.get("lunes") as Boolean, meta.get("martes") as Boolean, meta.get("miercoles") as Boolean, meta.get("jueves") as Boolean, meta.get("viernes") as Boolean, meta.get("sabado") as Boolean, meta.get("domingo") as Boolean, diaHoy, mesHoy, anoHoy,
                                        (meta.get("ultDia") as Long).toInt(), (meta.get("ultMes") as Long).toInt(), (meta.get("ultAno") as Long).toInt())
                                    }

                                    if((meta.get("ultDia") as Long).toInt() != diaHoy || (meta.get("ultMes") as Long).toInt() != mesHoy || (meta.get("ultAno") as Long).toInt() != anoHoy){ //si la meta no se ha trabajado hoy
                                        MainActivity.listaMetas.add(cadena)

                                        var cadena4 = (meta.get("ultDia") as Long).toString() + "-" + (meta.get("ultMes") as Long).toString() + "-" + (meta.get("ultAno") as Long).toString()
                                        MainActivity.listaMetasDates.add(cadena4)
                                    }
                                }
                                var cadena2 = (meta.get("ultDia") as Long).toString() + "-" + (meta.get("ultMes") as Long).toString() + "-" + (meta.get("ultAno") as Long).toString()
                                MainActivity.listaMetasAllDates.add(cadena2) //guarda todas las metas no caducadas (su ultimo dia trabajado)
                                MainActivity.listaMetasVistaDates.add(cadena2)

                                cargarFechasDeMetas(diaHoy, mesHoy, anoHoy, diaF, mesF, anoF, lun == 1, mar == 2, mier == 3, juev == 4, vier == 5, sab == 6, dom == 7)//carga todas las fechas en que se trabaja una meta

                                //se le agrega las repeticiones, peso o tiempo final
                                if(meta.get("peso") as Boolean){ //con un texto que diferencie
                                    cadena3 += "Levantar: "
                                    cadena3 += (meta.get("datoFinal") as Long).toString()
                                    cadena3 += "kg"
                                }
                                if(meta.get("repeticion") as Boolean){ //con un texto que diferencie
                                    cadena3 += "Repeticiones: "
                                    cadena3 += (meta.get("datoFinal") as Long).toString()
                                }
                                if(meta.get("tiempo") as Boolean){ //con un texto que diferencie
                                    cadena3 += "Completar: "

                                    var minutos = 0
                                    var horas = 0

                                    minutos = (meta.get("datoFinal") as Long).toInt()

                                    while(minutos >= 60){ //se obtienen las horas
                                        minutos -= 60
                                        horas += 1
                                    }

                                    if(horas != 0){
                                        cadena3 += horas //se le agrega el tiempo con horas
                                        cadena3 += "hr "
                                    }
                                    cadena3 += minutos //se le agregan los minutos
                                    cadena3 += "min"
                                }
                                //se le agrega la fecha de finalizacion
                                cadena3 += " | Fecha de finalización: "
                                cadena3 += diaF; cadena3 += "-"; cadena3 += mesF; cadena3 += "-"; cadena3 += anoF

                                MainActivity.listaAllMetas.add(cadena3)
                                MainActivity.listaMetasVista.add(cadena3)
                            }
                        }
                    }
            }
            MainActivity.validadorListas = false //cambia el validador para que esto no se vuelva a hacer
        }
    }
    private fun borrarMetas(dia: Int, mes: Int, ano: Int, id: String): Boolean { //se encarga de borrar metas ya caducadas
        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt(); val mesHoy = mesHoy2.toInt(); val anoHoy = anoHoy2.toInt()

        //var dia: Int; var mes: Int; var ano: Int
        if(anoHoy >= ano){
            if(anoHoy > ano){
                MainActivity.user?.let { usuario -> //abre la base de datos
                    db.collection("users").document(usuario).collection("metas").document(id).delete()
                    return false
                }
            }else{
                if(mesHoy >= mes){
                    if(mesHoy > mes){
                        MainActivity.user?.let { usuario -> //abre la base de datos
                            db.collection("users").document(usuario).collection("metas").document(id).delete()
                            return false
                        }
                    }else{
                        if(diaHoy > dia){
                            MainActivity.user?.let { usuario -> //abre la base de datos
                                db.collection("users").document(usuario).collection("metas").document(id).delete()
                                return false
                            }
                        }
                    }
                }
            }
        }
        return true
    }
    private fun actualizarMetas(Nombre: String, DatoInicial: Int, DatoFinal: Int, dia: Int, mes: Int, ano: Int, Peso: Boolean, Repeticion: Boolean, Tiempo: Boolean, D1: Boolean, D2: Boolean, D3: Boolean, D4: Boolean, D5: Boolean, D6: Boolean, D7: Boolean, diaSeg: Int, mesSeg: Int, anoSeg: Int, ultDia: Int, ultMes: Int, ultAno: Int){
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
                        "diaSeg" to diaSeg,
                        "mesSeg" to mesSeg,
                        "anoSeg" to anoSeg,
                        "ultDia" to ultDia,
                        "ultMes" to ultMes,
                        "ultAno" to ultAno
                    )
                )
        }
    }

    fun cargarFechasDeMetas(diaHoyAux: Int, mesHoyAux: Int, anoHoyAux: Int, dia: Int, mes: Int, ano: Int, D1: Boolean, D2: Boolean, D3: Boolean, D4: Boolean, D5: Boolean, D6: Boolean, D7: Boolean){
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

    private fun callMetasActivity() {
        val intent = Intent(this, MetasActivity::class.java)
        startActivity(intent)
    }

    private fun setup(){
        val AlturaProfileEt = findViewById<EditText>(R.id.Perfil_altura)
        val PesoProfileEt = findViewById<EditText>(R.id.Perfil_peso)
        val EditProfileDataButton = findViewById<Button>(R.id.EditProfileDataButton)
        val recoverProfileDataButton = findViewById<Button>(R.id.recoverProfileDataButton)
        val saveProfileButton = findViewById<Button>(R.id.saveProfileButton)
        val ChangeProfilePicButton = findViewById<Button>(R.id.ChangeProfilePicButton)
        val edBirthDay =   findViewById<EditText>(R.id.Perfil_birthday)
        val edEmail =   findViewById<EditText>(R.id.Perfil_mail)
        val edName =   findViewById<EditText>(R.id.Perfil_name)
        val ivProfilePic = findViewById<ImageView>(R.id.Perfil_pic)
        PesoProfileEt.isEnabled=false
        edBirthDay.isEnabled = false
        edEmail.isEnabled = false
        edName.isEnabled = false
        AlturaProfileEt.isEnabled =false
        saveProfileButton.isVisible = false
        ChangeProfilePicButton.isVisible = false
        EditProfileDataButton.isVisible = false

        EditProfileDataButton.isVisible = true
        recoverProfileDataButton.isVisible = false

        setBirthdayEditText(edBirthDay)

        MainActivity.user?.let { it1 ->
            db.collection("users").document(MainActivity.user!!).get()
                .addOnSuccessListener {
                edName.setText (it.get("Name") as String?)
                    NombreUsuario = ((it.get("Name") as String?).toString())
                edEmail.setText(it.get("email") as String?)
                edBirthDay.setText(it.get("birthDay") as String?)
                    AlturaProfileEt.setText(it.get("altura") as String?)
                    PesoProfileEt.setText(it.get("peso") as String?)
            }

        }

        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Imagen")
        progresDialog.setCancelable(false)
        progresDialog.show()


        val userID =FirebaseAuth.getInstance().currentUser!!.email.toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener{

            if(progresDialog.isShowing){
                progresDialog.dismiss()
                try {
                    MainActivity.user?.let { it1 ->
                        db.collection("users").document(MainActivity.user!!).get()
                            .addOnSuccessListener {
                                Companion.usernameDb = ((it.get("Name") as String?).toString())
                            }
                    }

                   // usernameDb = edName.text.toString()

                }catch (e: Exception){
                    MainActivity.user?.let { it1 ->
                        db.collection("users").document(MainActivity.user!!).get()
                            .addOnSuccessListener {
                                Companion.usernameDb = ((it.get("Name") as String?).toString())
                            }
                    }
                }
            }
            val bitmap =BitmapFactory.decodeFile(localfile.absolutePath)
            ivProfilePic.setImageBitmap(bitmap)
        }.addOnFailureListener{
            progresDialog.dismiss()
            Toast.makeText(this,"Recuperación de imagen fallida, sube otra foto",Toast.LENGTH_SHORT).show()
            try {
                MainActivity.user?.let { it1 ->
                    db.collection("users").document(MainActivity.user!!).get()
                        .addOnSuccessListener {
                            Companion.usernameDb = ((it.get("Name") as String?).toString())
                        }
                }

                // usernameDb = edName.text.toString()

            }catch (e: Exception){
                MainActivity.user?.let { it1 ->
                    db.collection("users").document(MainActivity.user!!).get()
                        .addOnSuccessListener {
                            Companion.usernameDb = ((it.get("Name") as String?).toString())
                        }
                }
            }
        }




        saveProfileButton.setOnClickListener{
            var Name = findViewById<EditText>(R.id.Perfil_name).text.toString()
            var NombreDisponible: Boolean = true

            for(item in listaNombres){
                if (Name == item){
                    NombreDisponible = false
                }
            }

            if(NombreDisponible) {
                MainActivity.user?.let { usuario ->
                    db.collection("users").document(usuario).update(
                        mapOf(
                            "birthDay" to findViewById<EditText>(R.id.Perfil_birthday).text.toString(),
                            "email" to findViewById<EditText>(R.id.Perfil_mail).text.toString(),
                            "Name" to findViewById<EditText>(R.id.Perfil_name).text.toString(),
                            "altura" to findViewById<EditText>(R.id.Perfil_altura).text.toString(),
                            "peso" to findViewById<EditText>(R.id.Perfil_peso).text.toString()
                        )
                    )
                }
            }else {
                Toast.makeText(this, "Nombre de usuario no disponible", Toast.LENGTH_SHORT).show()
            }
            callPerfilActivity()
            AlturaProfileEt.isEnabled =false
            saveProfileButton.isVisible = false
            edBirthDay.isEnabled = false
            edEmail.isEnabled = false
            edName.isEnabled = false
            PesoProfileEt.isEnabled=false
            EditProfileDataButton.isVisible = true
        }

        EditProfileDataButton.setOnClickListener {
            AlturaProfileEt.isEnabled =true
            saveProfileButton.isVisible = true
              edBirthDay.isEnabled = true
            edName.isEnabled = true
            PesoProfileEt.isEnabled=true
            EditProfileDataButton.isVisible = false

        }

        ChangeProfilePicButton.setOnClickListener {
        uploadFile()
        }
        ivProfilePic.setOnClickListener {
          startFileChooser()
            ChangeProfilePicButton.isVisible = true
        }

    }
    fun setBirthdayEditText(edBirthDay: EditText) {

        edBirthDay.addTextChangedListener(object : TextWatcher {

            private var current = ""
            private val ddmmyyyy = "DDMMYYYY"
            private val cal = Calendar.getInstance()

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString() != current) {
                    var clean = p0.toString().replace("[^\\d.]|\\.".toRegex(), "")
                    val cleanC = current.replace("[^\\d.]|\\.", "")

                    val cl = clean.length
                    var sel = cl
                    var i = 2
                    while (i <= cl && i < 6) {
                        sel++
                        i += 2
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean == cleanC) sel--

                    if (clean.length < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length)
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        var day = Integer.parseInt(clean.substring(0, 2))
                        var mon = Integer.parseInt(clean.substring(2, 4))
                        var year = Integer.parseInt(clean.substring(4, 8))

                        mon = if (mon < 1) 1 else if (mon > 12) 12 else mon
                        cal.set(Calendar.MONTH, mon - 1)
                        year = if (year < 1900) 1900 else if (year > 2100) 2100 else year
                        cal.set(Calendar.YEAR, year)
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                        clean = String.format("%02d%02d%02d", day, mon, year)
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8))

                    sel = if (sel < 0) 0 else sel
                    current = clean
                    edBirthDay.setText(current)
                    edBirthDay.setSelection(if (sel < current.count()) sel else current.count())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable) {

            }
        })
    }
    private fun uploadFile() {
        val ChangeProfilePicButton = findViewById<Button>(R.id.ChangeProfilePicButton)
        val  userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (filepath != null) {
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()
            var imageRef = FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
            imageRef.putFile(filepath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                }


        }
        ChangeProfilePicButton.isVisible = false
    }

    private fun startFileChooser() {
        var i = Intent()
        i.setType("image/*").action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i,"Elige una imagen"),111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val ivProfilePic = findViewById<ImageView>(R.id.Perfil_pic)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode==Activity.RESULT_OK && data!=null)
        {
            filepath =data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            ivProfilePic.setImageBitmap(bitmap)
        }
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
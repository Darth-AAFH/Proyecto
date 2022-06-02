package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
//import android.widget.Toast
//import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.LoginActivity.Companion.useremail
import com.example.wildtracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

//import com.github.mikephil.charting.data.BarData

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

    ////////////////////////////////////////////////////////////////////////////////////////////////
  /*  lateinit var barList: ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData*/
    ////////////////////////////////////////////////////////////////////////////////////////////////

    companion object{
        val auth: String? = FirebaseAuth.getInstance().currentUser?.email
        var user =  auth

        var listaRutinas1 = ArrayList<String>()
        var listaRutinas2 = ArrayList<String>()
        var listaEjercicios1 = ArrayList<String>()
        var listaEjercicios2 = ArrayList<String>()
        var validadorListas = true

        var listaRutinas = ArrayList<String>()
        var listaEjercicios = ArrayList<String>()
        var validadorAcomodo = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initNavigationView()

        //
        CargarTiempos()
        //
        CargarEjercicios()
        CargarRutinas()
    }


    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerlayout)
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
        tvUser.text = user

    }


    fun callSignOut(view: View) {
        signOut()
    }

    fun signOut() {

        useremail = ""
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_inicio -> callInicioActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_metas -> callMetasActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    private fun callPerfilActivity() {
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun callInicioActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun CargarEjercicios(){
        var cadena: String; var id: Int
        if(validadorListas) {
            user?.let { usuario -> //para cargar las rutinas
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
                                listaEjercicios1.add(cadena)//y lo guarda en la primer lista
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
                                listaEjercicios2.add(cadena) //y guarda en la segunda lista
                            }
                        }
                    }
            }
            listaEjercicios1.sort(); listaEjercicios2.sort()// acomoda las listas
        }
    }
    private fun CargarRutinas(){
        var cadena: String; var id: Int
        if(validadorListas) {
            user?.let { usuario -> //para cargar las rutinas
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
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                listaRutinas1.add(cadena)
                            }else{
                                cadena = (rutina.get("id") as Long).toString() //toma el id de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                                cadena += " | Nivel: " //le pone un texto para darle orden
                                cadena += (rutina.get("nivel") as Long).toString() //toma el nivel de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                listaRutinas2.add(cadena)
                            }
                        }
                    }
            }
            listaRutinas1.sort(); listaRutinas2.sort()// acomoda las listas
            validadorListas = false //cambia el validador para que esto no se vuelva a hacer
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun CargarTiempos(){
        var sdf = SimpleDateFormat("dd")
        var diaHoy = sdf.format(Date())
        sdf = SimpleDateFormat("MM")
        var mesHoy = sdf.format(Date())
        sdf = SimpleDateFormat("yyyy")
        val anoHoy = sdf.format(Date())
        val diaSemHoy = diaSemana(diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())

        val dias: ArrayList<String> = arrayListOf<String>() //arreglo de string?
        dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add("")

        var contadorAux = 0

        for (i in diaSemHoy downTo 0) {
            var diaAux = diaHoy.toInt() - contadorAux

            if(diaAux == 0){
                val mesAux = mesHoy.toInt()

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
                    val mesAux2 = (mesHoy.toInt() - 1).toString()
                    mesHoy = "0" + mesAux2
                }else{
                mesHoy = (mesHoy.toInt() - 1).toString()
                }
                contadorAux = 0
            }

            if(diaAux < 10){
                dias[i] = "0" + diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }else {
                dias[i] = diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }
            contadorAux+= 1

        }

        var lunes = 0.0; var martes = 0; var miercoles = 0; var jueves = 0
        var viernes = 0; var sabado = 0; var domingo = 0; var tiempoAux: Int

        if(validadorListas) {
            user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario).collection("tiempos") //abre la base de datos
                    .get().addOnSuccessListener {
                        for(tiempos in it) { //por cada dia registrado
                            val idFecha = tiempos.get("idFecha") as String //toma la fecha

                            if(idFecha == dias[0]){ //si la fecha es igual al dia lunes guardado
                                lunes = (tiempos.get("minutos") as Long).toDouble() //guardara el tiempo en la variable del dia

                                tiempoAux = (tiempos.get("horas") as Long).toInt() //de horas a minutos
                                lunes += tiempoAux * 60

                                tiempoAux = (tiempos.get("segundos") as Long).toInt() //y de segundos a minutos
                                lunes += tiempoAux / 60
                            }
                            if(idFecha == dias[1]){ //y así con las demas fechas
                                martes = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); martes += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); martes += tiempoAux / 60
                            }
                            if(idFecha == dias[2]){
                                miercoles = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); miercoles += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); miercoles += tiempoAux / 60
                            }
                            if(idFecha == dias[3]){
                                jueves = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); jueves += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); jueves += tiempoAux / 60
                            }
                            if(idFecha == dias[4]){
                                viernes = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); viernes += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); viernes += tiempoAux / 60
                            }
                            if(idFecha == dias[5]){
                                sabado = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); sabado += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); sabado += tiempoAux / 60
                            }
                            if(idFecha == dias[6]){
                                domingo = (tiempos.get("minutos") as Long).toInt()
                                tiempoAux = (tiempos.get("horas") as Long).toInt(); domingo += tiempoAux * 60
                                tiempoAux = (tiempos.get("segundos") as Long).toInt(); domingo += tiempoAux / 60
                            }
                        }

                    }
            }
        }

        Toast.makeText(this, "dom: "+dias[6], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "sab: "+dias[5], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "vie: "+dias[4], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "jue: "+dias[3], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "mie: "+dias[2], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "mar: "+dias[1], Toast.LENGTH_LONG).show()
        Toast.makeText(this, "lun: "+dias[0], Toast.LENGTH_LONG).show()

        /*
        dom: 0
        sab: 5.5
        vie: 40
        jue: 15 (o 16)
        mie: 0
        mar: 128
        lun: 23
        dom?: 12

         */

    }
    private fun diaSemana(dia: Int, mes: Int, ano: Int): Int {
        val c = Calendar.getInstance()
        c.set(ano, mes, dia)

        val diaSem =  c.get(Calendar.DAY_OF_WEEK)

        if(diaSem == 4){
            return 7 //domingo
        }
        if(diaSem == 5){
            return 1 //lunes
        }
        if(diaSem == 6){
            return 2 //martes
        }
        if(diaSem == 7){
            return 3 //miercoles
        }
        if(diaSem == 1){
            return 4 //jueves
        }
        if(diaSem == 2){
            return 5 //viernes
        }
        if(diaSem == 3){
            return 6 //sabado
        }
        return 0
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

    private fun callMetasActivity() {
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }


}
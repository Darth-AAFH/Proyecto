package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

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
    private fun CargarTiempos(){

        var lunes: String; var martes: String; var miercoles: String; var jueves: String
        var viernes: String; var sabado: String; var domingo: String

        var sdf = SimpleDateFormat("dd")
        var diaHoy = sdf.format(Date())
        sdf = SimpleDateFormat("MM")
        val mesHoy = sdf.format(Date())
        sdf = SimpleDateFormat("yyyy")
        val anoHoy = sdf.format(Date())

        val diaSemHoy = diaSemana(diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())

////////////////////////////////////////////////////////////////////////////////////////////////////
        var fecha: String
        var dia: String; var mes: String; var ano: String; var diaSem: Int

        if(validadorListas) {
            user?.let { usuario -> db.collection("users").document(usuario)
                    .collection("tiempos") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (tiempo in it) { //para cada fecha

                            fecha = tiempo.get("idFecha").toString()
                            dia = fecha.split("-").toTypedArray()[0]
                            mes = fecha.split("-").toTypedArray()[1]
                            ano = fecha.split("-").toTypedArray()[2]
                            diaSem = diaSemana(dia.toInt(), mes.toInt(), ano.toInt())

                            if(ano == anoHoy){
                                if(mes == mesHoy){
                                    if((dia.toInt()+1) == diaHoy.toInt() || dia.toInt() == diaHoy.toInt()){
                                        //guardar el dato depende del día
                                            //if es de los 7 dias anteriores
                                        when(diaSem){
                                            1 -> lunes = fecha
                                            2 -> martes = fecha
                                            3 -> miercoles = fecha
                                            4 -> jueves = fecha
                                            5 -> viernes = fecha
                                            6 -> sabado = fecha
                                            7 -> domingo = fecha
                                        }

                                        if(diaSem == 1){
                                            lunes = fecha //al final abrir la bd para cada día y sacar el tiempo
                                        }
                                        if(diaSem == 2){
                                            martes = fecha
                                        }
                                        diaHoy = dia
                                    }else{
                                        //que sea 0
                                    }

                                }else{
                                    if((mes+1) == mesHoy){

                                    }
                                    if((mes+-1) == mesHoy){

                                    }
                                }
                            }


                            //mejor tomar el dia como numero y ya se es menor guardarla

                            //Toast.makeText(this, "dia: "+dia, Toast.LENGTH_SHORT).show()
                            //Toast.makeText(this, "mes: "+mes, Toast.LENGTH_SHORT).show()
                            //Toast.makeText(this, "año: "+ano, Toast.LENGTH_SHORT).show()



                            /*
                            if(fecha < 10) {
                                cadena = fecha.toString() //toma el id del ejercicio
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
                            }else{
                                cadena = fecha.toString() //toma el id del ejercicio
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
                            }

                             */
                        }
                    }
            }


            //Toast.makeText(this, "dia: "+diaHoy, Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, "mes: "+mesHoy, Toast.LENGTH_SHORT).show()
            //Toast.makeText(this, "año: "+anoHoy, Toast.LENGTH_SHORT).show()


            /*
            if(hoy == "L"){
                martes = 0; miercoles = 0; jueves = 0; viernes = 0; sabado = 0; domingo = 0
            }
            if(hoy == "M"){
                miercoles = 0; jueves = 0; viernes = 0; sabado = 0; domingo = 0
            }
            if(hoy == "I"){
                jueves = 0; viernes = 0; sabado = 0; domingo = 0
            }
            if(hoy == "J"){
                viernes = 0; sabado = 0; domingo = 0
            }
            if(hoy == "V"){
                sabado = 0; domingo = 0
            }
            if(hoy == "S"){
                domingo = 0
            }
             */
        }
    }
    private fun diaSemana(dia: Int, mes: Int, ano: Int): Int {
        val c = Calendar.getInstance()
        c.set(ano, mes, dia)

        val diaSem =  c.get(Calendar.DAY_OF_WEEK)

        if(diaSem == 4){
            return 7
        }
        if(diaSem == 5){
            return 1
        }
        if(diaSem == 6){
            return 2
        }
        if(diaSem == 7){
            return 3 //miercoles
        }
        if(diaSem == 1){
            return 4
        }
        if(diaSem == 2){
            return 5
        }
        if(diaSem == 3){
            return 6
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
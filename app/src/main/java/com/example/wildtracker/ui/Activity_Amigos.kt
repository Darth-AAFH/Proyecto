package com.example.wildtracker.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_amigos.*
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class Activity_Amigos : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance() //Instancia de firebase para traerse los datos
    private var listViewRanking: ListView?= null
    private var buttonRecargar: Button?= null //Boton para recargar los datos del activity
    private lateinit var builder: AlertDialog.Builder //Dialogo de alerta para interactuar en el activity
    private lateinit var builderStadistics: AlertDialog.Builder

    private fun CargarSeguidores () {
        //Funcion que se trae los datos de la lista de personas a las que el usuario esta siguiendo
        val values = ArrayList<String>() //Values es la lista de seguidores a que será llenada con datos de firebase
        values.clear() // Limpiamos la vista cada que se presione el boton para evitar escribir mas de alguna vez el dato
        var mListView = findViewById<ListView>(R.id.userlist) //Seteamos la lista de usuarios en una variable para poder manipularla
        mListView.emptyView //Vaciamos la lista
        mListView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, values) //Seteamos los valores de la lista a que se adapten con los valores que se cargue de la lista de usuarios
        //Accesar a la coleccion de amigos del usuario
        var listaSeguidores = ArrayList<String>()
        var perfilGet ="" //Variable para el nombre de usuario
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        MainActivity.listaSeguidores.clear() //Cada que se traiga los datos de firebase actualizara la lista de seguidores global
        //Sentencia para consultar los datos de firebase en la ruta de usuarios, el usuario actual loggeado y en sus seguidores
        db.collection("users").document(MainActivity.user!!).collection("Seguidores").get().addOnSuccessListener { result ->
            for (document in result) {
                perfilGet = document.get("Nombre").toString()
                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                    //Toast.makeText(this,perfilGet,Toast.LENGTH_LONG).show()
                    Thread.sleep(1_00)  // wait for 1 second
                    MainActivity.listaSeguidores.add(perfilGet)//Añade a la lista de seguidores los nombres encontrados en firebase
                }

            }
            Thread.sleep(1_00)  // wait for 1 second
            MainActivity.listaSeguidores.sort() // Acomoda la lista de seguidores numericamente 0,1,2....
            val array = ArrayList<String>()
            var i = MainActivity.listaSeguidores.size - 1
            while (i != -1) {
                //Acomoda el arreglo para mostrar en orden a los seguidores
                array.add(MainActivity.listaSeguidores[i])
                i--
            }

            progresDialog.dismiss()
            val arrayAdapter: ArrayAdapter<*>
            // val users = MainActivity.listaSeguidores

            // access the listView from xml file

            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, array)
            //Pinta la lista de seguidores en orden
            mListView.adapter = arrayAdapter

        }







    }

    private fun CargarSeguidoresSiguiendome () {
        //Funcion que se trae los datos de la lista de personas a las que el usuario esta siguiendo
        val values = ArrayList<String>() //Values es la lista de seguidores a que será llenada con datos de firebase
        values.clear() // Limpiamos la vista cada que se presione el boton para evitar escribir mas de alguna vez el dato
        var mListView = findViewById<ListView>(R.id.userlistSiguiendome) //Seteamos la lista de usuarios en una variable para poder manipularla
        mListView.emptyView //Vaciamos la lista
        mListView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, values) //Seteamos los valores de la lista a que se adapten con los valores que se cargue de la lista de usuarios
        //Accesar a la coleccion de amigos del usuario
        var listaSeguidores = ArrayList<String>()
        var perfilGet ="" //Variable para el nombre de usuario
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        MainActivity.ListaSiguiendome.clear() //Cada que se traiga los datos de firebase actualizara la lista de seguidores global
        //Sentencia para consultar los datos de firebase en la ruta de usuarios, el usuario actual loggeado y en sus seguidores
        db.collection("users").document(MainActivity.user!!).collection("Siguiendo").get().addOnSuccessListener { result ->
            for (document in result) {
                perfilGet = document.get("Nombre").toString()
                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                    //Toast.makeText(this,perfilGet,Toast.LENGTH_LONG).show()
                    Thread.sleep(1_00)  // wait for 1 second
                    MainActivity.ListaSiguiendome.add(perfilGet)//Añade a la lista de seguidores los nombres encontrados en firebase
                }

            }
            Thread.sleep(1_00)  // wait for 1 second
            MainActivity.ListaSiguiendome.sort() // Acomoda la lista de seguidores numericamente 0,1,2....
            val array = ArrayList<String>()
            var i = MainActivity.ListaSiguiendome.size - 1
            while (i != -1) {
                //Acomoda el arreglo para mostrar en orden a los seguidores
                array.add(MainActivity.ListaSiguiendome[i])
                i--
            }

            progresDialog.dismiss()
            val arrayAdapter: ArrayAdapter<*>
            // val users = MainActivity.listaSeguidores

            // access the listView from xml file

            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, array)
            //Pinta la lista de seguidores en orden
            mListView.adapter = arrayAdapter

        }

    }

    private fun AlertaSeguidor(perfil: String) {
        //Alert dialog para interactuar sobre un usuario ya elegido
        var perfil2 = perfil.substringAfter(" ")
        val progresDialog = ProgressDialog(this)
        // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
        var perfilGet: String
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()

        builder = AlertDialog.Builder(this)
        builder.setTitle("Siguiendo a ${perfil}")
            .setMessage("Que deseas hacer con este usuario")
            .setCancelable(true)
            .setPositiveButton("Dejar de seguir") { dialogInterface, it ->
                //Funcion para eliminar al usuario de mis amigos
                db.collection("users").document(MainActivity.user!!).collection("Seguidores").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {

                        perfilGet = document.get("Nombre").toString()

                        if(perfilGet==perfil){
                            document.reference.delete()
                        }
                    }

                }
            }
            .setNeutralButton("Ver estadisticas") { dialogInterface, it ->
                //Buscar al usuario y traerse sus estadisticas
                db.collection("users").get().addOnSuccessListener { result ->
                    for (document in result) {

                        perfilGet = document.get("Name").toString()
                        if(perfilGet==perfil){
                            //Traerse los datos necesarios
                            /*   builderStadistics = AlertDialog.Builder(this)
                               builderStadistics.setTitle("Estadisticas de $perfil")
                                   .setCancelable(true)
                                   .setNeutralButton("OK"){
                                       dialogInterface,it->*/
                            alertScrollView(perfil) //Muestra las estadisticas del usuario seleccionado

                        }
                        /* else{
                             builder = AlertDialog.Builder(this)
                             builder.setTitle("Alerta")
                                 .setMessage("No se encontro el usuario es posible que haya cambiado de nombre")
                                 .setCancelable(true)
                                 .setPositiveButton("Dejar de seguir") { dialogInterface, it ->
                                     //Funcion para eliminar al usuario de mis amigos
                                     db.collection("users").document(MainActivity.user!!).collection("Seguidores").get().addOnSuccessListener { result ->
                                         for (document in result) {

                                             perfilGet = document.get("Nombre").toString()

                                             if(perfilGet==perfil){
                                                 document.reference.delete()
                                             }
                                         }

                                     }
                                     dialogInterface.dismiss()
                                 }
                                 .setNegativeButton("Cancelar") { dialogInterface, it -> //dialogInterface.cancel()
                                     dialogInterface.dismiss()
                                 }
                                 .show()
                         } */

                    }
                }

            }
            .setNegativeButton("Ver grafica semanal") { dialogInterface, it -> //dialogInterface.cancel()
                val intent = Intent(this, VerGraficaAmigos::class.java)
                intent.putExtra("Nombre", perfil)
                intent.putExtra("Dia1", dia1)
                intent.putExtra("Dia2", dia2)
                intent.putExtra("Dia3", dia3)
                intent.putExtra("Dia4", dia4)
                intent.putExtra("Dia5", dia5)
                intent.putExtra("Dia6", dia6)
                intent.putExtra("Dia7", dia7)
                startActivity(intent)
            }

            .show()
//Toast.makeText(this,perfil,Toast.LENGTH_SHORT).show()
        progresDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Carga la barra de navegacion de la app y la lista de seguidores del mainActivity
        setContentView(R.layout.lista_amigos)
        initToolbar()
        initNavigationView()
        val arrayAdapter: ArrayAdapter<*>
        val arrayAdapterSiguiendome: ArrayAdapter<*>
        val users = MainActivity.listaSeguidores
        val siguiendome = MainActivity.ListaSiguiendome
        //Seteamos la mListView como la lista de seguidores
        var mListView = findViewById<ListView>(R.id.userlist)
        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, users)
        mListView.adapter = arrayAdapter
        CargarSeguidores() //Cargamos a los seguidores


        var mListViewSiguiendome = findViewById<ListView>(R.id.userlistSiguiendome)
        arrayAdapterSiguiendome = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, siguiendome)
        mListViewSiguiendome.adapter = arrayAdapterSiguiendome
        CargarSeguidoresSiguiendome() //Cargamos a los seguidores


        buttonRecargar = findViewById(R.id.buttonRecargar) //Boton para recargar la lista
        // listViewRanking = findViewById(R.id.listViewRanking)

        //  CargarSeguidores()

        buttonRecargar!!.setOnClickListener{////////////////////////////////////
            CargarSeguidores()
        }
        mListView!!.setOnItemClickListener  { parent, view, position, id ->
            //Al dar click en un elemento de la vista se traere el texto que se clickeo y mandara llamar a AlertaSeguidor para interactuar con ese elemento
            var Perfil:String  =  MainActivity.listaSeguidores[(MainActivity.listaSeguidores!!.size.toInt()- position.toInt())-1]
            CargarDatosGrafica(Perfil)
            AlertaSeguidor(Perfil )
        }
        mListViewSiguiendome!!.setOnItemClickListener { parent, view, position, id ->

            var Perfil:String  =  MainActivity.ListaSiguiendome[(MainActivity.ListaSiguiendome!!.size.toInt()- position.toInt())-1]
            AlertaSiguiendome(Perfil )
        }



    }

    private fun AlertaSiguiendome(perfil: String) {
        //Alert dialog para interactuar sobre un usuario ya elegido
        var perfil2 = perfil.substringAfter(" ")
        val progresDialog = ProgressDialog(this)
        // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
        var perfilGet: String
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()

        builder = AlertDialog.Builder(this)
        builder.setTitle("Siguiendo a ${perfil}")
            .setMessage("Que deseas hacer con este usuario")
            .setCancelable(true)
            .setPositiveButton("Eliminar") { dialogInterface, it ->
                //Funcion para eliminar al usuario de mis amigos
                db.collection("users").document(MainActivity.user!!).collection("Siguiendome").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {
                        perfilGet = document.get("Nombre").toString()
                        if(perfilGet==perfil){
                            document.reference.delete()
                        }
                    }

                }
            }
            .setNeutralButton("Ver estadisticas") { dialogInterface, it ->
                //Buscar al usuario y traerse sus estadisticas
                db.collection("users").get().addOnSuccessListener { result ->
                    for (document in result) {

                        perfilGet = document.get("Name").toString()
                        if(perfilGet==perfil){
                            //Traerse los datos necesarios
                            /*   builderStadistics = AlertDialog.Builder(this)
                               builderStadistics.setTitle("Estadisticas de $perfil")
                                   .setCancelable(true)
                                   .setNeutralButton("OK"){
                                       dialogInterface,it->*/
                            alertScrollView(perfil) //Muestra las estadisticas del usuario seleccionado

                        }
                        /* else{
                             builder = AlertDialog.Builder(this)
                             builder.setTitle("Alerta")
                                 .setMessage("No se encontro el usuario es posible que haya cambiado de nombre")
                                 .setCancelable(true)
                                 .setPositiveButton("Dejar de seguir") { dialogInterface, it ->
                                     //Funcion para eliminar al usuario de mis amigos
                                     db.collection("users").document(MainActivity.user!!).collection("Seguidores").get().addOnSuccessListener { result ->
                                         for (document in result) {

                                             perfilGet = document.get("Nombre").toString()

                                             if(perfilGet==perfil){
                                                 document.reference.delete()
                                             }
                                         }

                                     }
                                     dialogInterface.dismiss()
                                 }
                                 .setNegativeButton("Cancelar") { dialogInterface, it -> //dialogInterface.cancel()
                                     dialogInterface.dismiss()
                                 }
                                 .show()
                         } */

                    }
                }

            }
            .setNegativeButton("Ver más") { dialogInterface, it -> //dialogInterface.cancel()
                val intent = Intent(this, RankingActivity::class.java)
                startActivity(intent)
            }

            .show()
//Toast.makeText(this,perfil,Toast.LENGTH_SHORT).show()
        progresDialog.dismiss()
    }

    var dia1: Double = 0.0; var dia2: Double = 0.0; var dia3: Double = 0.0; var dia4: Double = 0.0
    var dia5: Double = 0.0; var dia6: Double = 0.0; var dia7: Double = 0.0
    var tiempoAux: Double = 0.0
    private fun CargarDatosGrafica(perfil: String) {
        dia1 = 0.0; dia2 = 0.0; dia3 = 0.0; dia4 = 0.0; dia5 = 0.0; dia6 = 0.0; dia7 = 0.0
        var sdf = SimpleDateFormat("dd")
        var diaHoy = sdf.format(Date()) //se obtiene e dia actual
        sdf = SimpleDateFormat("MM")
        var mesHoy = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy = sdf.format(Date()) //se obiene el año actual

        val dias: java.util.ArrayList<String> = arrayListOf<String>() //arreglo de string?
        dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add("") //se llena el arreglo de dias con datos vacios

        var contadorAux = 0 //un contador auxiliar para encontrar los dias pasados

        for (i in 7 downTo 0) { //para obtener los ejercicios de la semana pasada
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

            if(diaAux < 10){ //se guardan en el arreglo de días los dias pasados y se les da el formato
                dias[i] = "0" + diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }else {
                dias[i] = diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }

            contadorAux+= 1 //se le agrega al contador un numero más para retorceder más días
        }

        var perfilGet: String
        var email = ""

        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                perfilGet = document.get("Name").toString()
                if(perfilGet == perfil){
                    email = document.get("email").toString()

                    MainActivity.user?.let { usuario -> //trae los tiempos segun el día
                        db.collection("users").document(email).collection("tiempos") //abre la base de datos
                            .get().addOnSuccessListener {
                                for(tiempos in it) { //por cada dia registrado
                                    val idFecha = tiempos.get("idFecha") as String //toma la fecha

                                    if(idFecha == dias[1]){ //si la fecha es igual al dia 1 para la grafica
                                        dia1 = (tiempos.get("minutos") as Long).toDouble() //guardara el tiempo en la variable del dia

                                        tiempoAux = (tiempos.get("horas") as Long).toDouble() //de horas a minutos
                                        dia1 += tiempoAux * 60

                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble() //y de segundos a minutos
                                        dia1 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[2]){ //y así con las demas fechas
                                        dia2 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia2 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia2 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[3]){
                                        dia3 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia3 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia3 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[4]){
                                        dia4 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia4 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia4 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[5]){
                                        dia5 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia5 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia5 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[6]){
                                        dia6 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia6 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia6 += tiempoAux / 60
                                    }
                                    if(idFecha == dias[7]){
                                        dia7 = (tiempos.get("minutos") as Long).toDouble()
                                        tiempoAux = (tiempos.get("horas") as Long).toDouble(); dia7 += tiempoAux * 60
                                        tiempoAux = (tiempos.get("segundos") as Long).toDouble(); dia7 += tiempoAux / 60
                                    }
                                }
                            }
                    }
                }
            }
        }
    }


    private fun initToolbar() {
        //Inicia el toolbar para el activity Seguidores
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Siguiendo"
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
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_ranking->callRankingActivity()
            R.id.nav_musica ->callMusica()
            R.id.nav_amigos ->callAmigosActivity()
            R.id.Settings->callAjustesActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }
    private fun callRankingActivity() {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
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

    private fun callChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }

    private fun callMetasActivity() {
        val intent = Intent(this, MetasActivity::class.java)
        startActivity(intent)
    }

    fun signOut() {
        //Cierra sesion del usuario actual
        LoginActivity.useremail = ""
        FirebaseAuth.getInstance().signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        deleteAppData() //Elimina los datos de la app
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



    fun alertScrollView(perfil: String) {
        //Estadisticas del usuario seleccionado
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val myScrollView: View = inflater.inflate(R.layout.seguidores_estadisticas, null, false)

        val tv = myScrollView.findViewById<View>(R.id.textViewWithScrollSta) as TextView

        // Initializing a blank textview so that we can just append a text later
        tv.text = ""
        var nombre = perfil
        var puntos = " "
        var altura =" "
        var peso = ""
        var contador =1
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                try {
                    if(perfil==document.get("Name"))
                        contador++
                    if(perfil==document.get("Name")&& contador<=2) {
                        puntos = document.get("puntosTotales").toString()
                        peso = document.get("peso").toString()
                        altura = document.get("altura").toString()
                        // myScrollView.setVisibility(View.GONE);
                        Thread.sleep(200)
                        tv.append("Nombre : ${document.get("Name").toString()}\n")
                        myScrollView.setVisibility(View.VISIBLE);
                        if(puntos==null||puntos=="null"){
                            puntos ="NR"
                        }
                        if(peso==null||peso=="null"){
                            peso ="NR"
                        }
                        if(altura==null||altura=="null"){
                            altura ="NR"
                        }
                        tv.append("Puntos Totales : $puntos\n")
                        tv.append("Peso : ${peso}\n")
                        tv.append("Altura : ${altura}\n")
                    }

                }
                catch (e:Exception){

                }
            }
            if(progresDialog.isShowing) {
                progresDialog.dismiss()
            }
        }
        AlertDialog.Builder(this).setView(myScrollView)
            .setTitle("Informacion de personas")
            .setNeutralButton("Ok") {  dialog, id -> dialog.cancel() }.show()



    }



}
package com.example.wildtracker.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
import com.google.firebase.firestore.SetOptions
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class SolicitudesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance() //Instancia de firebase para traerse los datos
    private var listViewRanking: ListView?= null
    private var buttonRecargar: Button?= null //Boton para recargar los datos del activity
    private lateinit var builder: AlertDialog.Builder //Dialogo de alerta para interactuar en el activity
    private lateinit var builderStadistics: AlertDialog.Builder

    private fun CargarSolicitidesEnviadas () {
        //Funcion que se trae los datos de la lista de personas a las que el usuario esta siguiendo
        val values = ArrayList<String>() //Values es la lista de seguidores a que será llenada con datos de firebase
        values.clear() // Limpiamos la vista cada que se presione el boton para evitar escribir mas de alguna vez el dato
        var mListView = findViewById<ListView>(R.id.solicitudesList) //Seteamos la lista de usuarios en una variable para poder manipularla
        mListView.emptyView //Vaciamos la lista
        mListView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, values) //Seteamos los valores de la lista a que se adapten con los valores que se cargue de la lista de usuarios
        //Accesar a la coleccion de amigos del usuario
        var listaSeguidores = ArrayList<String>()
        var perfilGet ="" //Variable para el nombre de usuario
        var origen =""
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        MainActivity.listaSolicitudesEnviadas.clear() //Cada que se traiga los datos de firebase actualizara la lista de seguidores global
        //Sentencia para consultar los datos de firebase en la ruta de usuarios, el usuario actual loggeado y en sus seguidores
        db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
            for (document in result) {
                perfilGet = document.get("Nombre").toString()
                origen = document.get("Paraquien").toString()
                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                    //Toast.makeText(this,perfilGet,Toast.LENGTH_LONG).show()
                    Thread.sleep(1_00)  // wait for 1 second
                    if(PerfilActivity.NombreUsuario!=origen){
                    MainActivity.listaSolicitudesEnviadas.add(origen)//Añade a la lista de seguidores los nombres encontrados en firebase
                    }
                }

            }
            Thread.sleep(1_00)  // wait for 1 second
            MainActivity.listaSolicitudesEnviadas.sort() // Acomoda la lista de seguidores numericamente 0,1,2....
            val array = ArrayList<String>()
            var i = MainActivity.listaSolicitudesEnviadas.size - 1
            while (i != -1) {
                //Acomoda el arreglo para mostrar en orden a los seguidores
                array.add(MainActivity.listaSolicitudesEnviadas[i])
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
    private fun CargarSolicitidesRecibidas () {
        //Funcion que se trae los datos de la lista de personas a las que el usuario esta siguiendo
        val values = ArrayList<String>() //Values es la lista de seguidores a que será llenada con datos de firebase
        values.clear() // Limpiamos la vista cada que se presione el boton para evitar escribir mas de alguna vez el dato
        var mListView = findViewById<ListView>(R.id.solicitudesListRecibidas) //Seteamos la lista de usuarios en una variable para poder manipularla
        mListView.emptyView //Vaciamos la lista
        mListView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, values) //Seteamos los valores de la lista a que se adapten con los valores que se cargue de la lista de usuarios
        //Accesar a la coleccion de amigos del usuario
        var listaSeguidores = ArrayList<String>()
        var perfilGet ="" //Variable para el nombre de usuario
        var Paraquien =""
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        MainActivity.listaSolicitudesRecibidas.clear() //Cada que se traiga los datos de firebase actualizara la lista de seguidores global
        //Sentencia para consultar los datos de firebase en la ruta de usuarios, el usuario actual loggeado y en sus seguidores
        db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
            for (document in result) {
                Paraquien = document.get("Paraquien").toString()
                perfilGet =  document.get("Nombre").toString() //De quien es
                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                    //Toast.makeText(this,perfilGet,Toast.LENGTH_LONG).show()
                    Thread.sleep(1_00)  // wait for 1 second
                    if(PerfilActivity.NombreUsuario == Paraquien){
                    MainActivity.listaSolicitudesRecibidas.add(perfilGet)//Añade a la lista de seguidores los nombres encontrados en firebase
                    }
                }

            }
            Thread.sleep(1_00)  // wait for 1 second
            MainActivity.listaSolicitudesRecibidas.sort() // Acomoda la lista de seguidores numericamente 0,1,2....
            val array = ArrayList<String>()
            var i = MainActivity.listaSolicitudesRecibidas.size - 1
            while (i != -1) {
                //Acomoda el arreglo para mostrar en orden a los seguidores
                array.add(MainActivity.listaSolicitudesRecibidas[i])
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

    private fun AlertaSolicitudEnviada(perfil: String) {
        //Alert dialog para interactuar sobre un usuario ya elegido
        var perfil2 = perfil.substringAfter(" ")
        val progresDialog = ProgressDialog(this)
        // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
        var perfilGet: String
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()

        builder = AlertDialog.Builder(this)
        builder.setTitle("Enviaste una solicitud a ${perfil}")
            .setMessage("Que deseas hacer ?")
            .setCancelable(true)
           /* .setPositiveButton("Aceptar") { dialogInterface, it ->
                //Funcion para eliminar al usuario de mis amigos
                db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {

                        perfilGet = document.get("Nombre").toString()
                        if(perfilGet==perfil){
                            var documentRef = document.reference
                            val data = hashMapOf("SolicitudAceptada" to true)

                            db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").document(document.id).set(data, SetOptions.merge())
                        }
                    }

                }
            }*/
            .setNeutralButton("Eliminar") { dialogInterface, it ->
                //Buscar al usuario y traerse sus estadisticas

                db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {
                        perfilGet = document.get("Nombre").toString()
                        if(perfilGet==perfil){
                            var documentRef = document.reference

                            db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").document(
                                document.id
                            ).delete()
                        }
                    }

                }


                           // alertScrollView(perfil) //Muestra las estadisticas del usuario seleccionado

                        }
            .setNegativeButton("Volver") { dialogInterface, it -> //dialogInterface.cancel()
               dialogInterface.cancel()
            }

            .show()
//Toast.makeText(this,perfil,Toast.LENGTH_SHORT).show()
        progresDialog.dismiss()
    }



    private fun AlertaSolicitudRecibida(perfil: String) {
        //Alert dialog para interactuar sobre un usuario ya elegido
        var perfil2 = perfil.substringAfter(" ")
        val progresDialog = ProgressDialog(this)
        // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
        var perfilGet: String
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()

        builder = AlertDialog.Builder(this)
        builder.setTitle("Tienes una solicitud de seguidor de ${perfil}")
            .setMessage("Que deseas hacer ?")
            .setCancelable(true)
            .setPositiveButton("Aceptar") { dialogInterface, it ->
                //Funcion para eliminar al usuario de mis amigos
                db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {
                        perfilGet = document.get("Nombre").toString()
                        if(perfilGet==perfil){
                            var documentRef = document.reference
                            val data = hashMapOf("SolicitudAceptada" to true)

                            db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").document(document.id).set(data, SetOptions.merge())

                        }
                    }

                }
            }
            .setNeutralButton("Eliminar") { dialogInterface, it ->
                //Buscar al usuario y traerse sus estadisticas

                db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").get().addOnSuccessListener { result ->
                    //Consulta en la base de datos los usuarios que coicidan con el nombre de usuario a dejar de seguir, cuando lo encuentra lo elimina
                    for (document in result) {
                        perfilGet = document.get("Nombre").toString()
                        if(perfilGet==perfil){
                            var documentRef = document.reference

                            db.collection("users").document(MainActivity.user!!).collection("SolicitudesAmistad").document(
                                document.id
                            ).delete()
                        }
                    }

                }


                // alertScrollView(perfil) //Muestra las estadisticas del usuario seleccionado

            }
            .setNegativeButton("Volver") { dialogInterface, it -> //dialogInterface.cancel()
                dialogInterface.cancel()
            }

            .show()
//Toast.makeText(this,perfil,Toast.LENGTH_SHORT).show()
        progresDialog.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Carga la barra de navegacion de la app y la lista de seguidores del mainActivity
        setContentView(R.layout.activity_solicitudes)
        initToolbar()
        initNavigationView()
        val arrayAdapter: ArrayAdapter<*>
        val arrayAdapterRecibidas: ArrayAdapter<*>
        val enviadas = MainActivity.listaSolicitudesEnviadas
        val recibidas = MainActivity.listaSolicitudesEnviadas
        //Seteamos la mListView como la lista de seguidores
        var mListView = findViewById<ListView>(R.id.solicitudesList)
        arrayAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, enviadas)
        mListView.adapter = arrayAdapter

        var mListViewRecibidas = findViewById<ListView>(R.id.solicitudesListRecibidas)
        arrayAdapterRecibidas = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, recibidas)
        mListViewRecibidas.adapter = arrayAdapterRecibidas

        CargarSolicitidesEnviadas() //Cargamos a los seguidores
        CargarSolicitidesRecibidas()



       // CargarSeguidoresSiguiendome() //Cargamos a los seguidores


        buttonRecargar = findViewById(R.id.buttonRecargar) //Boton para recargar la lista
        // listViewRanking = findViewById(R.id.listViewRanking)

        //  CargarSeguidores()

        buttonRecargar!!.setOnClickListener{////////////////////////////////////
            CargarSolicitidesEnviadas()
            CargarSolicitidesRecibidas()
        }
        mListView!!.setOnItemClickListener  { parent, view, position, id ->
            //Al dar click en un elemento de la vista se traere el texto que se clickeo y mandara llamar a AlertaSeguidor para interactuar con ese elemento
            var Perfil:String  =  MainActivity.listaSolicitudesEnviadas[(MainActivity.listaSolicitudesEnviadas.size.toInt()- position.toInt())-1]
            AlertaSolicitudEnviada(Perfil )
        }

        mListViewRecibidas!!.setOnItemClickListener { parent, view, position, id ->

            var Perfil:String  =  MainActivity.listaSolicitudesRecibidas[(MainActivity.listaSolicitudesRecibidas.size.toInt()- position.toInt())-1]
            AlertaSolicitudRecibida(Perfil )
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
            R.id.nav_seguimiento-> callSeguimientoActivity()
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





}
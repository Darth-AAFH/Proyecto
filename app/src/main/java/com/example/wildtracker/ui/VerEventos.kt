package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_plantillas.*
import kotlinx.android.synthetic.main.activity_ver_eventos.*

class VerEventos : AppCompatActivity(),OnNavigationItemSelectedListener {

    var dia = 0; var mes = 0; var ano = 0

    var listaEventosVista = listOf<eventos>()

    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_eventos)

        val b = intent.extras //b toma los datos mandados de la otra activity
        if (b != null) {
            dia = b.getInt("Dia")
            mes = b.getInt("Mes")
            ano = b.getInt("Ano")
        }

        initToolbar()
        initNavigationView()

        cargarEventos()
        mostrarLista()
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
    fun cargarEventos(){
        var cont = 0

        for(i in MainActivity.listaEventos1){
            if(i == (dia.toString() + "-" + mes.toString() + "-" + ano.toString())){
                val nombre = MainActivity.listaRutinasATrabajarAux[cont].split(" | ").toTypedArray()[1]
                val descripcion = MainActivity.listaRutinasATrabajarAux[cont].split(" | ").toTypedArray()[2]

                var acomodo: eventos
                acomodo = eventos(nombre, descripcion, R.drawable.rutina)

                val listaEventosVistaAux = listOf(acomodo)
                listaEventosVista += listaEventosVistaAux
            }
            cont++
        }

        cont = 0
        for(i in MainActivity.listaEventos2){
            val arreglo: Array<String?>
            arreglo = i.split(",").toTypedArray() //toma las fechas separadas

            for(j in arreglo){
                if(j == (dia.toString() + "-" + mes.toString() + "-" + ano.toString())){
                    val nombre = MainActivity.listaAllMetas[cont].split(" | ").toTypedArray()[0]
                    val descripcion = MainActivity.listaAllMetas[cont].split(" | ").toTypedArray()[2]

                    var acomodo: eventos
                    acomodo = eventos(nombre, descripcion, R.drawable.excersice_icon)

                    val listaEventosVistaAux = listOf(acomodo)
                    listaEventosVista += listaEventosVistaAux
                }
            }

            cont++
        }

    }

    fun mostrarLista(){
        if(listaEventosVista.isEmpty()){
            textViewAyudaEventos.visibility = View.VISIBLE
        }

        val adapter = eventosAdapter(this, listaEventosVista)
        listViewEventos!!.adapter = adapter
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = dia.toString() + "-" + mes.toString() + "-" + ano.toString()
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerlayout)!!
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_musica ->callMusica()
            R.id.nav_metas -> callMetasActivity()
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

    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
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

    private fun callMetasActivity() {
        val intent = Intent(this, MetasActivity::class.java)
        startActivity(intent)
    }
    fun callSignOut(view: View) {
        signOut()
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
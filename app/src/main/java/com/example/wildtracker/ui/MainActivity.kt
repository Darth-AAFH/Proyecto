package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.method.TextKeyListener.clear
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.LoginActivity.Companion.starts
import com.example.wildtracker.LoginActivity.Companion.useremail
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.RepoManager.clear
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

    var buttonPruebaBD: Button?= null ////////////////////////

    private var grafica: GraphicalView? = null
    private var datosGrafica: XYSeries? = null
    private val informacionGrafica = XYMultipleSeriesDataset()
    private val renderGrafica = XYMultipleSeriesRenderer()
    private var seriesRendererGrafica: XYSeriesRenderer? = null

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private fun insigniasRutinas() {
        var nivel: Int
        var nombre: String
        var tiempo: Int

        user?.let { usuario -> //para cargar las rutinas de manera ordenada (de mayor a menor nivel y poniendole las insignias)
            db.collection("users").document(usuario).collection("rutinas") //abre la base de datos
                .get().addOnSuccessListener {
                    for (rutina in it) { //para cada rutina

                        nivel = (rutina.get("nivel") as Long).toInt()
                        nombre = rutina.get("nombre") as String
                        tiempo = (rutina.get("minutos") as Long).toInt()
                        tiempo += (rutina.get("horas") as Long).toInt() / 60
                        tiempo += (rutina.get("segundos") as Long).toInt() * 60

                        var cadena = "Nivel: " + nivel + " | " + nombre + " | Tiempo: " + tiempo
                    }
                }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    companion object{
        val auth: String? = FirebaseAuth.getInstance().currentUser?.email
        var user =  auth

        //
        var listaRutinas1 = ArrayList<String>()
        var listaRutinas2 = ArrayList<String>()
        var listaEjercicios1 = ArrayList<String>()
        var listaEjercicios2 = ArrayList<String>()
        var validadorListas = true
        //
        var lunes: Double = 0.0; var martes: Double = 0.0; var miercoles: Double = 0.0; var jueves : Double = 0.0
        var viernes: Double = 0.0; var sabado: Double = 0.0; var domingo: Double = 0.0;
        //

        var listaRutinas = ArrayList<String>()
        var listaEjercicios = ArrayList<String>()
        var validadorAcomodo = true
        val listaRanking = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initToolbar()
        initNavigationView()

        iniciarGrafica()

        buttonPruebaBD = findViewById(R.id.buttonPruebaBD)//////////////////////

        buttonPruebaBD!!.setOnClickListener{////////////////////////////////////
            insigniasRutinas()
        }
    }

    private fun cargarGrafica() {
        datosGrafica = XYSeries("Simple Data")
        informacionGrafica.addSeries(datosGrafica)

        datosGrafica!!.add(0.0, lunes)
        datosGrafica!!.add(1.0, martes)
        datosGrafica!!.add(2.0, miercoles)
        datosGrafica!!.add(3.0, jueves)
        datosGrafica!!.add(4.0, viernes)
        datosGrafica!!.add(5.0, sabado)
        datosGrafica!!.add(6.0, domingo)

        seriesRendererGrafica = XYSeriesRenderer()
        renderGrafica.addSeriesRenderer(seriesRendererGrafica)
    }
    private fun iniciarGrafica() {
        val layout = findViewById<LinearLayout>(R.id.chart)
        if (grafica == null) {
            cargarGrafica()
            grafica = ChartFactory.getCubeLineChartView(this, informacionGrafica, renderGrafica, 0f)
            layout.addView(grafica)
        } else {
            grafica!!.repaint()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
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

        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
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
        val intent = Intent(this, RecordActivity::class.java)
        startActivity(intent)
    }
}
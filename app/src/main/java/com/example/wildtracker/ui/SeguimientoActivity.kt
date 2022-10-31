package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.*

class SeguimientoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CalendarView.OnDateChangeListener{
    private lateinit var drawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

    private lateinit var vistaCalendario: CalendarView
    //private var buttonTest2: Button ?= null///////////////////////////////////////////////////////////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)
        //initToolbar()
        //initNavigationView()

        vistaCalendario = findViewById(R.id.vistaCalendario)
        //buttonTest2 = findViewById(R.id.buttonTest2)////////////////////////////////////////////////////

        cargarMetas()

        vistaCalendario.setOnDateChangeListener(this)

        //buttonTest2!!.setOnClickListener{
            //Toast.makeText(this, "Dato de suma:" + datoDeSuma, Toast.LENGTH_SHORT).show()
        //}
    }

    var datoDeSuma = 0

    @SuppressLint("SimpleDateFormat")
    private fun cargarMetas() {
        //var peso= false

        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual
        val diaHoy = diaHoy2.toInt(); val mesHoy = mesHoy2.toInt(); val anoHoy = anoHoy2.toInt()

        var fechaFinal: String
        var diaF: Int; var mesF: Int; var anoF: Int

        var diasTotales: Int; var diasxSemana = 0; var diasATrabajar: Int

        //var datoDeSuma = 0

        MainActivity.user?.let { usuario -> //para cargar las metas
            db.collection("users").document(usuario)
                .collection("metas") //abre la base de datos
                .get().addOnSuccessListener {
                    for (meta in it) { //para cada meta

                        fechaFinal = meta.get("fechaFinal") as String
                        diaF = fechaFinal.split("/").toTypedArray()[0].toInt()
                        mesF = fechaFinal.split("/").toTypedArray()[1].toInt()
                        anoF = fechaFinal.split("/").toTypedArray()[2].toInt()

                        //primero se obtiene la diferencia de dias entre las dos fechas
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
                                        diasTotales += 31 - diaHoy
                                    }else{
                                        if(mesHoy == 2){
                                            diasTotales += 28 - diaHoy
                                        }else{
                                            diasTotales += 30 - diaHoy
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
                                        diasTotales -= 31 - diaHoy
                                    }else{
                                        if(mesHoy == 2){
                                            diasTotales -= 28 - diaHoy
                                        }else{
                                            diasTotales -= 30 - diaHoy
                                        }
                                    }
                                }
                            }
                        }

                        if(meta.get("lunes") as Boolean){diasxSemana += 1}
                        if(meta.get("martes") as Boolean){diasxSemana += 1}
                        if(meta.get("miercoles") as Boolean){diasxSemana += 1}
                        if(meta.get("jueves") as Boolean){diasxSemana += 1}
                        if(meta.get("viernes") as Boolean){diasxSemana += 1}
                        if(meta.get("sabado") as Boolean){diasxSemana += 1}
                        if(meta.get("domingo") as Boolean){diasxSemana += 1}

                        diasATrabajar = diasTotales/diasxSemana

                        datoDeSuma = ((meta.get("datoFinal") as Long).toInt() - (meta.get("datoInicial") as Long).toInt())/diasATrabajar
                    }
                }
        }

    }

    override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {

        val builder = AlertDialog.Builder(this) //alertaTareas
        val items = arrayOfNulls<CharSequence>(3)
        items[0] = "Agregar rutina única"; items[1] = "Ver eventos"; items[2] = "Cancelar"

        var dia: Int = p1; var mes: Int = p2 + 1; var ano: Int = p3

        val alertaTareas = AlertDialog.Builder(this)
        alertaTareas.setTitle("Seleccionar una tarea")

        alertaTareas.setItems(items, DialogInterface.OnClickListener() { dialogInterface, i ->
            if(i == 0){ //mandar a lista de rutinas
                val intent = Intent(this@SeguimientoActivity, SeleccionadorRutina::class.java)

                val bundle = Bundle()
                bundle.putInt("dia", dia); bundle.putInt("mes", mes); bundle.putInt("ano", ano)

                intent.putExtras(bundle)
                startActivity(intent)
            }else{
                if(i == 1){ //muestra la rutina de ese día y la puede quitar
                    val intent = Intent(this@SeguimientoActivity, MetasActivity::class.java)

                    val bundle = Bundle()
                    bundle.putInt("dia", dia); bundle.putInt("mes", mes); bundle.putInt("ano", ano)

                    intent.putExtras(bundle)
                    startActivity(intent)
                }else{}
            }
        })

        val mostrarAlerta = alertaTareas.create()
        mostrarAlerta.show()
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Seguimiento"
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
            R.id.nav_maps -> callMetasActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            
            R.id.nav_musica ->callMusica()
            R.id.nav_amigos ->callAmigosActivity()

        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
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
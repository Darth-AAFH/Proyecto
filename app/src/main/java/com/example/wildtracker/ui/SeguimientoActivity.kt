package com.example.wildtracker.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
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
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class SeguimientoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var drawer: DrawerLayout

    var calendario: CompactCalendarView? = null
    private val dateFormatMonth = SimpleDateFormat("MMMM - yyyy", Locale.getDefault())

    private val db = FirebaseFirestore.getInstance()

    fun cargarRutinasProgramadas(){
        for(i in MainActivity.listaEventos1){ //para agregar las rutinas a trabajar en el calendario
            var dia = i.split("-").toTypedArray()[0].toLong()
            var mes = i.split("-").toTypedArray()[1].toLong()
            var ano = i.split("-").toTypedArray()[2].toLong()

            dia = dia-1L; mes = mes-1L; ano = ano-1970L
            val tiempoMil = (dia*86400L + mes*2629743L + ano*31556926L)*1000L + 36000000L

            var evento = Event(Color.WHITE  , tiempoMil, "")

            val random = Random().nextInt(10)
            if(random == 0){evento = Event(Color.BLUE, tiempoMil, "")}
            if(random == 1){evento = Event(Color.BLACK, tiempoMil, "")}
            if(random == 2){evento = Event(Color.CYAN, tiempoMil, "")}
            if(random == 3){evento = Event(Color.DKGRAY, tiempoMil, "")}
            if(random == 4){evento = Event(Color.GRAY, tiempoMil, "")}
            if(random == 5){evento = Event(Color.GREEN, tiempoMil, "")}
            if(random == 6){evento = Event(Color.LTGRAY, tiempoMil, "")}
            if(random == 7){evento = Event(Color.MAGENTA, tiempoMil, "")}
            if(random == 8){evento = Event(Color.RED, tiempoMil, "")}
            if(random == 9){evento = Event(Color.YELLOW, tiempoMil, "")}

            calendario!!.addEvent(evento)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento)
        initToolbar()
        initNavigationView()

        calendario = findViewById(R.id.calendario2) as CompactCalendarView
        calendario!!.setUseThreeLetterAbbreviation(true)

        cargarRutinasProgramadas()

        calendario!!.setListener(object : CompactCalendarViewListener {
            override fun onDayClick(fechaSeleccionada: Date) {
                val context = applicationContext

                val dia = (fechaSeleccionada.toString()).split(" ").toTypedArray()[2].toInt()
                val ano = (fechaSeleccionada.toString()).split(" ").toTypedArray()[5].toInt()

                val mesAux = (fechaSeleccionada.toString()).split(" ").toTypedArray()[1]
                var mes = 0
                if(mesAux == "Jan"){ mes = 1}; if(mesAux == "Feb"){ mes = 2}; if(mesAux == "Mar"){ mes = 3}
                if(mesAux == "Apr"){ mes = 4}; if(mesAux == "May"){ mes = 5}; if(mesAux == "Jun"){ mes = 6}
                if(mesAux == "Jul"){ mes = 7}; if(mesAux == "Aug"){ mes = 8}; if(mesAux == "Sep"){ mes = 9}
                if(mesAux == "Oct"){ mes = 10}; if(mesAux == "Nov"){ mes = 11}; if(mesAux == "Dec"){ mes = 12}

                crearAlerta(dia, mes, ano)
            }
            override fun onMonthScroll(firstDayOfNewMonth: Date) {

            }
        })
    }

    fun crearAlerta(dia: Int, mes: Int, ano: Int){
        if(diaAnterior(dia, mes, ano)) {
            Toast.makeText(this, "No puede seleccionar una fecha pasada", Toast.LENGTH_SHORT).show()
        }else{
            val items = arrayOfNulls<CharSequence>(3)
            items[0] = "Agregar rutina única"; items[1] = "Borrar rutina programada"; items[2] = "Cancelar"

            val alertaTareas = AlertDialog.Builder(this)
            alertaTareas.setTitle("Seleccionar una tarea")

            alertaTareas.setItems(items, DialogInterface.OnClickListener() { dialogInterface, i ->
                if (i == 0) { //mandar a lista de rutinas
                    val intent = Intent(this@SeguimientoActivity, SeleccionadorRutina::class.java)

                    val bundle = Bundle()
                    bundle.putInt("dia", dia); bundle.putInt("mes", mes); bundle.putInt("ano", ano)

                    intent.putExtras(bundle)
                    startActivity(intent)
                } else {
                    if (i == 1) { //elimina la rutina programada de ese dia
                        var fecha = dia.toString() + "-" + mes.toString() + "-" + ano.toString()

                        MainActivity.user?.let { usuario ->
                            db.collection("users").document(usuario).collection("rutinasAtrabajar")
                                .document(fecha).delete()
                        }

                        val posicion: Int
                        posicion = MainActivity.listaEventos1.indexOf(fecha) //borra la rutina de las listas en base a la fecha
                        if(posicion != -1){
                            MainActivity.listaEventos1.removeAt(posicion)
                            MainActivity.listaRutinasATrabajarAux.removeAt(posicion)
                        }

                        for(i in MainActivity.listaRutinasATrabajar){ //busca la rutina si es que esta se hace hoy para quitarla de la otra lista
                            val fecha2 = i.split("Fecha: ").toTypedArray()[1]
                            if(fecha2 == fecha){
                                MainActivity.listaRutinasATrabajar.clear()
                            }
                        }
                    }
                }
            })

            val mostrarAlerta = alertaTareas.create()
            mostrarAlerta.show()
        }
    }

    fun diaAnterior(dia: Int, mes: Int, ano: Int): Boolean{
        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt(); val mesHoy = mesHoy2.toInt(); val anoHoy = anoHoy2.toInt() //obtiene la fecha actual en enteros

        if(dia < diaHoy && mes == mesHoy && ano == anoHoy || mes < mesHoy && ano == anoHoy || ano < anoHoy){
            return true
        }else{
            return false
        }
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Seguimiento"
        setSupportActionBar(toolbar)

        com.example.wildtracker.ui.drawer = findViewById(R.id.drawerlayout)!!
        val toggle = ActionBarDrawerToggle(
            this, com.example.wildtracker.ui.drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        com.example.wildtracker.ui.drawer.addDrawerListener(toggle)
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

        com.example.wildtracker.ui.drawer.closeDrawer(GravityCompat.START) // cerrar menu

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
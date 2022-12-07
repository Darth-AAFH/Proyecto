package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
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

class SeleccionadorRutina : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val db = FirebaseFirestore.getInstance()

    var listViewRutinas2: ListView?= null

    var dia = 0; var mes  = 0; var ano = 0

    private fun CargarListas(){ //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)

            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)

            MainActivity.listaRutinasVista = MainActivity.listaRutinasVista1
            MainActivity.listaRutinasVista.addAll(MainActivity.listaRutinasVista2)

            MainActivity.validadorAcomodo = false
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinas)
        listViewRutinas2!!.adapter = adapter //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionador_rutina)
        initToolbar()
        initNavigationView()

        val b = intent.extras //b toma la fecha en que se trabajara la rutina
        if (b != null) {
            dia = b.getInt("dia")
            mes = b.getInt("mes")
            ano = b.getInt("ano")
        }

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)

        CargarListas()
        Toast.makeText(this, "Seleccione un rutina", Toast.LENGTH_SHORT).show()

        listViewRutinas2!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            val nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]
            val nivelAux = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[2]

            val arreglo: Array<String?>
            arreglo = nivelAux.split(" ").toTypedArray()

            val fecha = dia.toString() + "-" + mes.toString() + "-" + ano.toString()

            MainActivity.user?.let{ usuario ->
                db.collection("users").document(usuario).collection("rutinasAtrabajar")
                    .document(fecha).set(
                        hashMapOf(
                            "dia" to dia,
                            "mes" to mes,
                            "ano" to ano,
                            "idRutina" to num,
                            "nombre" to nombre
                        )
                    )
            }

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

            var cadena = num.toString() + " | " + nombre + " | Fecha: " + dia.toString() + "-" + mes.toString() + "-" + ano.toString()
            if(fecha == fechaHoy) {
                MainActivity.listaRutinasATrabajar.clear()
                MainActivity.listaRutinasATrabajar.add(cadena)
            }
            MainActivity.listaRutinasATrabajarAux.add(cadena)

            //añadir la rutina a la lista de eventos
            MainActivity.listaEventos1.add(fecha)

            Toast.makeText(this, "Se añadio correctamente la rutina", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@SeleccionadorRutina, SeguimientoActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Seleccionar rutina"
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
            R.id.nav_solicitudes-> callSolicitudesActivity()

        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }
    private fun callSolicitudesActivity() {
        val intent = Intent(this, SolicitudesActivity::class.java)
        startActivity(intent)    }
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
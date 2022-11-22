package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity.Companion.useremail
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.example.wildtracker.ui.MainActivity.Companion.InsigniasSwitch
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    var listViewInsignias: ListView?= null

    private var grafica: GraphicalView? = null
    private var datosGrafica: XYSeries? = null
    private val informacionGrafica = XYMultipleSeriesDataset()
    private val renderGrafica = XYMultipleSeriesRenderer()
    private var seriesRendererGrafica: XYSeriesRenderer? = null


    companion object{
        var InsigniasSwitch = true
        val auth: String? = FirebaseAuth.getInstance().currentUser?.email
        var user =  auth

        //
        var listaRutinas1 = ArrayList<String>()
        var listaRutinas2 = ArrayList<String>()
        var listaRutinasVista1 = ArrayList<String>()
        var listaRutinasVista2 = ArrayList<String>()
        var listaEjercicios1 = ArrayList<String>()
        var listaEjercicios2 = ArrayList<String>()
        val listaRanking1 = ArrayList<String>()
        val listaRanking2 = ArrayList<String>()
        val listaRanking3 = ArrayList<String>()
        val listaRanking4 = ArrayList<String>()
        var listaRutinasATrabajarAux = ArrayList<String>()
        var listaMetasAux = ArrayList<String>()
        var validadorListas = true
        //
        var lunes: Double = 0.0; var martes: Double = 0.0; var miercoles: Double = 0.0; var jueves : Double = 0.0
        var viernes: Double = 0.0; var sabado: Double = 0.0; var domingo: Double = 0.0
        //

        var listaRutinas = ArrayList<String>()
        var listaRutinasVista = ArrayList<String>()
        var listaEjercicios = ArrayList<String>()
        var validadorAcomodo = true
        var listaRanking = ArrayList<String>()
        val listaSeguidores = ArrayList<String>()
        var listaRutinasATrabajar = ArrayList<String>()
        var listaMetas = ArrayList<String>()
    }

    private fun CargarListas(){
        if(validadorAcomodo){ //ayuda a organizar las listas de rutinas y los ejercicios
            listaRutinas = listaRutinas1
            listaRutinas.addAll(listaRutinas2)

            listaEjercicios = listaEjercicios1
            listaEjercicios.addAll(listaEjercicios2)

            listaRutinasVista = listaRutinasVista1
            listaRutinasVista.addAll(listaRutinasVista2)

            validadorAcomodo = false
        }
    }

    private fun tipoEjercicio(arreglo: Array<String?>): String{ //Funcion que encuentra si sobresale un tipo de ejercicio
        var tipo: String
        var piernas = 0; var abdomen = 0; var pecho = 0
        var espalda = 0; var brazos = 0; var hombros = 0
        var otro = 0

        for(i in 0 until arreglo.size) { //va a recorrer los ejercicios de la rutina
            for (j in listaEjercicios) { //para todos los ejercicios
                val id = j.split(" ").toTypedArray()[0] //toma el id
                if(arreglo[i] == id){ //si esta el ejercicio en la rutina

                    tipo = j.split(" | ").toTypedArray()[2] // toma el tipo

                    if(tipo == "Piernas") {piernas += 1} //y lo añade a los tipos de ejercicios
                    if(tipo == "Abdomen") {abdomen += 1}
                    if(tipo == "Pecho") {pecho += 1}
                    if(tipo == "Espalda") {espalda += 1}
                    if(tipo == "Brazos") {brazos += 1}
                    if(tipo == "Hombros") {hombros += 1}
                    if(tipo == "Otro") {otro += 1}
                }
            }
        }

        var aux = 0 //variable auxiliar para diferenciar tipos de ejercicios destacados

        if(piernas >= 3){ //necesita que el ejercicio sea minimo 3 veces trabajado
            aux = (piernas / 2)-1 //se obiene un dato
            if(abdomen <= aux && pecho <= aux && espalda <= aux && brazos <= aux && hombros <= aux){ //que se compara con los demas tipos de ejercicios para saber si el tipo piernas destaca
                return "piernas" //si es que destaca lo hara saber
            }
        }
        if(abdomen >= 3){ //lo mismo para los demas tipos de ejercicios
            aux = (abdomen / 2)-1
            if(piernas <= aux && pecho <= aux && espalda <= aux && brazos <= aux && hombros <= aux){
                return "abdomen"
            }
        }
        if(pecho >= 3){
            aux = (pecho / 2)-1
            if(abdomen <= aux && piernas <= aux && espalda <= aux && brazos <= aux && hombros <= aux){
                return "pecho"
            }
        }
        if(espalda >= 3){
            aux = (espalda / 2)-1
            if(abdomen <= aux && pecho <= aux && piernas <= aux && brazos <= aux && hombros <= aux){
                return "espalda"
            }
        }
        if(brazos >= 3){
            aux = (brazos / 2)-1
            if(abdomen <= aux && pecho <= aux && espalda <= aux && piernas <= aux && hombros <= aux){
                return "brazos"
            }
        }
        if(hombros >= 3){
            aux = (hombros / 2)-1
            if(abdomen <= aux && pecho <= aux && espalda <= aux && brazos <= aux && piernas <= aux){
                return "hombros"
            }
        }
        return ""
    }

private fun myPreferences() {
    val prefs = PreferenceManager.getDefaultSharedPreferences(this)
    val switch = prefs.getBoolean("switch_preference_insignias", true)
    if(switch){
        InsigniasSwitch = switch
    }
    else{
        InsigniasSwitch = false
    }
}

    private fun insigniasRutinas() {
        var listaRutinasInsignias = listOf<insignias>()

        for (j in listaRutinas) { //para todas las rutinas
            val nombre = j.split(" | ").toTypedArray()[1] //toma el nombre
            val nivelAux = j.split("Nivel:").toTypedArray()[1] //toma el nombre
            val nivel = (nivelAux.split(" ").toTypedArray()[1]).toInt()

            val ejercicios = j.split(" | ").toTypedArray()[3] //toma los ejercicios

            val arreglo: Array<String?>
            arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios
            val tipoSobresaliente = tipoEjercicio(arreglo) //funcion para ver si sobresale un tipo de ejercicio

            var rutina: insignias
            if(InsigniasSwitch){
            if(nivel == 100){
                rutina = insignias(nombre, nivel, R.drawable.insignia11)
            }else{
                if(nivel > 90){
                    rutina = insignias(nombre, nivel, R.drawable.insignia10)
                }else{
                    if(nivel > 80){
                        rutina = insignias(nombre, nivel, R.drawable.insignia9)
                    }else{
                        if(nivel > 70){
                            rutina = insignias(nombre, nivel, R.drawable.insignia8)
                        }else{
                            if(nivel > 60){
                                rutina = insignias(nombre, nivel, R.drawable.insignia7)
                            }else{
                                if(nivel > 50){
                                    rutina = insignias(nombre, nivel, R.drawable.insignia6)
                                }else{
                                    if(nivel > 40){
                                        rutina = insignias(nombre, nivel, R.drawable.insignia5)
                                    }else{
                                        if(nivel > 30){
                                            rutina = insignias(nombre, nivel, R.drawable.insignia4)
                                        }else{
                                            if(nivel > 20){
                                                rutina = insignias(nombre, nivel, R.drawable.insignia3)
                                            }else{
                                                if(nivel > 10){
                                                    rutina = insignias(nombre, nivel, R.drawable.insignia2)
                                                }else{
                                                    rutina = insignias(nombre, nivel, R.drawable.insignia1)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

                if (nivel >= 1){
                    val random = Random().nextInt(2) //numero random para no siempre mostrar las insignias de tipo (50%) //esto se tiene que eliminar despues
                    if(random == 0){
                        if(tipoSobresaliente == "piernas"){rutina = insignias(nombre, nivel, R.drawable.insigniapiernas)} //en caso de ser de un tipo lo va a poner
                        if(tipoSobresaliente == "abdomen"){rutina = insignias(nombre, nivel, R.drawable.insigniaabdomen)}
                        if(tipoSobresaliente == "pecho"){rutina = insignias(nombre, nivel, R.drawable.insigniapecho)}
                        if(tipoSobresaliente == "espalda"){rutina = insignias(nombre, nivel, R.drawable.insigniaespalda)}
                        if(tipoSobresaliente == "brazos"){rutina = insignias(nombre, nivel, R.drawable.insigniabrazos)}
                        if(tipoSobresaliente == "hombros"){rutina = insignias(nombre, nivel, R.drawable.insigniahombros)}
                    }
                }

                val listaRutinasAux3 = listOf(rutina)
                listaRutinasInsignias += listaRutinasAux3
            }
            else if(!InsigniasSwitch){
                rutina = insignias(nombre, nivel, R.drawable.excersice_icon)

                val listaRutinasAux3 = listOf(rutina)
                listaRutinasInsignias += listaRutinasAux3
            }
        }

        val adapter = insigniaAdapter(this, listaRutinasInsignias)
        listViewInsignias!!.adapter = adapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initNavigationView()
        myPreferences()
        listViewInsignias = findViewById(R.id.listViewInsignias)

        iniciarGrafica()
        CargarListas()

        insigniasRutinas()

    }

    private fun initToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
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
            R.id.nav_metas -> callMetasActivity()
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

}

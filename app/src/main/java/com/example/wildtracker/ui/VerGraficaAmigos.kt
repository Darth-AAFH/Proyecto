package com.example.wildtracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.*
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia1
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia2
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia3
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia4
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia5
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia6
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewDia7
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo1
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo2
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo3
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo4
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo5
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo6
import kotlinx.android.synthetic.main.activity_ver_grafica_amigos.textViewTiempo7
import org.achartengine.ChartFactory
import org.achartengine.GraphicalView
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.renderer.XYSeriesRenderer
import java.util.*
import kotlin.collections.ArrayList

class VerGraficaAmigos : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var grafica: GraphicalView? = null
    private var datosGrafica: XYSeries? = null
    private val informacionGrafica = XYMultipleSeriesDataset()
    private val renderGrafica = XYMultipleSeriesRenderer()
    private var seriesRendererGrafica: XYSeriesRenderer? = null

    var dia1: Double = 0.0; var dia2: Double = 0.0; var dia3: Double = 0.0; var dia4: Double = 0.0
    var dia5: Double = 0.0; var dia6: Double = 0.0; var dia7: Double = 0.0

    var nombre = ""

    companion object{
        var listaRutinasAmigo = ArrayList<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_grafica_amigos)
        initNavigationView()

        val b = intent.extras //b toma el id de la rutina a trabajar
        if (b != null) {
            nombre = b.getString("Nombre").toString()
            dia1 = b.getDouble("Dia1")
            dia2 = b.getDouble("Dia2")
            dia3 = b.getDouble("Dia3")
            dia4 = b.getDouble("Dia4")
            dia5 = b.getDouble("Dia5")
            dia6 = b.getDouble("Dia6")
            dia7 = b.getDouble("Dia7")
        }

        iniciarGrafica()
        acomodarTextoGrafica()
        cargarRutinas()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Estadisticas de $nombre"
        setSupportActionBar(toolbar)

        com.example.wildtracker.ui.drawer = findViewById(R.id.drawerlayout)!!
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.bar_title,
            R.string.navigation_drawer_close
        )
        com.example.wildtracker.ui.drawer.addDrawerListener(toggle)
        toggle.syncState()


    }

    fun acomodarTextoGrafica(){
        if(MainActivity.diaSemanaHoy == 7){
            textViewDia1!!.text = "L"; textViewDia2!!.text = "M"; textViewDia3!!.text = "M"; textViewDia4!!.text =
                "J"
            textViewDia5!!.text = "V"; textViewDia6!!.text = "S"; textViewDia7!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 6){
            textViewDia2!!.text = "L"; textViewDia3!!.text = "M"; textViewDia4!!.text = "M"; textViewDia5!!.text =
                "J"
            textViewDia6!!.text = "V"; textViewDia7!!.text = "S"; textViewDia1!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 5){
            textViewDia3!!.text = "L"; textViewDia4!!.text = "M"; textViewDia5!!.text = "M"; textViewDia6!!.text =
                "J"
            textViewDia7!!.text = "V"; textViewDia1!!.text = "S"; textViewDia2!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 4){
            textViewDia4!!.text = "L"; textViewDia5!!.text = "M"; textViewDia6!!.text = "M"; textViewDia7!!.text =
                "J"
            textViewDia1!!.text = "V"; textViewDia2!!.text = "S"; textViewDia3!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 3){
            textViewDia5!!.text = "L"; textViewDia6!!.text = "M"; textViewDia7!!.text = "M"; textViewDia1!!.text =
                "J"
            textViewDia2!!.text = "V"; textViewDia3!!.text = "S"; textViewDia4!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 2){
            textViewDia6!!.text = "L"; textViewDia7!!.text = "M"; textViewDia1!!.text = "M"; textViewDia2!!.text =
                "J"
            textViewDia3!!.text = "V"; textViewDia4!!.text = "S"; textViewDia5!!.text = "D"
        }
        if(MainActivity.diaSemanaHoy == 1){
            textViewDia7!!.text = "L"; textViewDia1!!.text = "M"; textViewDia2!!.text = "M"; textViewDia3!!.text =
                "J"
            textViewDia4!!.text = "V"; textViewDia5!!.text = "S"; textViewDia6!!.text = "D"
        }


        textViewTiempo1!!.text = (dia1.toInt()).toString()
        textViewTiempo2!!.text = (dia2.toInt()).toString()
        textViewTiempo3!!.text = (dia3.toInt()).toString()
        textViewTiempo4!!.text = (dia4.toInt()).toString()
        textViewTiempo5!!.text = (dia5.toInt()).toString()
        textViewTiempo6!!.text = (dia6.toInt()).toString()
        textViewTiempo7!!.text = (dia7.toInt()).toString()
        /*
        var aux: Int
        var horas = 0

        aux = dia1.toInt()
        if(dia1 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            this.textViewTiempo1!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo1!!.text = (dia1.toInt()).toString()
        }

        aux = dia2.toInt()
        if(dia2 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo2!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo2!!.text = (dia2.toInt()).toString()
        }

        aux = dia3.toInt()
        if(dia3 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo3!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo3!!.text = (dia3.toInt()).toString()
        }

        aux = dia4.toInt()
        if(dia4 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo4!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo4!!.text = (dia4.toInt()).toString()
        }

        aux = dia5.toInt()
        if(dia5 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo5!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo5!!.text = (dia5.toInt()).toString()
        }

        aux = dia6.toInt()
        if(dia6 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo6!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo6!!.text = (dia6.toInt()).toString()
        }

        aux = dia7.toInt()
        if(dia7 >= 60){
            while(aux >= 60){ //se obtienen las horas
                aux -= 60
                horas += 1
            }
            textViewTiempo7!!.text = horas.toString() + "hr" + aux.toString()
            horas = 0
        }else{
            textViewTiempo7!!.text = (dia7.toInt()).toString()
        }
        */
    }

    private fun iniciarGrafica() {
        val layout = findViewById<LinearLayout>(R.id.chart2)
        if (grafica == null) {
            cargarGrafica()
            grafica = ChartFactory.getCubeLineChartView(this, informacionGrafica, renderGrafica, 0f)
            layout.addView(grafica)
        } else {
            grafica!!.repaint()
        }
    }

    private fun cargarGrafica() {
        datosGrafica = XYSeries("Simple Data")
        informacionGrafica.addSeries(datosGrafica)

        datosGrafica!!.add(0.0, dia1)
        datosGrafica!!.add(1.0, dia2)
        datosGrafica!!.add(2.0, dia3)
        datosGrafica!!.add(3.0, dia4)
        datosGrafica!!.add(4.0, dia5)
        datosGrafica!!.add(5.0, dia6)
        datosGrafica!!.add(6.0, dia7)

        seriesRendererGrafica = XYSeriesRenderer()
        renderGrafica.addSeriesRenderer(seriesRendererGrafica)
    }

    private fun cargarRutinas(){
        if(listaRutinasAmigo.isEmpty()){
            textViewAyudaVerGrafica.visibility = View.VISIBLE
        }else{
            for(i in listaRutinasAmigo){
                val nombre = i.split(" | ").toTypedArray()[0] //toma el nombre
                val ejercicios = i.split(" | ").toTypedArray()[2] //toma los ejercicios
                val nivel = i.split(" | ").toTypedArray()[1].toInt() //toma el nivel

                ponerInsignia(nombre, nivel, ejercicios)
            }

            val adapter = amigosAdapter(this, listaRutinasVista)
            listViewRutinasAmigo!!.adapter = adapter
        }
    }

    var listaRutinasVista = listOf<amigos>()
    private fun ponerInsignia(nombre: String, nivel: Int, ejercicios: String){
        var vista: amigos

        if(MainActivity.InsigniasSwitch){
            if(nivel == 100){
                vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia11)
            }else{
                if(nivel > 90){
                    vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia10)
                }else{
                    if(nivel > 80){
                        vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia9)
                    }else{
                        if(nivel > 70){
                            vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia8)
                        }else{
                            if(nivel > 60){
                                vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia7)
                            }else{
                                if(nivel > 50){
                                    vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia6)
                                }else{
                                    if(nivel > 40){
                                        vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia5)
                                    }else{
                                        if(nivel > 30){
                                            vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia4)
                                        }else{
                                            if(nivel > 20){
                                                vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia3)
                                            }else{
                                                if(nivel > 10){
                                                    vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia2)
                                                }else{
                                                    vista = amigos(nombre, ejercicios, nivel, R.drawable.insignia1)
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

            val listaRutinasAmigoAux = listOf(vista)
            listaRutinasVista += listaRutinasAmigoAux
        }
        else if(!MainActivity.InsigniasSwitch){
            vista = amigos(nombre, ejercicios, nivel, R.drawable.excersice_icon)

            val listaRutinasAmigoAux2 = listOf(vista)
            listaRutinasVista += listaRutinasAmigoAux2
        }
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

        com.example.wildtracker.ui.drawer.closeDrawer(GravityCompat.START) // cerrar menu

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
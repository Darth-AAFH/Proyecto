package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
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
import kotlinx.android.synthetic.main.activity_metas.*
import java.text.SimpleDateFormat
import java.util.*

class MetasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout

    var editTextNombreMeta: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso: Switch?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchRepeticion: Switch?= null
    var editTextInicio: EditText ?= null; var editTextFinal: EditText ?= null
    var d1: CheckBox ?= null; var d2: CheckBox ?= null; var d3: CheckBox ?= null; var d4: CheckBox ?= null
    var d5: CheckBox ?= null; var d6: CheckBox ?= null; var d7: CheckBox ?= null
    private var buttonGuardar: Button?= null
    private var buttonTest: Button?= null///////////////////////////////////////////////////////////////

    private val db = FirebaseFirestore.getInstance()

    var sdf = SimpleDateFormat("dd/MM/yyy")
    var fechaHoy = sdf.format(Date())
    var dia = 0; var mes = 0; var ano = 0
    var D1 = false; var D2 = false; var D3 = false; var D4 = false; var D5 = false; var D6 = false; var D7 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)
        //initToolbar()
        //initNavigationView()

        editTextNombreMeta = findViewById<View>(R.id.editTextNombreMeta) as EditText
        switchPeso = findViewById<View>(R.id.idSwitch1) as Switch
        switchRepeticion = findViewById<View>(R.id.idSwitch2) as Switch
        editTextInicio = findViewById<View>(R.id.editTextInicio) as EditText
        editTextFinal = findViewById<View>(R.id.editTextFinal) as EditText
        d1 = findViewById<CheckBox>(R.id.check1); d2 = findViewById(R.id.check2) as CheckBox
        d3 = findViewById(R.id.check3) as CheckBox; d4 = findViewById(R.id.check4) as CheckBox
        d5 = findViewById(R.id.check5) as CheckBox; d6 = findViewById(R.id.check6) as CheckBox
        d7 = findViewById(R.id.check7) as CheckBox
        buttonGuardar = findViewById(R.id.buttonGuardarMeta)
        buttonTest = findViewById(R.id.buttonTest)////////////////////////////////////////////////////

        editTextDate.setOnClickListener{ tomarFecha() }

        buttonGuardar!!.setOnClickListener{
            val nombre = editTextNombreMeta!!.text.toString(); val peso = switchPeso!!.isChecked(); val repeticion = switchRepeticion!!.isChecked()
            val datoInicial = (editTextInicio!!.text.toString()).toInt(); val datoFinal = (editTextFinal!!.text.toString()).toInt()
            D1 = d1!!.isChecked(); D2 = d2!!.isChecked(); D3 = d3!!.isChecked(); D4 = d4!!.isChecked(); D5 = d5!!.isChecked(); D6 = d6!!.isChecked(); D7 = d7!!.isChecked()

            if(D1 == false && D2 == false && D3 == false && D4 == false && D5 == false && D6 == false && D7 == false){
                Toast.makeText(this, "Debe de seleccionar por lo menos un día", Toast.LENGTH_LONG).show()
            }else{
                var sdf = SimpleDateFormat("dd")
                var diaHoy = sdf.format(Date()) //se obtiene el dia actual
                sdf = SimpleDateFormat("MM")
                var mesHoy = sdf.format(Date()) //se obtiene el mes actual
                sdf = SimpleDateFormat("yyyy")
                val anoHoy = sdf.format(Date()) //se obiene el año actual

                if(anoHoy.toInt() > ano){
                    Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                }else{
                    if(anoHoy.toInt() == ano){
                        if(mesHoy.toInt() > mes){
                            Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                        }else{
                            if(mesHoy.toInt() == mes){
                                if(diaHoy.toInt() > dia){
                                    Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                                }else{
                                    guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal)
                                }
                            }else{
                                guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal)
                            }
                        }
                    }else{
                        guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal)
                    }
                }
            }
        }

        buttonTest!!.setOnClickListener{
            val intent = Intent(this@MetasActivity, SeguimientoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun tomarFecha() {
        val fechaFinal = SeleccionadorFecha{ day, month, year -> acomodarFecha(day, month, year) }
        fechaFinal.show(supportFragmentManager, "datePicker")
    }

    @SuppressLint("SetTextI18n")
    fun acomodarFecha(day: Int, month: Int, year: Int) {
        var month2 = month + 1
        editTextDate.setText("Fecha de finalización seleccionada: $day del mes $month2 de $year")
        dia = day; mes = month2; ano = year
    }

    /*
    public void onclick(View view) {
        if(view.getId() ==R.id.idSwitch){
            if(switchPeso.isChecked){
                switchRepeticion = false

                editTextInicio.setTect("Introduzca peso inicial")
                editTextFinal.setText("Introduzca peso final")
            }
        }

        if(view.getId() ==R.id.idSwitch2){
            if(switchRepeticion.isChecked){
                switchPeso = false

                editTextInicio.setTect("Introduzca cantidad de repetinciones inicial")
                editTextFinal.setText("Introduzca cantidad de repetinciones final")
            }
        }
    }
     */


    private fun guardarMeta(Nombre: String, Peso: Boolean, Repeticion: Boolean, DatoInicial: Int, DatoFinal: Int) {

        /*
        Toast.makeText(this, "nombre: "+Nombre, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "peso: "+Peso, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "repeticion: "+Repeticion, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "lunes: "+D1, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "martes: "+D2, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "miercoles: "+D3, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "jueves: "+D4, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "viernes: "+D5, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "sabado: "+D6, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "domingo: "+D7, Toast.LENGTH_SHORT).show()
         */


        var diaF = ""; var mesF = ""; var fechaFin = ""
        if(dia < 10)
            diaF = "0"
        diaF += dia.toString()
        if(mes < 10)
            mesF = "0"
        mesF += mes.toString()
        fechaFin = diaF + "/" + mesF + "/" + ano.toString()

        //Toast.makeText(this, "fecha inicio: "+fechaHoy, Toast.LENGTH_SHORT).show()
        //Toast.makeText(this, "fecha final: "+fechaFin, Toast.LENGTH_SHORT).show()

        //poner una funcion similar a la de crear de Creador Ejercicios (para )

        //falta peso o repeticion inicial y final

        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("metas")
                .document(Nombre).set(
                    hashMapOf(
                        "nombre" to Nombre,
                        "peso" to Peso,
                        "repeticion" to Repeticion,
                        "lunes" to D1,
                        "martes" to D2,
                        "miercoles" to D3,
                        "jueves" to D4,
                        "viernes" to D5,
                        "sabado" to D6,
                        "domingo" to D7,
                        "fechaFinal" to fechaFin,
                        "datoInicial" to DatoInicial,
                        "datoFinal" to DatoFinal
                    )
                )
        }

        Toast.makeText(this, "Se ha guardado la meta", Toast.LENGTH_SHORT).show()
    }

    /////////////////////////////////////////////////////

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Metas"
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

    private fun callMusica() {
        val intent = Intent(this, mPlayerActivity::class.java)
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
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

    var dia = 0; var mes = 0; var ano = 0
    var D1 = false; var D2 = false; var D3 = false; var D4 = false; var D5 = false; var D6 = false; var D7 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metas)
        initToolbar()
        initNavigationView()

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

        switchPeso!!.setChecked(true)

        buttonGuardar!!.setOnClickListener{
            val nombre = editTextNombreMeta!!.text.toString(); val peso = switchPeso!!.isChecked(); val repeticion = switchRepeticion!!.isChecked()

            var datoInicial = 0; var datoFinal = 0

            if(editTextInicio!!.text.toString() != ""){
                datoInicial = (editTextInicio!!.text.toString()).toInt()
            }
            if(editTextFinal!!.text.toString() != ""){
                datoFinal = (editTextFinal!!.text.toString()).toInt()
            }

            D1 = d1!!.isChecked(); D2 = d2!!.isChecked(); D3 = d3!!.isChecked(); D4 = d4!!.isChecked(); D5 = d5!!.isChecked(); D6 = d6!!.isChecked(); D7 = d7!!.isChecked()

            if(nombre == ""){
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }else{
                if(datoFinal == 0){
                    Toast.makeText(this, "El dato final no puede ser igual a 0", Toast.LENGTH_SHORT).show()
                }else{
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
                            Toast.makeText(this, "La fecha de finalizacion seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                        }else{
                            if(anoHoy.toInt() == ano){
                                if(mesHoy.toInt() > mes){
                                    Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                                }else{
                                    if(mesHoy.toInt() == mes){
                                        if(diaHoy.toInt() > dia){
                                            Toast.makeText(this, "La fecha seleccionada es una fecha pasada", Toast.LENGTH_LONG).show()
                                        }else{
                                            guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                                        }
                                    }else{
                                        guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                                    }
                                }
                            }else{
                                guardarMeta(nombre, peso, repeticion, datoInicial, datoFinal, diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt())
                            }
                        }
                    }
                }
            }
        }

        buttonTest!!.setOnClickListener{
            val intent = Intent(this@MetasActivity, SeguimientoActivity::class.java)
            startActivity(intent)
        }

        switchPeso!!.setOnClickListener {
            switchRepeticion!!.setChecked(true)
            editTextInicio!!.setHint("Cantidad de repeticiones iniciales")
            editTextFinal!!.setHint("Cantidad de repeticiones finales")
            if(switchPeso!!.isChecked()){
                switchRepeticion!!.setChecked(false)
                editTextInicio!!.setHint("Peso inicial (kg)")
                editTextFinal!!.setHint("Peso inicial (kg)")
            }
        }

        switchRepeticion!!.setOnClickListener {
            switchPeso!!.setChecked(true)
            editTextInicio!!.setHint("Peso inicial (kg)")
            editTextFinal!!.setHint("Peso inicial (kg)")
            if(switchRepeticion!!.isChecked()){
                switchPeso!!.setChecked(false)
                editTextInicio!!.setHint("Cantidad de repeticiones iniciales")
                editTextFinal!!.setHint("Cantidad de repeticiones finales")
            }
        }
    }

    private fun tomarFecha() {
        val fechaFinal = SeleccionadorFecha{ day, month, year -> acomodarFecha(day, month, year) }
        fechaFinal.show(supportFragmentManager, "datePicker")
    }

    @SuppressLint("SetTextI18n")
    fun acomodarFecha(day: Int, month: Int, year: Int) {
        var month2 = month + 1
        editTextDate.setText("Fecha de finalización: $day del mes $month2 de $year")
        dia = day; mes = month2; ano = year
    }

    private fun guardarMeta(Nombre: String, Peso: Boolean, Repeticion: Boolean, DatoInicial: Int, DatoFinal: Int, diaHoy: Int, mesHoy: Int, anoHoy: Int) {
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
                        "diaFinal" to dia,
                        "mesFinal" to mes,
                        "anoFinal" to ano,
                        "datoInicial" to DatoInicial,
                        "datoFinal" to DatoFinal,
                        "diaSeg" to diaHoy, //dia de seguimiento (para llevar un orden a los datos que se le suman)
                        "mesSeg" to mesHoy,
                        "anoSeg" to anoHoy,
                        "ultDia" to diaHoy - 1, //ultima fecha en que se trabajo la meta (esto para que no la repita dos veces en un día)
                        "ultMes" to mesHoy,
                        "ultAno" to anoHoy
                    )
                )
        }

        Toast.makeText(this, "Se ha guardado la meta", Toast.LENGTH_LONG).show()

        var sdf = SimpleDateFormat("dd")
        val diaHoy2 = sdf.format(Date()) //se obtiene el dia actual
        sdf = SimpleDateFormat("MM")
        val mesHoy2 = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy2 = sdf.format(Date()) //se obiene el año actual

        val diaHoy = diaHoy2.toInt()
        val mesHoy = mesHoy2.toInt()
        val anoHoy = anoHoy2.toInt()

        val diaSemHoy = diaSemana(diaHoy, mesHoy, anoHoy) //se obtiene el numero de dia de la semana (lunes = 1, martes = 2, miercoles = 3, etc)

        var lun = 0; var mar = 0; var mier = 0; var juev = 0
        var vier = 0; var sab = 0; var dom = 0

        if(D1) lun = 1; if(D2) mar = 2; if(D3) mier = 3; if(D4) juev = 4
        if(D5) vier = 5; if(D6) sab = 6; if(D7) dom = 7

        if(diaSemHoy == lun || diaSemHoy == mar || diaSemHoy == mier || diaSemHoy == juev || diaSemHoy == vier || diaSemHoy == sab || diaSemHoy == dom){
            var cadena = Nombre //toma el nombre de la meta
            cadena += " | " //se le agraga texto de formato
            if(D1){cadena += "lun "} //se le agregan los dias a trabajar
            if(D2){cadena += "mar "}
            if(D3){cadena += "mier "}
            if(D4){cadena += "juev "}
            if(D5){cadena += "vier "}
            if(D6){cadena += "sab "}
            if(D7){cadena += "dom "}
            cadena += "| " //se le agraga texto de formato
            //se le agrega las repeticiones o peso a levantar
            if(Peso){ //con un texto que diferencie peso o repeticiones
                cadena += "Levantar: "
                cadena += DatoInicial //se le agrega las repeticiones o peso a levantar
                cadena += "kg"
            }
            if(Repeticion){ //con un texto que diferencie peso o repeticiones
                cadena += "Repeticiones: "
                cadena += DatoInicial //se le agrega las repeticiones o peso a levantar
            }
            //se le agrega la fecha de finalizacion
            cadena += " | Fecha de finalización: "
            cadena += dia; cadena += "-"; cadena += mes; cadena += "-"; cadena += ano

            MainActivity.listaMetas.add(cadena)
        }

        editTextNombreMeta!!.setText("")
        editTextDate!!.setText("")
        switchPeso!!.setChecked(true)
        switchRepeticion!!.setChecked(false)
        editTextInicio!!.setText("")
        editTextFinal!!.setText("")
        d1!!.setChecked(false); d2!!.setChecked(false); d3!!.setChecked(false)
        d4!!.setChecked(false); d5!!.setChecked(false); d6!!.setChecked(false)
        d7!!.setChecked(false)
    }

    private fun diaSemana(dia: Int, mes: Int, ano: Int): Int {  ///// revisar esto
        val c = Calendar.getInstance()
        c.set(ano, mes, dia)
        val diaSem =  c.get(Calendar.DAY_OF_WEEK)

        if(mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12){
            if(diaSem == 5) return 1; if(diaSem == 6) return 2; if(diaSem == 7) return 3
            if(diaSem == 1) return 4; if(diaSem == 2) return 5; if(diaSem == 3) return 6
            if(diaSem == 4) return 7
        }else{
            if(mes == 2){
                if(diaSem == 2) return 1; if(diaSem == 3) return 2; if(diaSem == 4) return 3
                if(diaSem == 5) return 4; if(diaSem == 6) return 5; if(diaSem == 7) return 6
                if(diaSem == 1) return 7
            }else{
                if(diaSem == 4) return 1; if(diaSem == 5) return 2; if(diaSem == 6) return 3
                if(diaSem == 7) return 4; if(diaSem == 1) return 5; if(diaSem == 2) return 6
                if(diaSem == 3) return 7
            }
        }
        return 0
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
            R.id.Settings->callAjustesActivity()


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
package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.example.wildtracker.musica.mPlayerActivity
import com.google.firebase.firestore.FirebaseFirestore

class PlantillasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var buttonAdd: Button ?= null; var buttonRutina: Button ?= null; var buttonEjercicio: Button ?= null
    var listViewRutinas: ListView?= null

    var validadorMostar = 0

    private val db = FirebaseFirestore.getInstance()
    var ejerciciosPredeterminados = false

    private fun CargarListas(){ //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)

            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)

            MainActivity.validadorAcomodo = false
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinas)
        listViewRutinas!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private lateinit var drawer: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plantillas)
        initToolbar()
        initNavigationView()

        buttonAdd = findViewById(R.id.buttonAdd)
        buttonRutina = findViewById(R.id.buttonRutina); buttonRutina!!.setVisibility(View.INVISIBLE); buttonRutina!!.setEnabled(false)
        buttonEjercicio = findViewById(R.id.buttonEjercicio); buttonEjercicio!!.setVisibility(View.INVISIBLE); buttonEjercicio!!.setEnabled(false)
        listViewRutinas = findViewById(R.id.listViewRutinas)

        MainActivity.user?.let { usuario -> //para cargar los ejercicios por defecto
            db.collection("users").document(usuario).collection("ejercicios")
                .get().addOnSuccessListener {
                if(it.isEmpty){
                    ejerciciosPredeterminados = true
                }
            }
        }
        CargarListas()

        buttonAdd!!.setOnClickListener{
            if(validadorMostar == 0) {
                buttonRutina!!.setVisibility(View.VISIBLE); buttonRutina!!.setEnabled(true)
                buttonEjercicio!!.setVisibility(View.VISIBLE); buttonEjercicio!!.setEnabled(true)
                validadorMostar = 1
            }else{
                buttonRutina!!.setVisibility(View.INVISIBLE); buttonRutina!!.setEnabled(false)
                buttonEjercicio!!.setVisibility(View.INVISIBLE); buttonEjercicio!!.setEnabled(false)
                validadorMostar = 0
            }
            validadorPredeterDB()
        }

        buttonRutina!!.setOnClickListener{
            val intent = Intent(this@PlantillasActivity, CreadorRutinas::class.java)
            startActivity(intent)
        }

        buttonEjercicio!!.setOnClickListener{
            val intent = Intent(this@PlantillasActivity, CreadorEjercicios::class.java)
            startActivity(intent)
        }

        listViewRutinas!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            val nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]
            val ejercicios = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[3]
            val nivelAux = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[2]

            val arreglo: Array<String?>
            arreglo = nivelAux.split(" ").toTypedArray()
            val nivel = arreglo[1]!!.toInt()

            val intent = Intent(this@PlantillasActivity, EditorRutinas::class.java)
            intent.putExtra("Num", num)
            intent.putExtra("Nombre", nombre)
            intent.putExtra("Ejercicios", ejercicios)
            intent.putExtra("Nivel", nivel)
            startActivity(intent)
        }
    }

    private fun validadorPredeterDB(){
        if(ejerciciosPredeterminados) {
            var id = 1; var nombre = "Sentadillas"; var tipo = "Piernas"; var peso = true; var url =".youtube.com/embed/VRKdOsad3HQ"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 2; nombre = "Saltos de tijera"; tipo = "Piernas"; peso = false; url=".youtube.com/embed/95j1mH27eXc"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 3; nombre = "Elevación de talones"; tipo = "Piernas"; peso = false; url=".youtube.com/embed/igRyr2jWRTs"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 4; nombre = "Abdominales"; tipo = "Abdomen"; peso = false; url=".youtube.com/embed/OsUz898onTE"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 5; nombre = "Plancha"; tipo = "Abdomen"; peso = false; url=".youtube.com/embed/OuFDY0fwlvk"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 6; nombre = "Escaladores"; tipo = "Abdomen"; peso = false; url=".youtube.com/embed/lD_gfTofg4A"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 7; nombre = "Dominadas"; tipo = "Pecho"; peso = false; url=".youtube.com/embed/A2thchjoWkI"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 8; nombre = "Press de pecho"; tipo = "Pecho"; peso = true; url=".youtube.com/embed/NfJqRwAlZY8"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 9; nombre = "Peso muerto"; tipo = "Espalda"; peso = true; url=".youtube.com/embed/gBY5Se4apXc"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 10; nombre = "Punches"; tipo = "Brazos"; peso = false; url=".youtube.com/embed/b0ZeY-j5T1w"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 11; nombre = "Dips de tríceps"; tipo = "Brazos"; peso = false; url="youtube.com/embed/EtPHEAOIxUU"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 12; nombre = "Press de hombros"; tipo = "Hombros"; peso = true; url=".youtube.com/embed/UFKqIoAbUBg?start=9"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 13; nombre = "Elevaciones laterales"; tipo = "Hombros"; peso = true; url=".youtube.com/embed/dT6Q3NHtSjw"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)

            id = 14; nombre = "Flexiones"; tipo = "Otro"; peso = false; url=".youtube.com/embed/nOFk-PYAvwI"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
            id = 15; nombre = "Burpees"; tipo = "Otro"; peso = false; url=".youtube.com/embed/auBLPXO8Fww"
            ejerciciosPredeterDB(id, nombre, tipo, peso,url)
        }
    }

    private fun ejerciciosPredeterDB(
        Id: Int,
        Nombre: String,
        Tipo: String,
        Peso: Boolean,
        url: String
    ){
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios").document(Id.toString()).set(
                hashMapOf(
                    "id" to Id,
                    "nombre" to Nombre,
                    "tipo" to Tipo,
                    "peso" to Peso,
                "url" to url
                )
            )
        }

        var cadena = Id.toString() + " | " + Nombre + " | " + Tipo
        if(Peso){
            cadena += " | Con peso"
        }else{
            cadena += " | Sin peso"
        }
        MainActivity.listaEjercicios.add(cadena)
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Plantillas"
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
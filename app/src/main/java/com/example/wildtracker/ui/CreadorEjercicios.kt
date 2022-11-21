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
import androidx.core.text.isDigitsOnly
import androidx.core.view.GravityCompat
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreadorEjercicios : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var editTextNombre: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso: Switch ?= null
    private var buttonCrear: Button?= null; private var buttonEditar: Button ?= null

    var arregloEjercicios = Array<ejercicio?>(66){null}
    var validadorNombre = true

    private val db = FirebaseFirestore.getInstance()
    var contadorMaxEjer = 0; var idFinalEjer = 0; var idAux = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_ejercicios)
        initToolbar()
        initNavigationView()
        editTextNombre = findViewById<View>(R.id.editTextNombre) as EditText
        switchPeso = findViewById<View>(R.id.switchPeso) as Switch
        buttonCrear = findViewById(R.id.buttonCrear)
        buttonEditar = findViewById(R.id.buttonEditarEjercicio)
        val spinnerTipos: Spinner = findViewById(R.id.spinnerTipos)

        val listaSpinner = listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSpinner)
        spinnerTipos.adapter = adaptadorSpinner

        MainActivity.user?.let { usuario -> //para encontrar el id final de los ejercicios
            db.collection("users").document(usuario).collection("ejercicios") //abre la base de datos
                .get().addOnSuccessListener {
                    for(ejercicio in it){ //para cada ejercicio
                        contadorMaxEjer += 1 //cuenta cuantos ejercicios hay
                        idAux = (ejercicio.get("id") as Long).toInt() //toma el id
                        if(idFinalEjer < idAux){ //si es un id mayor
                            idFinalEjer = (ejercicio.get("id") as Long).toInt() //lo va a guardar como el id final
                        }

                    }
                }
        }

        buttonCrear!!.setOnClickListener{
            val nombre = editTextNombre!!.text.toString(); val tipo = spinnerTipos.selectedItem.toString(); val peso = switchPeso!!.isChecked()
            if(crear(nombre, tipo, peso)){
                if(validadorNombre) {
                    finish()
                }
            }else {
                Toast.makeText(this, "Se ha alcanzado el numero maximo de ejercicios", Toast.LENGTH_SHORT).show()
            }
        }

        buttonEditar!!.setOnClickListener{
            val intent = Intent(this@CreadorEjercicios, VerEjercicios::class.java)
            startActivity(intent)
        }
    }

    private fun crear(Nombre: String, Tipo: String, validadorPeso: Boolean): Boolean{
        var confirmacion = false
        if(contadorMaxEjer <= 65){//////////////numero máx de ejercicios que el usuario puede crear (50)
            var nombre = Nombre

            if(nombre == ""){
                val idF = idFinalEjer
                nombre = "Ejercicio" + (idF - 14)
            }

            val arreglo: Array<String?>
            arreglo = nombre.split(" ").toTypedArray()

            validadorNombre = true
            for (i in 0 until arreglo.size) {//recorre todo el nombre
                if(arreglo[i]!!.isDigitsOnly()) { //si uno de los datos es numero
                    Toast.makeText(this, "El nombre de un ejercicio no puede contener numeros", Toast.LENGTH_SHORT).show()
                    validadorNombre = false
                }
            }

            if(validadorNombre == true){
                arregloEjercicios[contadorMaxEjer] = ejercicio(idFinalEjer+1, nombre, Tipo, validadorPeso)
                guardarBD(arregloEjercicios[contadorMaxEjer]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

    private fun guardarBD(Ejercicio: ejercicio) {
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios")
                .document(Ejercicio.id.toString()).set(
                hashMapOf(
                    "id" to Ejercicio.id,
                    "nombre" to Ejercicio.nombre,
                    "tipo" to Ejercicio.tipo,
                    "peso" to Ejercicio.peso
                )
            )
        }
        var cadena = Ejercicio.id.toString() + " | " + Ejercicio.nombre + " | " + Ejercicio.tipo
        if(Ejercicio.peso){
            cadena += " | Con peso"
        }else{
            cadena += " | Sin peso"
        }
        MainActivity.listaEjercicios.add(cadena)

        Toast.makeText(this, "Se ha guardado el ejercicio", Toast.LENGTH_SHORT).show()
    }
    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Editor Ejercicio"
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
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

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
package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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

@Suppress("NAME_SHADOWING")
class EditorEjercicios : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var editTextNombre2: EditText? = null
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private var switchPeso2: Switch? = null
    private var buttonGuardar: Button? = null;
    private var buttonBorrar: Button? = null

    var num = ""
    var nombre: String? = null;
    var tipo: String? = null;
    var peso: String? = null
    private lateinit var builder: AlertDialog.Builder //Dialogo de alerta para interactuar en el activity

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_ejercicios)

        val b = intent.extras //b toma los datos enviados del Listado
        if (b != null) { //Si existen datos
            num = b.getString("Num").toString() //Los guarda en estas variables
            nombre = b.getString("Nombre")
            tipo = b.getString("Tipo")
            peso = b.getString("Peso")
        }
        initToolbar()
        initNavigationView()

        editTextNombre2 = findViewById<View>(R.id.editTextNombre2) as EditText
        switchPeso2 = findViewById<View>(R.id.switchPeso2) as Switch
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonBorrar = findViewById(R.id.buttonBorrar)
        val spinnerTipos2: Spinner = findViewById(R.id.spinnerTipos2)

        val listaSpinner =
            listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptadorSpinner =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, listaSpinner)
        spinnerTipos2.adapter = adaptadorSpinner

        editTextNombre2!!.setText(nombre)
        if (tipo == "Piernas") spinnerTipos2.setSelection(0)
        if (tipo == "Abdomen") spinnerTipos2.setSelection(1)
        if (tipo == "Pecho") spinnerTipos2.setSelection(2)
        if (tipo == "Espalda") spinnerTipos2.setSelection(3)
        if (tipo == "Brazos") spinnerTipos2.setSelection(4)
        if (tipo == "Hombros") spinnerTipos2.setSelection(5)
        if (tipo == "Otro") spinnerTipos2.setSelection(6)
        if (peso == "Con peso") switchPeso2!!.isChecked = true
        if (peso == "Sin peso") switchPeso2!!.isChecked = false

        buttonGuardar!!.setOnClickListener {
            //Validacion para guardar el cambio en el ejercicio
            builder = AlertDialog.Builder(this)
            builder.setTitle("Modificar ejercicio")
                .setMessage("Deseas modificar este ejercicio?")
                .setCancelable(true)
                .setPositiveButton("Si") { dialogInterface, it ->
                    //Editar
                    val cambioNombre = editTextNombre2!!.text.toString();
                    val cambioTipo = spinnerTipos2.selectedItem.toString();
                    val cambioPeso = switchPeso2!!.isChecked
                    if (guardar(num, cambioNombre, cambioTipo, cambioPeso)) {
                        val intent = Intent(this@EditorEjercicios, PlantillasActivity::class.java)
                        startActivity(intent)
                    }
                }
                .setNegativeButton("Cancelar") { dialogInterface, it -> //dialogInterface.cancel()
                    dialogInterface.dismiss()
                }
                .show()
        }

        buttonBorrar!!.setOnClickListener {
            builder = AlertDialog.Builder(this)
            builder.setTitle("Borrar ejercicio")
                .setMessage("Deseas borrar este ejercicio?")
                .setCancelable(true)
                .setPositiveButton("Si") { dialogInterface, it ->
                    //Editar
                    borrar(num)
                    val intent = Intent(this@EditorEjercicios, PlantillasActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar") { dialogInterface, it -> //dialogInterface.cancel()
                    dialogInterface.dismiss()
                }
                .show()
        }


    }

    private fun guardar(Id: String, Nombre: String, Tipo: String, Peso: Boolean): Boolean {
        if (Nombre == "") { //Si el nombre esta vacio lo hara notar
            Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
            return false
        } else {
            val arreglo: Array<String?>
            arreglo = Nombre.split(" ").toTypedArray() //va a tomar el nuevo nombre

            var validadorNombre = true
            for (i in 0 until arreglo.size) {//recorre todo el nombre
                if (arreglo[i]!!.isDigitsOnly()) { //si uno de los datos es numero lo hara notar
                    Toast.makeText(
                        this,
                        "El nombre de un ejercicio no puede contener numeros",
                        Toast.LENGTH_SHORT
                    ).show()
                    validadorNombre = false
                }
            }

            if (validadorNombre == true) {
                MainActivity.user?.let { usuario ->
                    db.collection("users").document(usuario).collection("ejercicios").document(Id)
                        .set(
                            hashMapOf(
                                "id" to Id.toInt(),
                                "nombre" to Nombre,
                                "tipo" to Tipo,
                                "peso" to Peso
                            )
                        )
                }

                val linea: String;
                val cadenaCambio: String
                linea = num + " | " + nombre + " | " + tipo + " | " + peso //toma el ejercicio
                cadenaCambio = Id + " | " + Nombre + " | " + Tipo + " | " + Peso

                val posicion: Int
                posicion = MainActivity.listaEjercicios.indexOf(linea) //lo busca en la lista
                MainActivity.listaEjercicios.set(posicion, cadenaCambio) //y lo cambia

                Toast.makeText(this, "Se ha modificado el ejercicio", Toast.LENGTH_SHORT).show()
                return true
            } else {
                return false
            }
        }
    }

    private fun borrar(Id: String) {
        MainActivity.user?.let { usuario -> //abre la base de datos
            db.collection("users").document(usuario).collection("ejercicios").document(Id)
                .delete() //y borra el ejercicio seleccionado
        }

        val linea: String
        linea = num + " | " + nombre + " | " + tipo + " | " + peso //toma el ejercicio

        val posicion: Int
        posicion = MainActivity.listaEjercicios.indexOf(linea) //lo busca en la lista
        MainActivity.listaEjercicios.removeAt(posicion) //y lo borra

        Toast.makeText(this, "Se ha BORRADO el ejercicio", Toast.LENGTH_SHORT)
            .show() //manda mensaje de confirmación
    }


    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Editor de ejercicio"
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
            R.id.nav_metas -> callMetasActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()

            R.id.nav_musica -> callMusica()
            R.id.nav_amigos -> callAmigosActivity()
            R.id.Settings -> callAjustesActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
            R.id.nav_solicitudes -> callSolicitudesActivity()

        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
    }

    private fun callSolicitudesActivity() {
        val intent = Intent(this, SolicitudesActivity::class.java)
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

    private fun callSeguimientoActivity() {
        val intent = Intent(this, SeguimientoActivity::class.java)
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
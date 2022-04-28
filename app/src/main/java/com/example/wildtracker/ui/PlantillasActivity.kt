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
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.google.firebase.firestore.FirebaseFirestore

class PlantillasActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var buttonAdd: Button ?= null; var buttonRutina: Button ?= null; var buttonEjercicio: Button ?= null
    var listViewRutinas: ListView?= null

    var validadorMostar = 0
    var listado: java.util.ArrayList<String>? = null

    var ejerciciosPredeterminados = false

    private fun CargarTabla(){
        val datos1 = ArrayList<String>()

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Ejercicios from Rutinas"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        if (c.moveToFirst()) {
            do { //Mientras se haya movido de posicion va a tomar todos los datos de esa fila
                val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2)
                datos1.add(linea)
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        listado = datos1
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
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

        MainActivity.user?.let { usuario ->
            db.collection("users").document(usuario).collection("ejercicios")
                .get().addOnSuccessListener {
                if(it.isEmpty){
                    ejerciciosPredeterminados = true
                }
            }
        }

        CargarTabla()

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
            validadorLocalDB()
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
            val num = this.listado!![position].split(" ").toTypedArray()[0].toInt()
            val intent = Intent(this@PlantillasActivity, EditorRutinas::class.java)
            intent.putExtra("Num", num)
            startActivity(intent)
        }
    }

    private val db = FirebaseFirestore.getInstance()

    private fun validadorLocalDB(){
        if(ejerciciosPredeterminados) {
            var id = 1; var nombre = "Sentadillas"; var tipo = "Piernas"; var peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 2; nombre = "Saltos de tijera"; tipo = "Piernas"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 3; nombre = "Elevación de talones"; tipo = "Piernas"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 4; nombre = "Abdominales"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 5; nombre = "Plancha"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 6; nombre = "Escaladores"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 7; nombre = "Dominadas"; tipo = "Pecho"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 8; nombre = "Press de pecho"; tipo = "Pecho"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 9; nombre = "Peso muerto"; tipo = "Espalda"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 10; nombre = "Punches"; tipo = "Brazos"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 11; nombre = "Dips de tríceps"; tipo = "Brazos"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 12; nombre = "Press de hombros"; tipo = "Hombros"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 13; nombre = "Elevaciones laterales"; tipo = "Hombros"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 14; nombre = "Flexiones"; tipo = "Otro"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 15; nombre = "Burpees"; tipo = "Otro"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
        }
    }

    private fun ejerciciosLocalDB(Id: Int, Nombre: String, Tipo: String, Peso: Boolean){
        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("ejercicios").document(Id.toString()).set(
                hashMapOf(
                    "id" to Id,
                    "nombre" to Nombre,
                    "tipo" to Tipo,
                    "peso" to Peso
                )
            )
        }
    }

    /*
    private fun validadorLocalDB(){
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase()

        val sql = "select id from Ejercicios"
        val c = db.rawQuery(sql, null)

        if (!c.moveToFirst()) {
            var id = 1; var nombre = "Sentadillas"; var tipo = "Piernas"; var peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 2; nombre = "Saltos de tijera"; tipo = "Piernas"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 3; nombre = "Elevación de talones"; tipo = "Piernas"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 4; nombre = "Abdominales"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 5; nombre = "Plancha"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 6; nombre = "Escaladores"; tipo = "Abdomen"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 7; nombre = "Dominadas"; tipo = "Pecho"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 8; nombre = "Press de pecho"; tipo = "Pecho"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 9; nombre = "Peso muerto"; tipo = "Espalda"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 10; nombre = "Punches"; tipo = "Brazos"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 11; nombre = "Dips de tríceps"; tipo = "Brazos"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 12; nombre = "Press de hombros"; tipo = "Hombros"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 13; nombre = "Elevaciones laterales"; tipo = "Hombros"; peso = true
            ejerciciosLocalDB(id, nombre, tipo, peso)

            id = 14; nombre = "Flexiones"; tipo = "Otro"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
            id = 15; nombre = "Burpees"; tipo = "Otro"; peso = false
            ejerciciosLocalDB(id, nombre, tipo, peso)
        }
        c.close()
        db.close()
    }

    private fun ejerciciosLocalDB(Id: Int, Nombre: String, Tipo: String, Peso: Boolean){
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

        try {
            val c = ContentValues()
            c.put("Id", Id)
            c.put("Nombre", Nombre)
            c.put("Tipo", Tipo)
            c.put("Peso", Peso)
            db.insert("EJERCICIOS", null, c)
            db.close()
        } catch (e: Exception) {
        }
    }*/

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
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_ejercicio -> callEjercicioActivity()
            R.id.nav_maps -> callMapsActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            R.id.nav_metas -> callMetasActivity()
        }

        drawer.closeDrawer(GravityCompat.START) // cerrar menu

        return true
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

    private fun signOut() {
        LoginActivity.useremail = ""
        FirebaseAuth.getInstance().signOut()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("727481893022-adct709pnvj5tlihh532i6gjgm26thh6.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()
        //Cierra sesion y manda devuelta al login
        startActivity(Intent(this, LoginActivity::class.java))
    }

}
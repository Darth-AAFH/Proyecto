package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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
import android.database.sqlite.SQLiteDatabase
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class EjecicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var listViewRutinas2: ListView?= null
    var textViewRutina: TextView?= null
    var buttonIniciar: Button?= null

    var listado: java.util.ArrayList<String>? = null

    var num = 0

    private fun CargarTabla(){
        val datos = ArrayList<String>()

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        val sql = "select Id, Nombre, Ejercicios from Rutinas"
        val c = db.rawQuery(sql, null) //Se crea un cursor que ira avanzando de posicion uno a uno
        if (c.moveToFirst()) {
            do { //Mientras se haya movido de posicion va a tomar todos los datos de esa fila
                val linea = c.getString(0) + " | " + c.getString(1) + " | " + c.getString(2)
                datos.add(linea)
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        listado = datos
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado!!)
        listViewRutinas2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private lateinit var drawer: DrawerLayout
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejecicio)
        initToolbar()
        initNavigationView()

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)
        textViewRutina = findViewById(R.id.textViewRutina)
        buttonIniciar = findViewById(R.id.buttonIniciar); buttonIniciar!!.setVisibility(View.INVISIBLE); buttonIniciar!!.setEnabled(false)

        CargarTabla()

        listViewRutinas2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            var nombre = ""
            num = this.listado!![position].split(" ").toTypedArray()[0].toInt()

            val helper = LocalDB(this, "Demo", null, 1)
            val db: SQLiteDatabase = helper.getWritableDatabase()

            val sql = "select Nombre from Rutinas where Id = " + num
            val c = db.rawQuery(sql, null)
            if (c.moveToFirst()) {
                nombre = c.getString(0)
            }
            c.close()
            db.close()

            textViewRutina!!.setText(""+nombre)

            buttonIniciar!!.setVisibility(View.VISIBLE); buttonIniciar!!.setEnabled(true)
        }

        buttonIniciar!!.setOnClickListener{
            val intent = Intent(this@EjecicioActivity, PlantillasActivity::class.java)
            //intent.putExtra("Num", num)
            startActivity(intent)
        }
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Ejercicio"
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
        tvUser.text = LoginActivity.useremail

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
        val intent = Intent(this, EjecicioActivity::class.java)
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
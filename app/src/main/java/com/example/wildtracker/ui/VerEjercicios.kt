package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_ver_ejercicios.*

class VerEjercicios : AppCompatActivity(), OnNavigationItemSelectedListener {

    var listViewEjercicios: ListView?= null

    var listaEjercicios = ArrayList<String>()

    var cadena = "["

    private fun CargarTabla(){
        for(i in MainActivity.listaEjercicios){
            val arreglo = i.split(" ").toTypedArray()
            val id = arreglo[0].toInt()
            if(id > 15) {
                listaEjercicios.add(i)
            }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listaEjercicios)
        listViewEjercicios!!.setAdapter(adapter) //La tabla se adapta en la text view


        if(MainActivity.listaEjercicios.size <= 15){
            textViewAyudaVerEj.setVisibility(View.VISIBLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ejercicios)
        initToolbar()
        initNavigationView()
        listViewEjercicios = findViewById(R.id.listViewEjercicios)

        if(!MainActivity.listaRutinas.isEmpty()) { //para tomar los ejercicios que se estan usando
            for (i in 0..MainActivity.listaRutinas.size - 1) {
                cadena += MainActivity.listaRutinas[i].split(" | ").toTypedArray()[3] //agrega los ejercicios
                cadena += "," //y una coma
            }
        }

        var contador = 0
        for(i in 0 until cadena.length){
            contador += 1
        }
        cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

        val arreglo: Array<String?>
        arreglo = cadena.split(",").toTypedArray() //toma los ids de los ejercicios

        CargarTabla()
        Toast.makeText(this, "Click para editar ejercicio", Toast.LENGTH_SHORT).show()

        listViewEjercicios!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val num = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[0]
            var validadorEdicion = true

            for(i in arreglo){ //lo compara con todos los ejercicios de las rutinas
                if(num == i){ //si está en una rutina
                    validadorEdicion = false //no lo podra editar
                }
            }

            if(validadorEdicion) {
                val nombre = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[1]
                val tipo = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[2]
                val peso = MainActivity.listaEjercicios[position+15].split(" | ").toTypedArray()[3]

                val intent = Intent(this@VerEjercicios, EditorEjercicios::class.java)
                intent.putExtra("Num", num)
                intent.putExtra("Nombre", nombre)
                intent.putExtra("Tipo", tipo)
                intent.putExtra("Peso", peso)
                startActivity(intent)
            }else{
                Toast.makeText(this, "No se puede editar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Lista Ejercicios"
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
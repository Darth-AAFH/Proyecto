package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import com.google.firebase.firestore.FirebaseFirestore

class RankingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

    private fun CargarRanking () {

        var puntosUser: Int
        var nombreUser = ""

        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {

                /*
                for (usuario2 in it) { //para cada usuario
                    var user = usuario2.get("user") as String? //va a tomar el correo

                    MainActivity.user?.let { usuario -> //para cargar los puntos de cada usuario
                        db.collection("users").document(user).collection("tiempos") //abre la base de datos
                            .get().addOnSuccessListener {
                                puntosUser = 0
                                for(puntos in it){ //por todas las fechas que haya echo ejercicios
                                    puntosUser += (puntos.get("puntos") as Long).toInt() //los va a juntar
                                }
                            }
                    }

                    var linea = puntosUser.toString() //linea para hacer a lista
                    linea += "----------------" //separación de los puntos y el nombre

                    MainActivity.user?.let { usuario -> //para cargar el nombre de cada usuario
                        db.collection("users").document(user).get().addOnSuccessListener { //abre la base de datos
                                nombreUser = it.get("Name") as String //toma el nombre del usuario
                            }
                    }

                    linea += nombreUser //se le agrega el nombre del usuario a la lista
                    //add linea a un arreglo
                }

                //acomodar el arreglo de mayor a menor
                //poner el arreglo en la listView
                 */

                //val camilo = "nuevo@hotmail.com"
                val camilo = "camilo@gmail.com"
                var contadorAux = 0
                MainActivity.user?.let { usuario -> //para cargar el nombre de cada usuario
                    db.collection("users").document(camilo) //abre la base de datos
                        .get().addOnSuccessListener {
                            nombreUser = it.get("Name") as String
                        }
                }

                MainActivity.user?.let { usuario ->
                    db.collection("users").document(camilo).collection("tiempos") //abre la base de datos
                        .get().addOnSuccessListener {
                            puntosUser = 0
                            for(puntos in it){
                                puntosUser += (puntos.get("puntos") as Long).toInt()
                                //Toast.makeText(this, "Puntos : "+(puntos.get("puntos") as Long).toString(), Toast.LENGTH_SHORT).show()
                                contadorAux += 1
                                if(contadorAux >= 19){////////////////////////////////////////////////
                                    Toast.makeText(this, "Tienen en total: "+puntosUser+" puntos", Toast.LENGTH_SHORT).show()////
                                    Toast.makeText(this, "Nombre: "+nombreUser, Toast.LENGTH_SHORT).show()////
                                }
                            }
                        }
                }


            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        initToolbar()
        initNavigationView()

        CargarRanking()
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Ranking"
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
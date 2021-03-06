package com.example.wildtracker.ui

import android.annotation.SuppressLint
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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EjercicioActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var listViewRutinas2: ListView?= null
    var textViewRutina: TextView?= null
    var buttonIniciar: Button?= null

    private val db = FirebaseFirestore.getInstance()
    var num = 0; var nombre  = ""; var xp: Int? = null

    private fun CargarListas(){ //ayuda a organizar las listas de rutinas y los ejercicios
        if(MainActivity.validadorAcomodo){ //esto debe ir en plantillas y ejercicios
            MainActivity.listaRutinas = MainActivity.listaRutinas1
            MainActivity.listaRutinas.addAll(MainActivity.listaRutinas2)

            MainActivity.listaEjercicios = MainActivity.listaEjercicios1
            MainActivity.listaEjercicios.addAll(MainActivity.listaEjercicios2)

            MainActivity.validadorAcomodo = false
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaRutinas)
        listViewRutinas2!!.setAdapter(adapter) //La tabla se adapta en la text view
    }

    private fun CargarRanking() {
        MainActivity.listaRanking.clear() //limpiar la lista del ranking para poder recargarla

        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {

                GlobalScope.launch(Dispatchers.IO) { //para trer los datos correctamente por pausas

                    for (userIt in it) { //para cada usuario
                        val userEmail = userIt.get("email") as String? //va a tomar el correo
                        val nameDocument = Firebase.firestore.collection("users").document(userEmail.toString()) //la ruta en la base de datos
                        val user1 = nameDocument.get().await().toObject(PerfilActivity.userData::class.java) //y se va a traer los datos

                        withContext(Dispatchers.Main){
                            if(MainActivity.user == userEmail){ //si es el usuario en uso
                                MainActivity.listaRanking.add((user1!!.puntosTotales).toString() + " -- -- -- -- -- -- -- -- -- -- " + user1.Name + " ???") //lo agrega a la lista con una estrellita a modo de identificador
                            }else{
                                MainActivity.listaRanking.add((user1!!.puntosTotales).toString() + " -- -- -- -- -- -- -- -- -- -- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                            }
                        }
                    }
                }
            }
        }
    }

    private lateinit var drawer: DrawerLayout
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ejercicio)
        initToolbar()
        initNavigationView()

        listViewRutinas2 = findViewById(R.id.listViewRutinas2)
        textViewRutina = findViewById(R.id.textViewRutina)
        buttonIniciar = findViewById(R.id.buttonIniciar); buttonIniciar!!.setVisibility(View.INVISIBLE); buttonIniciar!!.setEnabled(false)

        Toast.makeText(this, "Seleccione la rutina", Toast.LENGTH_SHORT).show()
        CargarListas()
        CargarRanking()

        listViewRutinas2!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            num = MainActivity.listaRutinas[position].split(" ").toTypedArray()[0].toInt()
            nombre = MainActivity.listaRutinas[position].split(" | ").toTypedArray()[1]

            textViewRutina!!.setText("Rutina seleccionada: "+nombre)

            buttonIniciar!!.setVisibility(View.VISIBLE); buttonIniciar!!.setEnabled(true)

            var idRutina: Int
            MainActivity.user?.let { usuario -> //abre la base de datos
                db.collection("users").document(usuario).collection("rutinas").get().addOnSuccessListener {
                    for(rutinas in it){ //para cada rutina
                        idRutina = (rutinas.get("id") as Long).toInt() //toma el id de la rutina
                        if(idRutina == num){ //al encontrar la seleccionada
                            xp = (rutinas.get("xp") as Long).toInt() //guardara la xp que tiene
                        }
                    }

                }
            }
        }

        buttonIniciar!!.setOnClickListener{
            val intent = Intent(this@EjercicioActivity, EjecutadorRutina::class.java)
            intent.putExtra("Num", num)
            intent.putExtra("Nombre", nombre)
            intent.putExtra("XP", xp)
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
        tvUser.text = MainActivity.user

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_perfil -> callPerfilActivity()
            R.id.nav_inicio -> callInicioActivity()
            R.id.nav_plantillas -> callPlantillasActivity()
            R.id.nav_maps -> callMapsActivity()
            
            R.id.nav_ranking -> callRankingActivity()
            R.id.nav_chat -> callChatActivity()
            R.id.logOut -> signOut()
            
            R.id.nav_musica ->callMusica()

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
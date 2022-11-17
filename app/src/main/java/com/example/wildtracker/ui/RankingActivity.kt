package com.example.wildtracker.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.system.Os.remove
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.example.wildtracker.ui.MainActivity.Companion.listaRanking
import com.example.wildtracker.ui.MainActivity.Companion.listaSeguidores
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.errorprone.annotations.Var
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class RankingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance()
    private var listViewRanking: ListView?= null
    private var buttonRecargar: Button ?= null
    private lateinit var builder: AlertDialog.Builder
    private fun CargarRanking () {

        MainActivity.listaRanking1.sort(); MainActivity.listaRanking2.sort() //acomoda las listas de menor a mayor
        MainActivity.listaRanking3.sort(); MainActivity.listaRanking4.sort()

        listaRanking.clear()
        listaRanking.addAll(MainActivity.listaRanking1); listaRanking.addAll(MainActivity.listaRanking2)
        listaRanking.addAll(MainActivity.listaRanking3); listaRanking.addAll(MainActivity.listaRanking4)

        val array = ArrayList<String>() //arreglo auxiliar para acomodar las lista
        var i = listaRanking.size - 1
        while (i != -1) { //va a tomar el dato de la lista de ranking y lo va a acomodar al reves
            array.add(listaRanking[i])
            i--
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array)
        listViewRanking!!.setAdapter(adapter) //La lista se adapta en la text view
        listaRanking.sort()
        listViewRanking!!.setOnItemClickListener  { parent, view, position, id ->
            var Perfil:String  =  listaRanking[(listaRanking!!.size.toInt()- position.toInt())-1]
            Perfil = Perfil.substringAfter("-")
          //  Toast.makeText(this,MainActivity.listaRanking[(listaRanking!!.size.toInt()- position.toInt())-1]+"$Perfil",Toast.LENGTH_SHORT).show()
            AlertaSeguir(Perfil )
        }

    }


    private fun AlertaSeguir(perfil: String) {
        var perfil2 = perfil.substringAfter(" ")
        val progresDialog = ProgressDialog(this)
       // Toast.makeText(this, perfil2, Toast.LENGTH_SHORT).show()
        var perfilGet: String
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {

                    perfilGet = document.get("Name").toString()
                    var correo = document.get("email").toString()

                    if (perfil2 == perfilGet) {
                    val usuario =perfilGet
                  //  Toast.makeText(this,"Encontrado! "+document.get("Name").toString(),Toast.LENGTH_LONG).show()
                        //Si encuentro que coincide en firebase lo añado a amigos para desde ahi cargar sus datos de actividad fisica.
                      //  listaSeguidores.add(perfilGet)
                        builder = AlertDialog.Builder(this)
                        builder.setTitle("Seguir usuario")
                            .setMessage("Este es usuario al cual puedes comenzar a seguir")
                            .setCancelable(true)
                            .setPositiveButton("Seguir") { dialogInterface, it ->
                                MainActivity.user?.let {
                                    db.collection("users").document(it).collection("Seguidores").document().set(
                                        hashMapOf(
                                            "Nombre" to usuario
                                        )

                                    )
                                }
                            }
                            .setNegativeButton(
                                "Cancel",
                            ) { dialog, whichButton ->
                                dialog.dismiss()
                            }
                            .show()

                    }
                    else{

                       // Toast.makeText(this,"No se encontro... "+ perfilGet.length,Toast.LENGTH_LONG).show()
                      //  Toast.makeText(this,"Buscaba..."+ perfil2.length,Toast.LENGTH_LONG).show()
                    }
                Log.d("myTag", "Encontre:$perfilGet");
                Log.d("myTag", "Buscaba:$perfil2");

                if(progresDialog.isShowing) {
                    //Toast.makeText(this,"Encontrado! "+ document.get("Name").toString(),Toast.LENGTH_LONG).show()
                    Thread.sleep(1_000)  // wait for 1 second
                    progresDialog.dismiss()
                }


            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        val progresDialog = ProgressDialog(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        initToolbar()
        initNavigationView()

        buttonRecargar = findViewById(R.id.buttonRecargar)
        listViewRanking = findViewById(R.id.listViewRanking)
        progresDialog.setMessage("Cargando Datos")
        progresDialog.setCancelable(false)
        progresDialog.show()
        CargarRanking()

        buttonRecargar!!.setOnClickListener{////////////////////////////////////
            progresDialog.setMessage("Cargando Datos")
            progresDialog.setCancelable(false)
            progresDialog.show()
            CargarRanking()
            if(progresDialog.isShowing){
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        // This method will be executed once the timer is over
                        progresDialog.dismiss()
                    },
                    1000 // value in milliseconds
                )

            }
        }

        if(progresDialog.isShowing){
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    // This method will be executed once the timer is over
                    progresDialog.dismiss()
                },
                1200 // value in milliseconds
            )

        }

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
            R.id.nav_metas -> callMetasActivity()
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
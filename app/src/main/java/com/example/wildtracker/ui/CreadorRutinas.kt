package com.example.wildtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.GravityCompat
import com.example.wildtracker.LoginActivity
import java.util.*
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreadorRutinas : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var editTextNombre3: EditText ?= null
    private var buttonCrear2: Button?= null
    var listViewEjerciciosHechos: ListView?= null
    var listViewEjerciciosRutina: ListView?= null

    var listado2 = ArrayList<String>()
    var datos = ArrayList<String>()
    var contadorMaxEjer = 0

    var arregloRutinas = Array<rutina?>(51){null}
    var validadorVacia = true

    var contadorMaxRut = 0; var idFinalRut = 0;  var idAux = 0

    private val db = FirebaseFirestore.getInstance()

    private fun CargarTabla() { //Funcion que trae la tabla
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.listaEjercicios)
        listViewEjerciciosHechos!!.adapter = adapter //La tabla se adapta en la text view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creador_rutinas)
        initToolbar()
        initNavigationView()

        editTextNombre3 = findViewById<View>(R.id.editTextNombre3) as EditText
        buttonCrear2 = findViewById(R.id.buttonCrear2)
        listViewEjerciciosHechos = findViewById(R.id.listViewEjerciciosHechos)
        listViewEjerciciosRutina = findViewById(R.id.listViewEjerciciosRutina)

        Toast.makeText(this, "Click para añadir a la rutina", Toast.LENGTH_SHORT).show()
        CargarTabla()

        MainActivity.user?.let { usuario -> //para guardar el id final de las rutinas
            db.collection("users").document(usuario).collection("rutinas") //abre la base de datos
                .get().addOnSuccessListener {
                    for(rutina in it){ //para cada rutina
                        contadorMaxRut += 1 //cuenta cuantas rutinas hay
                        idAux = (rutina.get("id") as Long).toInt() //toma el id
                        if(idFinalRut < idAux){ //si es un id mayor
                            idFinalRut = (rutina.get("id") as Long).toInt() //lo va a guardar como el id final
                        }
                    }
                }
        }

        buttonCrear2!!.setOnClickListener{
            val nombre = editTextNombre3!!.text.toString()
            if(crear(nombre)){
                if(validadorVacia == true) {
                    val intent = Intent(this@CreadorRutinas, PlantillasActivity::class.java)
                    startActivity(intent)
                }
            }else {
                Toast.makeText(this, "Se ha alcanzado el numero maximo de rutinas", Toast.LENGTH_SHORT).show()
            }
        }

        listViewEjerciciosRutina!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            var linea: String
            linea = this.listado2[position].split(" ").toTypedArray()[0]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[1]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[2]; linea += " | "
            linea += this.listado2[position].split(" | ").toTypedArray()[3]

            val posicion = listado2.indexOf(linea) //Toma la posición del ejercicio en el array list

            listado2.removeAt(posicion) //Remueve el ejercicio del array list
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
            listViewEjerciciosRutina!!.adapter = adapter

            contadorMaxEjer -= 1
        }

        listViewEjerciciosHechos!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            if(contadorMaxEjer >= 10){ //un validador para que solo hayan max 10 ejercicios
                Toast.makeText(this, "Solo se pueden agregar 10 ejercicios a la rutina", Toast.LENGTH_SHORT).show()
            }else {
                var linea: String
                linea = MainActivity.listaEjercicios[position].split(" ").toTypedArray()[0]; linea += " | " //va a tomar el indice
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[1]; linea += " | " //nombre
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[2]; linea += " | " //tipo
                linea += MainActivity.listaEjercicios[position].split(" | ").toTypedArray()[3] //y peso del ejercicio seleccionado

                datos.add(linea) //y lo va a añadir a
                listado2 = datos //el listado de los ejercicios de rutina
                val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listado2)
                listViewEjerciciosRutina!!.adapter = adapter //después lo va a poner en la listView

                contadorMaxEjer += 1
            }
        }
    }

    private fun crear(Nombre: String): Boolean{
        var confirmacion = false
        if(contadorMaxRut <= 50){
            var nombre = Nombre

            if(nombre == "")
                nombre = "Rutina " + (idFinalRut + 1)

            var cadena: String //Variables para tomar los datos
            val arreglo: Array<String?>

            cadena = listado2.toString() //Toma la lista de los ejercicios
            arreglo = cadena.split(" ").toTypedArray() //arreglo tiene toda la lista separada por espacios

            cadena = arreglo[0].toString() //guarda el primer indice de los ejercicios
            cadena += ","
            var contador = 0
            for (i in 0 until arreglo.size) {//recorre todo el arreglo
                contador += 1
                if(arreglo[i]!!.isDigitsOnly()){ //si uno de los datos es numero
                    cadena += arreglo[i] //lo añade a la cadena
                    cadena += ","
                }
            }

            contador = 0
            for(i in 0 until cadena.length){
                contador += 1
            }
            cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

            validadorVacia = true
            if(cadena == "]"){
                Toast.makeText(this, "No se puede crear una rutina sin ejercicios", Toast.LENGTH_SHORT).show()
                validadorVacia = false
            }else {
                arregloRutinas[contadorMaxRut] = rutina(idFinalRut + 1, nombre, cadena)
                guardarBD(arregloRutinas[contadorMaxRut]!!)
            }

            confirmacion = true
        }
        return confirmacion
    }

    private fun guardarBD(Rutina: rutina) {
        val rutina2: String
        rutina2 = (Rutina.id).toString() + " | " + Rutina.nombre + " | Nivel: 0"
        MainActivity.listaRutinasVista.add(rutina2)

        MainActivity.user?.let{ usuario ->
            db.collection("users").document(usuario).collection("rutinas")
                .document(Rutina.id.toString()).set(
                    hashMapOf(
                        "id" to Rutina.id,
                        "nombre" to Rutina.nombre,
                        "ejercicios" to Rutina.ejercicios,
                        "nivel" to 0,
                        "xp" to 0,
                        "horas" to 0,
                        "minutos" to 0,
                        "segundos" to 0
                    )
                )
        }
        val rutina: String
        rutina = (Rutina.id).toString() + " | " + Rutina.nombre + " | Nivel: 0 | " + Rutina.ejercicios
        MainActivity.listaRutinas.add(rutina)

        Toast.makeText(this, "Se ha guardado la rutina", Toast.LENGTH_SHORT).show()

        recomendarRutinas()
    }

    fun recomendarRutinas(){ //Funcion que encuentra si un tipo de ejercicio se hace menos que los demas
        var piernas = 0; var abdomen = 0; var pecho = 0
        var espalda = 0; var brazos = 0; var hombros = 0

        for (i in MainActivity.listaRutinas) { //para todas las rutinas
            val ejercicios = i.split(" | ").toTypedArray()[3] //toma los ejercicios
            val arreglo = ejercicios.split(",").toTypedArray() //toma los ids de los ejercicios

            for(j in arreglo){ //va a recorrer todos los ejercicios

                for (k in MainActivity.listaEjercicios) { //para todos los ejercicios
                    val id = k.split(" ").toTypedArray()[0] //toma el id
                    if(j == id){ //si esta el ejercicio en la rutina

                        var tipo = k.split(" | ").toTypedArray()[2] // toma el tipo

                        if(tipo == "Piernas") {piernas += 1} //y lo añade a los tipos de ejercicios
                        if(tipo == "Abdomen") {abdomen += 1}
                        if(tipo == "Pecho") {pecho += 1}
                        if(tipo == "Espalda") {espalda += 1}
                        if(tipo == "Brazos") {brazos += 1}
                        if(tipo == "Hombros") {hombros += 1}
                    }
                }
            }
        }

        var max = piernas //variable que guardara que tipo de ejercicio se hace mas que los demas
        if(abdomen > max){max = abdomen}
        if(pecho > max){max = pecho}
        if(espalda > max){max = espalda}
        if(brazos > max){max = brazos}
        if(hombros > max){max = hombros}

        if(piernas <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas piernas", Toast.LENGTH_SHORT).show()
            //Notificacion piernas
        }
        if(abdomen <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas abdomen", Toast.LENGTH_SHORT).show()
            //Notificacion abdomen
        }
        if(pecho <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas pecho", Toast.LENGTH_SHORT).show()
            //Notificacion pecho
        }
        if(espalda <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas espalda", Toast.LENGTH_SHORT).show()
            //Notificacion espalda
        }
        if(brazos <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas brazos", Toast.LENGTH_SHORT).show()
            //Notificacion brazos
        }
        if(hombros <= (max * .25)){
            Toast.makeText(this, "Deberias trabajar mas hombros", Toast.LENGTH_SHORT).show()
            //Notificacion hombros
        }
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Creador rutinas"
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
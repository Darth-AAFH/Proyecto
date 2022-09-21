package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.example.wildtracker.LoginActivity
import com.example.wildtracker.R
import com.example.wildtracker.musica.mPlayerActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PerfilActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance()
    private lateinit var filepath:Uri
    /*var Perfil_birthday = findViewById<EditText>(R.id.Perfil_birthday)
    var Perfil_mail = findViewById<EditText>(R.id.Perfil_mail)
    var Perfil_name = findViewById<EditText>(R.id.Perfil_name)
*/

    companion object{
        lateinit var usernameDb : String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        initToolbar()
        initNavigationView()
        setup()

        ValidadorNombres()

        CargarTiempos()
        CargarRanking()

        CargarEjercicios()
        CargarRutinas()
    }

    val listaNombres = ArrayList<String>()

    private fun ValidadorNombres(){
        var cadena: String
        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {
                for (user in it) { //para cada usuario
                    cadena = user.get("Name").toString()
                    listaNombres.add(cadena)
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Perfil"
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

    var tiempoAux: Int = 0
    @SuppressLint("SimpleDateFormat")
    private fun CargarTiempos() {
        var sdf = SimpleDateFormat("dd")
        var diaHoy = sdf.format(Date()) //se obtiene e dia actual
        sdf = SimpleDateFormat("MM")
        var mesHoy = sdf.format(Date()) //se obtiene el mes actual
        sdf = SimpleDateFormat("yyyy")
        val anoHoy = sdf.format(Date()) //se obiene el año actual
        val diaSemHoy = diaSemana(diaHoy.toInt(), mesHoy.toInt(), anoHoy.toInt()) //se obtiene el numero de dia de la semana (lunes = 1, martes = 2, miercoles = 3, etc)

        val dias: ArrayList<String> = arrayListOf<String>() //arreglo de string?
        dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add(""); dias.add("") //se llena el arreglo de dias con datos vacios

        var contadorAux = 0 //un contador auxiliar para encontrar los demas días

        for (i in diaSemHoy downTo 0) { //para el numero de dia de la semana para toda la semana pasada
            var diaAux = diaHoy.toInt() - contadorAux //variable que va a modificarse segun el día, inicia siendo el dia actual menos 0

            if(diaAux == 0){ //en caso de que llegue a cero el día
                val mesAux = mesHoy.toInt() //una variable para cambiarle el valor al mes

                if(mesAux == 3){
                    diaHoy = "28"
                    diaAux = 28
                }else{
                    if(mesAux == 2 || mesAux == 4 || mesAux == 6 || mesAux == 8 || mesAux == 9 || mesAux == 11 || mesAux == 1) {
                        diaHoy = "31"
                        diaAux = 31
                    }else{
                        diaHoy = "30"
                        diaAux = 30
                    }
                }

                if((mesAux - 1) < 10){//fallo para cambio de año
                    mesHoy = "0" + (mesHoy.toInt() - 1).toString()
                }else{
                    mesHoy = (mesHoy.toInt() - 1).toString()
                }
                contadorAux = 0
            }

            if(diaAux < 10){ //se guardan en el arreglo de días los dias pasados y se les da el formato
                dias[i] = "0" + diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }else {
                dias[i] = diaAux.toString() + "-" + mesHoy + "-" + anoHoy
            }

            contadorAux+= 1 //se le agrega al contador un numero más para retorceder más días
        }

        MainActivity.user?.let { usuario -> //trae los tiempos segun el día
            db.collection("users").document(usuario).collection("tiempos") //abre la base de datos
                .get().addOnSuccessListener {
                    for(tiempos in it) { //por cada dia registrado
                        val idFecha = tiempos.get("idFecha") as String //toma la fecha

                        if(idFecha == dias[1]){ //si la fecha es igual al dia lunes guardado
                            MainActivity.lunes = (tiempos.get("minutos") as Long).toDouble() //guardara el tiempo en la variable del dia

                            tiempoAux = (tiempos.get("horas") as Long).toInt() //de horas a minutos
                            MainActivity.lunes += tiempoAux * 60

                            tiempoAux = (tiempos.get("segundos") as Long).toInt() //y de segundos a minutos
                            MainActivity.lunes += tiempoAux / 60
                        }
                        if(idFecha == dias[2]){ //y así con las demas fechas
                            MainActivity.martes = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.martes += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.martes += tiempoAux / 60
                        }
                        if(idFecha == dias[3]){
                            MainActivity.miercoles = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.miercoles += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.miercoles += tiempoAux / 60
                        }
                        if(idFecha == dias[4]){
                            MainActivity.jueves = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.jueves += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.jueves += tiempoAux / 60
                        }
                        if(idFecha == dias[5]){
                            MainActivity.viernes = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.viernes += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.viernes += tiempoAux / 60
                        }
                        if(idFecha == dias[6]){
                            MainActivity.sabado = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.sabado += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.sabado += tiempoAux / 60
                        }
                        if(idFecha == dias[7]){
                            MainActivity.domingo = (tiempos.get("minutos") as Long).toDouble()
                            tiempoAux = (tiempos.get("horas") as Long).toInt(); MainActivity.domingo += tiempoAux * 60
                            tiempoAux = (tiempos.get("segundos") as Long).toInt(); MainActivity.domingo += tiempoAux / 60
                        }
                    }
                }
        }
    }
    private fun diaSemana(dia: Int, mes: Int, ano: Int): Int {
        val c = Calendar.getInstance()
        c.set(ano, mes, dia)

        val diaSem =  c.get(Calendar.DAY_OF_WEEK)

        if(diaSem == 4){
            return 7 //domingo
        }
        if(diaSem == 5){
            return 1 //lunes
        }
        if(diaSem == 6){
            return 2 //martes
        }
        if(diaSem == 7){
            return 3 //miercoles
        }
        if(diaSem == 1){
            return 4 //jueves
        }
        if(diaSem == 2){
            return 5 //viernes
        }
        if(diaSem == 3){
            return 6 //sabado
        }
        return 0
    }

    private fun callInicioActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun CargarEjercicios(){
        var cadena: String; var id: Int
        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario)
                    .collection("ejercicios") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (ejercicio in it) { //para cada ejercicio
                            id = (ejercicio.get("id") as Long).toInt()
                            if(id < 10) {
                                cadena = id.toString() //toma el id del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("nombre").toString() //toma el nombre del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("tipo").toString() //toma el tipo
                                cadena += " | " //le pone un texto para darle orden
                                val pesoAux = ejercicio.get("peso").toString()
                                if (pesoAux == "true") {
                                    cadena += "Con peso"
                                } else {
                                    cadena += "Sin peso"
                                }
                                MainActivity.listaEjercicios1.add(cadena)//y lo guarda en la primer lista
                            }else{
                                cadena = id.toString() //toma el id del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("nombre").toString() //toma el nombre del ejercicio
                                cadena += " | " //le pone un texto para darle orden
                                cadena += ejercicio.get("tipo").toString() //toma el tipo
                                cadena += " | " //le pone un texto para darle orden
                                val pesoAux = ejercicio.get("peso").toString()
                                if (pesoAux == "true") {
                                    cadena += "Con peso"
                                } else {
                                    cadena += "Sin peso"
                                }
                                MainActivity.listaEjercicios2.add(cadena) //y guarda en la segunda lista
                            }
                        }
                    }
            }
            MainActivity.listaEjercicios1.sort(); MainActivity.listaEjercicios2.sort()// acomoda las listas
        }
    }
    private fun CargarRutinas(){
        var cadena: String; var id: Int
        if(MainActivity.validadorListas) {
            MainActivity.user?.let { usuario -> //para cargar las rutinas
                db.collection("users").document(usuario)
                    .collection("rutinas") //abre la base de datos
                    .get().addOnSuccessListener {
                        for (rutina in it) { //para cada rutina
                            id = (rutina.get("id") as Long).toInt()
                            if(id < 10) {
                                cadena = (rutina.get("id") as Long).toString() //toma el id de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                                cadena += " | Nivel: " //le pone un texto para darle orden
                                cadena += (rutina.get("nivel") as Long).toString() //toma el nivel de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                MainActivity.listaRutinas1.add(cadena)
                            }else{
                                cadena = (rutina.get("id") as Long).toString() //toma el id de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("nombre").toString() //toma el nombre de la rutina
                                cadena += " | Nivel: " //le pone un texto para darle orden
                                cadena += (rutina.get("nivel") as Long).toString() //toma el nivel de la rutina
                                cadena += " | " //le pone un texto para darle orden
                                cadena += rutina.get("ejercicios").toString() //toma los ejercicios
                                MainActivity.listaRutinas2.add(cadena)
                            }
                        }
                    }
            }
            MainActivity.listaRutinas1.sort(); MainActivity.listaRutinas2.sort()// acomoda las listas
            MainActivity.validadorListas = false //cambia el validador para que esto no se vuelva a hacer
        }
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

    data class userData (
        val Name: String? = "",
        var puntosTotales: Int? = 0
    )
    private fun CargarRanking () {
        MainActivity.listaRanking.clear() //limpiar la lista del ranking para poder recargarla

        MainActivity.user?.let { usuario -> //para cargar el ranking
            db.collection("users").get().addOnSuccessListener {

                GlobalScope.launch(Dispatchers.IO) { //para trer los datos correctamente por pausas

                    for (userIt in it) { //para cada usuario
                        val userEmail = userIt.get("email") as String? //va a tomar el correo
                        val nameDocument = Firebase.firestore.collection("users").document(userEmail.toString()) //la ruta en la base de datos
                        val user1 = nameDocument.get().await().toObject(userData::class.java) //y se va a traer los datos

                        withContext(Dispatchers.Main){
                            if(MainActivity.user == userEmail){ //si es el usuario en uso
                                MainActivity.listaRanking.add((user1!!.puntosTotales).toString() + " -- -- -- -- -- -- -- -- -- -- " + user1.Name + " ✰") //lo agrega a la lista con una estrellita a modo de identificador
                            }else{
                                MainActivity.listaRanking.add((user1!!.puntosTotales).toString() + " -- -- -- -- -- -- -- -- -- -- " + user1.Name) //y si no los va a agregar pero sin la estrellita
                            }
                        }
                    }
                }
            }
        }
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

    private fun setup(){

        val EditProfileDataButton = findViewById<Button>(R.id.EditProfileDataButton)
        val recoverProfileDataButton = findViewById<Button>(R.id.recoverProfileDataButton)
        val saveProfileButton = findViewById<Button>(R.id.saveProfileButton)
        val ChangeProfilePicButton = findViewById<Button>(R.id.ChangeProfilePicButton)
        val edBirthDay =   findViewById<EditText>(R.id.Perfil_birthday)
        val edEmail =   findViewById<EditText>(R.id.Perfil_mail)
        val edName =   findViewById<EditText>(R.id.Perfil_name)
        val ivProfilePic = findViewById<ImageView>(R.id.Perfil_pic)

        edBirthDay.isEnabled = false
        edEmail.isEnabled = false
        edName.isEnabled = false
        saveProfileButton.isVisible = false
        ChangeProfilePicButton.isVisible = false
        EditProfileDataButton.isVisible = false

        EditProfileDataButton.isVisible = true
        recoverProfileDataButton.isVisible = false
        MainActivity.user?.let { it1 ->
            db.collection("users").document(MainActivity.user!!).get()
                .addOnSuccessListener {
                edName.setText (it.get("Name") as String?)
                edEmail.setText(it.get("email") as String?)
                edBirthDay.setText(it.get("birthDay") as String?)

            }

        }

        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Imagen")
        progresDialog.setCancelable(false)
        progresDialog.show()


        val userID =FirebaseAuth.getInstance().currentUser!!.email.toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
        val localfile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localfile).addOnSuccessListener{

            if(progresDialog.isShowing){
                progresDialog.dismiss()
                try {
                    MainActivity.user?.let { it1 ->
                        db.collection("users").document(MainActivity.user!!).get()
                            .addOnSuccessListener {
                                Companion.usernameDb = ((it.get("Name") as String?).toString())
                            }
                    }

                   // usernameDb = edName.text.toString()

                }catch (e: Exception){
                    MainActivity.user?.let { it1 ->
                        db.collection("users").document(MainActivity.user!!).get()
                            .addOnSuccessListener {
                                Companion.usernameDb = ((it.get("Name") as String?).toString())
                            }
                    }
                }
            }
            val bitmap =BitmapFactory.decodeFile(localfile.absolutePath)
            ivProfilePic.setImageBitmap(bitmap)
        }.addOnFailureListener{
            progresDialog.dismiss()
            Toast.makeText(this,"Recuperación de imagen fallida, sube otra foto",Toast.LENGTH_SHORT).show()
            try {
                MainActivity.user?.let { it1 ->
                    db.collection("users").document(MainActivity.user!!).get()
                        .addOnSuccessListener {
                            Companion.usernameDb = ((it.get("Name") as String?).toString())
                        }
                }

                // usernameDb = edName.text.toString()

            }catch (e: Exception){
                MainActivity.user?.let { it1 ->
                    db.collection("users").document(MainActivity.user!!).get()
                        .addOnSuccessListener {
                            Companion.usernameDb = ((it.get("Name") as String?).toString())
                        }
                }
            }
        }




        saveProfileButton.setOnClickListener{
            var Name = findViewById<EditText>(R.id.Perfil_name).text.toString()
            var NombreDisponible: Boolean = true

            for(item in listaNombres){
                if (Name == item){
                    NombreDisponible = false
                }
            }

            if(NombreDisponible) {
                MainActivity.user?.let { usuario ->
                    db.collection("users").document(usuario).set(
                        hashMapOf(
                            "birthDay" to findViewById<EditText>(R.id.Perfil_birthday).text.toString(),
                            "email" to findViewById<EditText>(R.id.Perfil_mail).text.toString(),
                            "Name" to findViewById<EditText>(R.id.Perfil_name).text.toString(),
                        )
                    )
                }
            }else {
                Toast.makeText(this, "Nombre de usuario no disponible", Toast.LENGTH_SHORT).show()
            }

            saveProfileButton.isVisible = false
            edBirthDay.isEnabled = false
            edEmail.isEnabled = false
            edName.isEnabled = false
            EditProfileDataButton.isVisible = true
        }

        EditProfileDataButton.setOnClickListener {
            saveProfileButton.isVisible = true
              edBirthDay.isEnabled = true
            edName.isEnabled = true
            EditProfileDataButton.isVisible = false

        }

        ChangeProfilePicButton.setOnClickListener {
        uploadFile()
        }
        ivProfilePic.setOnClickListener {
          startFileChooser()
            ChangeProfilePicButton.isVisible = true
        }

    }

    private fun uploadFile() {
        val ChangeProfilePicButton = findViewById<Button>(R.id.ChangeProfilePicButton)
        val  userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        if (filepath != null) {
            var pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()
            var imageRef = FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
            imageRef.putFile(filepath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "${userID}", Toast.LENGTH_LONG).show()

                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { p0 ->
                    var progress = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    pd.setMessage("Uploaded ${progress.toInt()}%")
                }


        }
        ChangeProfilePicButton.isVisible = false
    }

    private fun startFileChooser() {
        var i = Intent()
        i.setType("image/*").action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i,"Elige una imagen"),111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val ivProfilePic = findViewById<ImageView>(R.id.Perfil_pic)
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==111 && resultCode==Activity.RESULT_OK && data!=null)
        {
            filepath =data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filepath)
            ivProfilePic.setImageBitmap(bitmap)
        }
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
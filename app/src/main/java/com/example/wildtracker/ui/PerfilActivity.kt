package com.example.wildtracker.ui

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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PerfilActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private val db = FirebaseFirestore.getInstance()
    private lateinit var filepath:Uri
    /*var Perfil_birthday = findViewById<EditText>(R.id.Perfil_birthday)
    var Perfil_mail = findViewById<EditText>(R.id.Perfil_mail)
    var Perfil_name = findViewById<EditText>(R.id.Perfil_name)
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        initToolbar()
        initNavigationView()
          setup()

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

        saveProfileButton.setOnClickListener{
            MainActivity.user?.let { usuario ->
                db.collection("users").document(usuario).collection("Ejercicios").document().set(
                    hashMapOf( "birthDay"  to  findViewById<EditText>(R.id.Perfil_birthday).text.toString(),
                        "email" to findViewById<EditText>(R.id.Perfil_mail).text.toString(),
                        "Name" to findViewById<EditText>(R.id.Perfil_name).text.toString(),
                    )
                )
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
            edEmail.isEnabled = true
            edName.isEnabled = true
            EditProfileDataButton.isVisible = false

        }

        recoverProfileDataButton.setOnClickListener {
            EditProfileDataButton.isVisible = true
            recoverProfileDataButton.isVisible = false
            MainActivity.user?.let { it1 ->
                db.collection("users").document(it1).get().addOnSuccessListener{
                    var nomber = (it.get("birthDay") as String?)
                    edEmail.setText(it.get("email") as String?)
                    edName.setText (it.get("Name") as String?)
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
                }
                val bitmap =BitmapFactory.decodeFile(localfile.absolutePath)
                ivProfilePic.setImageBitmap(bitmap)


            }.addOnFailureListener{
                progresDialog.dismiss()
                Toast.makeText(this,"Fallo el recuperar imagen",Toast.LENGTH_SHORT).show()

            }

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




}
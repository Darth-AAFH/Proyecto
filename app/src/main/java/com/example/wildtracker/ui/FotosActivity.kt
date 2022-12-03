package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_fotos.*
import java.io.File


class FotosActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotos)
        initToolbar()
        initNavigationView()
        var pd = ProgressDialog(this)
        pd.setTitle("Cargando fotos")
        pd.show()
        val intento = intent
        val db = FirebaseFirestore.getInstance()


        val userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val storageRef =
            FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
        val FotoInicial =
            FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_${insigniaAdapter.nombreCajaFotos}/foto_${insigniaAdapter.nombreCajaFotos}_1")
        val FotoFinal =
            FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_${insigniaAdapter.nombreCajaFotos}/foto_${insigniaAdapter.nombreCajaFotos}_2")
        val FotoMetaInicial =
            FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_${metasAdapter.nombreCajaFotos}/foto_${metasAdapter.nombreCajaFotos}_1")
        val FotoMetaFinal =
            FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Meta_${metasAdapter.nombreCajaFotos}/foto_${metasAdapter.nombreCajaFotos}_2")
        val localfileInicialFotoInicial = File.createTempFile("tempImage", "jpg")
        val localfileFinalFotoFinal = File.createTempFile("tempImage", "jpg")
        val localfileInicialFotoInicialMeta = File.createTempFile("tempImage", "jpg")
        val localfileFinalFotoFinalMeta = File.createTempFile("tempImage", "jpg")

        /* storageRef.getFile(localfile).addOnSuccessListener {
             val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            // fotoinicio.setImageBitmap(bitmap)
             //ultimafoto.setImageBitmap(bitmap)

             Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
         }.addOnFailureListener {
                 Toast.makeText(
                     this,
                     "Recuperación de imagen fallida, sube otra foto",
                     Toast.LENGTH_SHORT
                 ).show()
             }
 */

        if (!intento.getBooleanExtra("Ismeta", false)) {

            // Toast.makeText(this,"No es meta",Toast.LENGTH_LONG).show()
            FotoInicial.getFile(localfileInicialFotoInicial).addOnSuccessListener {
                if (pd.isShowing) {
                    try {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }

                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }

                var bitmapInicial =
                    BitmapFactory.decodeFile(localfileInicialFotoInicial.absolutePath)
                fotoinicio.setImageBitmap(bitmapInicial)
                bitmapInicial.recycle()
                bitmapInicial = BitmapFactory.decodeFile(localfileInicialFotoInicial.absolutePath)
                fotoinicio.setImageBitmap(bitmapInicial)
                pd.dismiss()
                // Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {

            }

            FotoFinal.getFile(localfileFinalFotoFinal).addOnSuccessListener {
                if (pd.isShowing) {

                    try {


                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }

                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }
                var bitmapFinal = BitmapFactory.decodeFile(localfileFinalFotoFinal.absolutePath)
                bitmapFinal.recycle()
                ultimafoto.setImageBitmap(bitmapFinal)
                bitmapFinal = BitmapFactory.decodeFile(localfileFinalFotoFinal.absolutePath)
                ultimafoto.setImageBitmap(bitmapFinal)
                pd.dismiss()
                // Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {

                //pd.dismiss()
            }

        } else {
            //  Toast.makeText(this,"Es meta",Toast.LENGTH_LONG).show()
            FotoMetaInicial.getFile(localfileInicialFotoInicialMeta).addOnSuccessListener {
                if (pd.isShowing) {
                    try {
                        pd.show()
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }

                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }
                var bitmapInicial =
                    BitmapFactory.decodeFile(localfileInicialFotoInicialMeta.absolutePath)
                bitmapInicial.recycle()
                fotoinicio.setImageBitmap(bitmapInicial)
                bitmapInicial =
                    BitmapFactory.decodeFile(localfileInicialFotoInicialMeta.absolutePath)
                fotoinicio.setImageBitmap(bitmapInicial)
                pd.dismiss()
                //   Toast.makeText(this, "${intento.getStringExtra("Meta")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {

                // pd.dismiss()

            }
            FotoMetaFinal.getFile(localfileFinalFotoFinalMeta).addOnSuccessListener {
                if (pd.isShowing) {
                    try {


                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }
                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb =
                                        ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }
                var bitmapFinal = BitmapFactory.decodeFile(localfileFinalFotoFinalMeta.absolutePath)
                bitmapFinal.recycle()
                ultimafoto.setImageBitmap(bitmapFinal)
                bitmapFinal = BitmapFactory.decodeFile(localfileFinalFotoFinalMeta.absolutePath)
                ultimafoto.setImageBitmap(bitmapFinal)
                pd.dismiss()
                //.makeText(this, "${intento.getStringExtra("Meta")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {
                pd.dismiss()
            }
        }

    }

    private fun initToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        toolbar.title = "Fotos"
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
            R.id.nav_musica -> callMusica()
            R.id.nav_amigos -> callAmigosActivity()
            R.id.Settings -> callAjustesActivity()
            R.id.nav_seguimiento -> callSeguimientoActivity()
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


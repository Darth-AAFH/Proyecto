package com.example.wildtracker.ui

import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wildtracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_fotos.*
import java.io.File


class FotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotos)
        val progresDialog = ProgressDialog(this)
        progresDialog.setMessage("Cargando Imagen")
        progresDialog.setCancelable(false)
        progresDialog.show()
        val intento = intent
        val db = FirebaseFirestore.getInstance()


        val userID = FirebaseAuth.getInstance().currentUser!!.email.toString()
        val storageRef =
            FirebaseStorage.getInstance().reference.child("UsersProfileImages/$userID.jpg")
        val FotoInicial =  FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_${insigniaAdapter.nombreCajaFotos}/foto_${insigniaAdapter.nombreCajaFotos}_1")
        val FotoFinal =  FirebaseStorage.getInstance().reference.child("UsersTakenPictures/$userID/Rutina_${insigniaAdapter.nombreCajaFotos}/foto_${insigniaAdapter.nombreCajaFotos}_2")
        val localfileInicial =  File.createTempFile("tempImage", "jpg")
        val localfileFinal = File.createTempFile("tempImage", "jpg")

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
            FotoInicial.getFile(localfileInicial).addOnSuccessListener {
                if (progresDialog.isShowing) {
                    progresDialog.dismiss()
                    try {
                        progresDialog.show()
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb = ((it.get("Name") as String?).toString())
                                }
                        }

                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb = ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }
                val bitmapInicial = BitmapFactory.decodeFile(localfileInicial.absolutePath)
                fotoinicio.setImageBitmap(bitmapInicial)
                Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Recuperación de imagen fallida, sube otra foto",
                    Toast.LENGTH_SHORT
                ).show()
            }

            FotoFinal.getFile(localfileFinal).addOnSuccessListener {
                if (progresDialog.isShowing) {
                    progresDialog.dismiss()
                    try {


                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb = ((it.get("Name") as String?).toString())
                                }
                        }

                        // usernameDb = edName.text.toString()

                    } catch (e: Exception) {
                        MainActivity.user?.let { it1 ->
                            db.collection("users").document(MainActivity.user!!).get()
                                .addOnSuccessListener {
                                    PerfilActivity.usernameDb = ((it.get("Name") as String?).toString())
                                }
                        }
                    }
                }
                val bitmapFinal = BitmapFactory.decodeFile(localfileFinal.absolutePath)
                ultimafoto.setImageBitmap(bitmapFinal)
                Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Recuperación de imagen fallida, sube otra foto",
                    Toast.LENGTH_SHORT
                ).show()
            }

            progresDialog.dismiss()

    }


}



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
        val localfile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localfile).addOnSuccessListener {

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
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            fotoinicio.setImageBitmap(bitmap)
            ultimafoto.setImageBitmap(bitmap)
            Toast.makeText(this, "${intento.getStringExtra("Rutina")}", Toast.LENGTH_LONG).show() // Muestra el nombre de la rutina cachado en insignia adapter
        }.addOnFailureListener {
            progresDialog.dismiss()
            Toast.makeText(
                this,
                "Recuperación de imagen fallida, sube otra foto",
                Toast.LENGTH_SHORT
            ).show()
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
    }


    }




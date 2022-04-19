package com.example.wildtracker.ui

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.example.wildtracker.R

@Suppress("NAME_SHADOWING")
class EditorEjercicios : AppCompatActivity() {

    var editTextNombre2: EditText ?= null
    @SuppressLint("UseSwitchCompatOrMaterialCode") private var switchPeso2: Switch ?= null
    private var buttonGuardar: Button?= null; private var buttonBorrar: Button?= null

    var num = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor_ejercicios)

        val b = intent.extras //b toma los datos enviados del Listado
        if (b != null) { //Si existen datos
            num = b.getInt("Num") //Los guardara en estas variables
        }

        editTextNombre2 = findViewById<View>(R.id.editTextNombre2) as EditText
        switchPeso2 = findViewById<View>(R.id.switchPeso2) as Switch
        buttonGuardar = findViewById(R.id.buttonGuardar)
        buttonBorrar = findViewById(R.id.buttonBorrar)
        val spinnerTipos2: Spinner = findViewById(R.id.spinnerTipos2)

        val lista0 = listOf("Piernas", "Abdomen", "Pecho", "Espalda", "Brazos", "Hombros", "Otro")
        val adaptador0 = ArrayAdapter(this, android.R.layout.simple_spinner_item, lista0)
        spinnerTipos2.adapter = adaptador0

        var nombre = ""; var tipo = ""; var peso = 0

        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getWritableDatabase()

        val sql ="select Id, Nombre, Tipo, Peso from Ejercicios where Id = "+num
        val c = db.rawQuery(sql, null)
        if (c.moveToFirst()) {
            nombre =  c.getString(1)
            tipo =  c.getString(2)
            peso = c.getInt(3)
        }
        c.close()
        db.close()

        editTextNombre2!!.setText(nombre)
        if(tipo == "Piernas") spinnerTipos2.setSelection(0)
        if(tipo == "Abdomen") spinnerTipos2.setSelection(1)
        if(tipo == "Pecho") spinnerTipos2.setSelection(2)
        if(tipo == "Espalda") spinnerTipos2.setSelection(3)
        if(tipo == "Brazos") spinnerTipos2.setSelection(4)
        if(tipo == "Hombros") spinnerTipos2.setSelection(5)
        if(tipo == "Otro") spinnerTipos2.setSelection(6)
        if(peso == 1) switchPeso2!!.setChecked(true)
        if(peso == 0) switchPeso2!!.setChecked(false)

        buttonGuardar!!.setOnClickListener{
            val helper = LocalDB(this, "Demo", null, 1)
            val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

            var cadena = "["
            val sql = "select Ejercicios from Rutinas"
            val c = db.rawQuery(sql, null) //Se crea un cursor
            if (c.moveToFirst()) {
                do {
                    cadena += c.getString(0)
                    cadena += ","
                } while (c.moveToNext())
            }
            c.close()
            db.close()

            var contador = 0
            for(i in 0 until cadena.length){
                contador += 1
            }
            cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

            var validadorGuardar = true
            val arreglo: Array<String?>

            arreglo = cadena.split(",").toTypedArray() //arreglo tiene todos los ejercicios de todas las rutinas

            for (i in 0 until arreglo.size) {//recorre todo el arreglo
                if(num == arreglo[i]!!.toInt()){ //si el ejercicio que se desea borrar esta en los de las rutinas
                    validadorGuardar = false
                }
            }

            if(validadorGuardar == true) {
                val cambioNombre = editTextNombre2!!.text.toString(); val cambioTipo = spinnerTipos2.selectedItem.toString(); val cambioPeso = switchPeso2!!.isChecked()
                if(guardar(num, cambioNombre, cambioTipo, cambioPeso))
                    onBackPressed()
            }else{
                Toast.makeText(this, "No se puede editar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBorrar!!.setOnClickListener{
            if(borrar(num))
                onBackPressed()
        }

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
    }

    private fun guardar(Id: Int, Nombre: String, Tipo: String, validadorPeso: Boolean): Boolean{
        if(Nombre == ""){ //Si el nombre esta vacio lo hara notar
            Toast.makeText(this, "El nombre no puede estar vacio", Toast.LENGTH_SHORT).show()
            return false
        }else {
            val arreglo: Array<String?>
            arreglo = Nombre.split(" ").toTypedArray() //va a tomar el nuevo nombre

            var validadorNombre = true
            for (i in 0 until arreglo.size) {//recorre todo el nombre
                if(arreglo[i]!!.isDigitsOnly()) { //si uno de los datos es numero lo hara notar
                    Toast.makeText(this, "El nombre de un ejercicio no puede contener numeros", Toast.LENGTH_SHORT).show()
                    validadorNombre = false
                }
            }

            if(validadorNombre == true){
                var Peso = 0
                if (validadorPeso == true) {
                    Peso = 1
                }

                val helper = LocalDB(this, "Demo", null, 1)
                val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

                val sql = "update Ejercicios set Nombre='" + Nombre + "',Tipo='" + Tipo + "',Peso='" + Peso + "' where Id=" + Id
                db.execSQL(sql) //Se actualizan los datos por los nuevos que introdujo el usuario
                db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
                Toast.makeText(this, "Se ha modificado el ejercicio (incluso si no lo nota)", Toast.LENGTH_SHORT).show()
                return true
            }else{
                return false
            }
        }
    }

    private fun borrar(Id: Int): Boolean{
        val helper = LocalDB(this, "Demo", null, 1)
        val db: SQLiteDatabase = helper.getReadableDatabase() //Se abre la base de datos

        var cadena = "["
        val sql = "select Ejercicios from Rutinas"
        val c = db.rawQuery(sql, null) //Se crea un cursor
        if (c.moveToFirst()) {
            do {
                cadena += c.getString(0)
                cadena += ","
            } while (c.moveToNext())
        }
        c.close()
        db.close()

        var contador = 0
        for(i in 0 until cadena.length){
            contador += 1
        }
        cadena = cadena.substring(1, contador - 1) //quita el '[' y la última coma

        var validadorBorrar = true
        val arreglo: Array<String?>

        arreglo = cadena.split(",").toTypedArray() //arreglo tiene todos los ejercicios de todas las rutinas

        for (i in 0 until arreglo.size) {//recorre todo el arreglo
            if(Id == arreglo[i]!!.toInt()){ //si el ejercicio que se desea borrar esta en los de las rutinas
                validadorBorrar = false
            }
        }

        if(validadorBorrar == true) {
            val helper = LocalDB(this, "Demo", null, 1)
            val db: SQLiteDatabase = helper.getWritableDatabase() //Se abre la base de datos

            val sql =
                "delete from Ejercicios where Id=" + Id //Se hace la consulta para borrar el registro
            db.execSQL(sql) //Se ejecuta la consulta
            db.close() //Se cierra la base de datos y se manda mensaje de confirmacion
            Toast.makeText(this, "Se ha BORRADO el ejercicio (incluso si no lo nota)", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No se puede borrar un ejercicio que esta siendo utilizado en una rutina", Toast.LENGTH_SHORT).show()
        }
        return validadorBorrar
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
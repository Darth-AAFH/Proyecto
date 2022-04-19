package com.example.wildtracker.ui

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class LocalDB (context: Context?, name: String?, factory: CursorFactory?, version: Int) :

    SQLiteOpenHelper(context, name, factory, version) {
    var tabla = "CREATE TABLE EJERCICIOS(ID INT, NOMBRE TEXT, TIPO TEXT, PESO BOOLEAN)"

    var tabla2 = "CREATE TABLE RUTINAS(ID INT, NOMBRE TEXT, EJERCICIOS TEXT)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(tabla)
        db.execSQL(tabla2)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table ejercicios")
        db.execSQL("drop table rutinas")
    }

}
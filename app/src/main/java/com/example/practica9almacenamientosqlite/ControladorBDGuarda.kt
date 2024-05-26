package com.example.practica9almacenamientosqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.sql.SQLException

class ControladorBDGuarda(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context,name,factory,version) {
    override fun onCreate(dataBase: SQLiteDatabase?) {
        //Instruccion DDL (Create)
        val sql = "create table guardias (ID int primary key, sueldo real, " +
                "animal text, accion text, genero text )"

        try {
            dataBase?.execSQL(sql)
        } catch (e: SQLException) {
            Toast.makeText(
                null, "Error al crear la base de datos", Toast.LENGTH_SHORT
            ).show()

        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}
}
package com.example.practica9almacenamientosqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.sql.SQLException

class ControladorBDParques(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context,name,factory,version) {
    override fun onCreate(dataBase: SQLiteDatabase?) {
        //Instruccion DDL (Create)
        val sql = "create table parques (ID int primary key, tamaño real, " +
                "area text, horario text, tipoParque text )"

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
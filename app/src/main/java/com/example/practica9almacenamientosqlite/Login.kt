package com.example.practica9almacenamientosqlite

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast

class Login : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var password: EditText
    private lateinit var Datasave: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        user = findViewById(R.id.edtUsuario)
        password = findViewById(R.id.edtContrasenia)
        Datasave = findViewById(R.id.rbGuardarDatos)

    }

    fun onClick(view: View?){
        when(view?.id){
            R.id.btnIngresar -> ingresar()
            R.id.btnSalir -> salir()
        }
    }

    private fun guardarPreferencias(user: User) {
        //Intancias donde se almacenara la informacion
        val preferences: SharedPreferences = getSharedPreferences("preferenciasUsuario",
            MODE_PRIVATE)
        //Editar de preferencias, para agregar, asociando con preferencias
        val editor: SharedPreferences.Editor = preferences.edit()
        //Agregar las preferencias
        editor.putString("email",user.correo)
        editor.putString("password", user.contrasena)
        editor.putBoolean("guardado",user.guardado)
        //Guardarlas
        editor.apply()
    }//guardarPreferencias

    private fun salir() {
        finish()
    }

    private fun ingresar() {

        if(user.text.isNotEmpty() && password.text.isNotEmpty() &&
            user.text.isNotBlank() && password.text.isNotBlank()){

            val usr = User(user.text.toString(), password.text.toString(), true)
            if(Datasave.isChecked){
                guardarPreferencias(usr)
                Toast.makeText(this,"Se guardo en SharedPreferences",Toast.LENGTH_LONG).show()

            }
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this,"Capturar informacion", Toast.LENGTH_LONG).show()
        }
    }
}
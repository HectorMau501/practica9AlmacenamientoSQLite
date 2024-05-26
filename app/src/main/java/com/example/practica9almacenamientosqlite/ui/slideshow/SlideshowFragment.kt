package com.example.practica9almacenamientosqlite.ui.slideshow

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.practica9almacenamientosqlite.ControladorBDGuarda
import com.example.practica9almacenamientosqlite.ControladorBDParques
import com.example.practica9almacenamientosqlite.R
import com.example.practica9almacenamientosqlite.databinding.FragmentSlideshowBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.SQLException

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var btnBuscar: FloatingActionButton
    private lateinit var btnActualizar: FloatingActionButton
    private lateinit var btnEliminar: FloatingActionButton

    private lateinit var IDGuard: EditText
    private lateinit var salari: EditText
    private lateinit var feed: CheckBox
    private lateinit var shower: CheckBox
    private lateinit var action: Spinner
    private lateinit var actionSel: String

    private lateinit var male: RadioButton
    private lateinit var femele: RadioButton
    private lateinit var adaptador: ArrayAdapter<String>

    //Objeto para gestion de la BD
    private lateinit var admin2: ControladorBDGuarda

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val view: View = binding.root

        IDGuard= view.findViewById(R.id.IDNombreGuarda)
        salari = view.findViewById(R.id.editSueldo)
        feed = view.findViewById(R.id.cbAlimentar)
        shower = view.findViewById(R.id.cbBañar)
        male = view.findViewById(R.id.rbMasculino)
        femele = view.findViewById(R.id.rbFemenino)
        btnAgregar = view.findViewById(R.id.btnInsertar2)
        btnBuscar = view.findViewById(R.id.btnBuscar2)
        btnEliminar = view.findViewById(R.id.btnEliminar2)
        btnActualizar = view.findViewById(R.id.btnActualizar2)
        action = view.findViewById(R.id.spnAcciones)
        //Definir valores de nivel
        val opciones = resources.getStringArray(R.array.listaAcciones)
        //Vincular las opciones con el componente Spinner
        adaptador = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,opciones)
        action.adapter = adaptador
        //Opcion predeterminada
        actionSel = opciones[0]
        //Escucha para determinar la opcion Seleccionada del Spinner
        action.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                actionSel = opciones[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }//AdapterView

        admin2 = ControladorBDGuarda(requireContext(), "empresapatito2.db", null,1)


        //Eventos onClick
        btnAgregar.setOnClickListener { registrarGuardia() }
        btnBuscar.setOnClickListener { buscarGuardia() }
        btnActualizar.setOnClickListener { actualizarGuardia() }
        btnEliminar.setOnClickListener { eliminarGuardia() }



        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    private fun eliminarGuardia() {
//Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin2.writableDatabase

        //Variables para busqueda de dato y eliminar
        val id = IDGuard.text.toString()

        //Validar que exista el campo no este vacio
        if(id.isNotEmpty()){
            //Variable que almacene el numero de registros borrados
            //La instruccion delete requiere parametros para realizar el borrado, estos son:
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.delete("guardias", "ID = $id", null)
            bd.close()


            //Limpiar los campos de texto
            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            //Validad si existieron registros a borrar
            if(cantidad >  0) Toast.makeText(requireContext(), "Parque Eliminado",Toast.LENGTH_SHORT)
                .show() else Toast.makeText(
                requireContext(),
                "El numero de empleado no existe",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(requireContext(), " Ingresa numero de parque",Toast.LENGTH_SHORT)
        }
    }

    private fun actualizarGuardia() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin2.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val numero = IDGuard.text.toString()
        val sueldo = salari.text.toString()
        //Checkbox

        val animales = when {
            feed.isChecked -> "Alimentar animal"
            shower.isChecked -> "Bañas animal"
            else -> ""
        }
        //RadioButton

        val genero = when {
            male.isChecked -> "masculino"
            femele.isChecked -> "femenino"
            else -> ""
        }
        val spinnerValue = action.selectedItem.toString()


        //Validar que exista informacion registrada
        if(numero.isNotEmpty() && numero.isNotEmpty() && sueldo.isNotEmpty() && sueldo.isNotEmpty()) {
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("ID", numero)
            registro.put("sueldo", sueldo)
            registro.put("animal", animales)
            registro.put("accion", spinnerValue)
            registro.put("genero", genero)

            //Variable que indica el numero de registros actualizados
            //La instruccion update requiere parametros para realizar la actualizacion de datos
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.update("guardias", registro, "ID=$numero", null)

            //Cerrar la Bd
            bd.close()

            //Limpiar los campos de texto
            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            //Validar si existieron registros a borrar
            if (cantidad > 0) Toast.makeText(
                requireContext(),
                "Datos del parque actualizado",
                Toast.LENGTH_SHORT
            ).show() else Toast.makeText(
                requireContext(),
                "El numero de parque no existe",
                Toast.LENGTH_SHORT
            ).show()
            Toast.makeText(requireContext(), "Parque actualizado", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(), "Debes registrar primero los datos ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarGuardia() {
//Establacer el modo de apertura de la base datos en modo Escritura
        val bd = admin2.readableDatabase

        //Variable para busqueda de dato para obtener informacion
        val numeroID = IDGuard.text.toString()

        //Validar que el campo no este vacio
        if(numeroID.isNotEmpty()){
            //Objeto apunta al registro donde localice el dato, se le envia la instruccion sql de busqueda
            val fila = bd.rawQuery(
                "select sueldo, animal, accion, genero from guardias " +
                        "where ID = " + numeroID, null
            )
            //Validar
            if(fila.moveToFirst()){
                //Se coloca en los componentes los valores encontrados
                salari.setText(fila.getString(0))
                // Obtener el valor de la base de datos
                val isAlimentar= fila.getString(1)
                val isBañar = fila.getString(1)
                // Convertir los valores de la base de datos a booleanos
                val alimentar= isAlimentar == "Alimentar animal"
                val bañar = isBañar == "Bañas animal"
                feed.isChecked = alimentar
                shower.isChecked = bañar

                val spinnerValue = fila.getString(2)
                val spinnerPosition = adaptador.getPosition(spinnerValue)
                action.setSelection(spinnerPosition)

                val isMasculino = fila.getString(3)
                val isFemenino = fila.getString(3)
                // Convertir los valores de la base de datos a booleanos
                val masculino= isMasculino == "masculino"
                val femenino = isFemenino == "femenino"
                male.isChecked = masculino
                femele.isChecked = femenino

                //Se cierra la base de datos
                bd.close()
            }else{
                Toast.makeText(requireContext(),"Numero de guardia no existe",Toast.LENGTH_SHORT).show()
                IDGuard.setText("")
                IDGuard.requestFocus()
                bd.close()
            }
        }else{
            Toast.makeText(requireContext(),"Ingresa numero de guardia",Toast.LENGTH_SHORT).show()
            IDGuard.requestFocus()

        }    }

    private fun registrarGuardia() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin2.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val id = IDGuard.text.toString()
        val sueldo = salari.text.toString()
        val guardaAnimals = when {
            feed.isChecked -> "Alimentar animal"
            shower.isChecked -> "Bañas animal"
            else -> ""
        }
        val acciones = actionSel

        val generos = when {
            male.isChecked -> "masculino"
            femele.isChecked -> "femenino"
            else -> ""
        }

        //Validar que exista informacion registrada
        if(id.isNotEmpty() && id.isNotEmpty() && sueldo.isNotEmpty() && sueldo.isNotEmpty()){
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("ID", id)
            registro.put("sueldo", sueldo)
            registro.put("animal", guardaAnimals)
            registro.put("accion", acciones)
            registro.put("genero", generos)
            if(bd != null){
                //Almacenar los valores en la tabla
                try{
                    val x: Long = bd.insert("guardias",null,registro)
                }catch (e: SQLException){
                    Log.e("Exception","Error"+e.message.toString())
                }
                //Cerrar la Bd
                bd.close()
            }
            //Limpiar los campos de texto
            IDGuard.setText("")
            salari.setText("")
            feed.isChecked = false
            shower.isChecked = false
            male.isChecked = false
            femele.isChecked = false
            IDGuard.requestFocus()

            Toast.makeText(requireContext(), "Guardia registrado", Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(requireContext(), "Debes registrar primero los datos ", Toast.LENGTH_SHORT).show()
        }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.practica9almacenamientosqlite.ui.home

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
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.practica9almacenamientosqlite.ControladorBDParques
import com.example.practica9almacenamientosqlite.R
import com.example.practica9almacenamientosqlite.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.sql.SQLException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var btnAgregar: FloatingActionButton
    private lateinit var btnBuscar: FloatingActionButton
    private lateinit var btnActualizar: FloatingActionButton
    private lateinit var btnEliminar: FloatingActionButton

    private lateinit var IDPark: EditText
    private lateinit var size: EditText
    private lateinit var areaPlay: CheckBox
    private lateinit var spaces: CheckBox
    private lateinit var timeTable: Spinner
    private lateinit var timeSel: String

    private lateinit var lineal: RadioButton
    private lateinit var virtuals: RadioButton
    private lateinit var adaptador: ArrayAdapter<String>


    //Objeto para gestion de la BD
    private lateinit var admin: ControladorBDParques


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view: View = binding.root

        IDPark = view.findViewById(R.id.editIDParque)
        size = view.findViewById(R.id.editTamanio)
        areaPlay = view.findViewById(R.id.cbJuego)
        spaces = view.findViewById(R.id.cbReposo)
        lineal = view.findViewById(R.id.rbLineales)
        virtuals = view.findViewById(R.id.rbVirtuales)
        btnAgregar = view.findViewById(R.id.btnInsertar)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        btnEliminar = view.findViewById(R.id.btnEliminar)
        btnActualizar = view.findViewById(R.id.btnActualizar)
        timeTable = view.findViewById(R.id.spnHorario)
        //Definir valores de nivel
        val opciones = resources.getStringArray(R.array.listaHorario)
        //Vincular las opciones con el componente Spinner
        adaptador = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,opciones)
        timeTable.adapter = adaptador
        //Opcion predeterminada
        timeSel = opciones[0]
        //Escucha para determinar la opcion Seleccionada del Spinner
        timeTable.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                timeSel = opciones[p2]
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }//AdapterView

        admin = ControladorBDParques(requireContext(), "empresapatito.db", null,1)


        //Eventos onClick
        btnAgregar.setOnClickListener { registrarEmpleado() }
        btnBuscar.setOnClickListener { buscarEmpleado() }
        btnActualizar.setOnClickListener { actualizarEmpleado() }
        btnEliminar.setOnClickListener { eliminarEmpleado() }

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    private fun eliminarEmpleado() {
//Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para busqueda de dato y eliminar
        val id = IDPark.text.toString()

        //Validar que exista el campo no este vacio
        if(id.isNotEmpty()){
            //Variable que almacene el numero de registros borrados
            //La instruccion delete requiere parametros para realizar el borrado, estos son:
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.delete("parques", "ID = $id", null)
            bd.close()


            //Limpiar los campos de texto
            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

            //Validad si existieron registros a borrar
            if(cantidad >  0) Toast.makeText(requireContext(), "Parque Eliminado",Toast.LENGTH_SHORT)
                .show() else Toast.makeText(
                requireContext(),
                "El numero de empleado no existe",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            Toast.makeText(requireContext(), " Ingresa numero de parque",Toast.LENGTH_SHORT)
        }    }

    private fun actualizarEmpleado() {
//Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val numero = IDPark.text.toString()
        val tamanio = size.text.toString()
        //Checkbox

        val area = when {
            areaPlay.isChecked -> "Area de Juego"
            spaces.isChecked -> "Espacio de Reposo"
            else -> ""
        }
        //RadioButton

        val tipoParque = when {
            lineal.isChecked -> "lineal"
            virtuals.isChecked -> "virtual"
            else -> ""
        }
        val spinnerValue = timeTable.selectedItem.toString()


        //Validar que exista informacion registrada
        if(numero.isNotEmpty() && numero.isNotEmpty() && tamanio.isNotEmpty() && tamanio.isNotEmpty()) {
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("ID", numero)
            registro.put("tamaño", tamanio)
            registro.put("area", area)
            registro.put("horario", spinnerValue)
            registro.put("tipoParque", tipoParque)

            //Variable que indica el numero de registros actualizados
            //La instruccion update requiere parametros para realizar la actualizacion de datos
            //tabla, informacion por actualizar, clausala where condicion y sin parametros para la clausula
            val cantidad = bd.update("parques", registro, "ID=$numero", null)

            //Cerrar la Bd
            bd.close()

            //Limpiar los campos de texto
            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

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
        }    }

    private fun buscarEmpleado() {
        //Establacer el modo de apertura de la base datos en modo Escritura
        val bd = admin.readableDatabase

        //Variable para busqueda de dato para obtener informacion
        val numeroID = IDPark.text.toString()

        //Validar que el campo no este vacio
        if(numeroID.isNotEmpty()){
            //Objeto apunta al registro donde localice el dato, se le envia la instruccion sql de busqueda
            val fila = bd.rawQuery(
                "select tamaño, area, horario, tipoParque from parques " +
                        "where ID = " + numeroID, null
            )
            //Validar
            if(fila.moveToFirst()){
                //Se coloca en los componentes los valores encontrados
                size.setText(fila.getString(0))
                // Obtener el valor de la base de datos
                val isArea = fila.getString(1)
                val isSpaces = fila.getString(1)
                // Convertir los valores de la base de datos a booleanos
                val area= isArea == "Area de Juego"
                val space = isSpaces == "Espacio de Reposo"
                areaPlay.isChecked = area
                spaces.isChecked = space
                val spinnerValue = fila.getString(2)
                val spinnerPosition = adaptador.getPosition(spinnerValue)
                timeTable.setSelection(spinnerPosition)
                val isLinea = fila.getString(3)
                val isVirtual = fila.getString(3)
                // Convertir los valores de la base de datos a booleanos
                val linea= isLinea == "lineal"
                val virtual = isVirtual == "virtual"
                lineal.isChecked = linea
                virtuals.isChecked = virtual

                //Se cierra la base de datos
                bd.close()
            }else{
                Toast.makeText(requireContext(),"Numero de parque no existe",Toast.LENGTH_SHORT).show()
                IDPark.setText("")
                IDPark.requestFocus()
                bd.close()
            }
        }else{
            Toast.makeText(requireContext(),"Ingresa numero de empleado",Toast.LENGTH_SHORT).show()
            IDPark.requestFocus()

        }
    }

    private fun registrarEmpleado() {
        //Establecer ek modo de apertura de la base de datos en modo escritura
        val bd = admin.writableDatabase

        //Variables para obtener los valores de componentes graficos
        val id = IDPark.text.toString()
        val tamanio = size.text.toString()
        val parqueAreas = when {
            areaPlay.isChecked -> "Area de Juego"
            spaces.isChecked -> "Espacio de Reposo"
            else -> ""
        }
        val horarios = timeSel
        val parqueRadios = when {
            lineal.isChecked -> "lineal"
            virtuals.isChecked -> "virtual"
            else -> ""
        }

        //Validar que exista informacion registrada
        if(id.isNotEmpty() && id.isNotEmpty() && tamanio.isNotEmpty() && tamanio.isNotEmpty()){
            //Objeto que almacene los valores para enviar a la tabla
            val registro = ContentValues()

            //Referencias a los datos que pasar a la BD
            //Indicando como parametros el put el nombre del campo y el valor a insertar
            //El segundo proviene de los campos de texto
            registro.put("ID", id)
            registro.put("tamaño", tamanio)
            registro.put("area", parqueAreas)
            registro.put("horario", horarios)
            registro.put("tipoParque", parqueRadios)
            if(bd != null){
                //Almacenar los valores en la tabla
                try{
                    val x: Long = bd.insert("parques",null,registro)
                }catch (e: SQLException){
                    Log.e("Exception","Error"+e.message.toString())
                }
                //Cerrar la Bd
                bd.close()
            }
            //Limpiar los campos de texto
            IDPark.setText("")
            size.setText("")
            areaPlay.isChecked = false
            spaces.isChecked = false
            lineal.isChecked = false
            virtuals.isChecked = false
            IDPark.requestFocus()

            Toast.makeText(requireContext(), "Parque registrado", Toast.LENGTH_SHORT).show()

        }else{
            Toast.makeText(requireContext(), "Debes registrar primero los datos ", Toast.LENGTH_SHORT).show()
        }    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.practica9almacenamientosqlite.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.practica9almacenamientosqlite.ControladorBDParques
import com.example.practica9almacenamientosqlite.R
import com.example.practica9almacenamientosqlite.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    //Instancias de componentes
    private lateinit var etListado: EditText
    private lateinit var admin: ControladorBDParques

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val view: View = binding.root

        //Asociar la instancia con el componente
        etListado = view.findViewById(R.id.editDetalle)
        //Creacion de la base de datos, de manera local, cuyo parametros son:
        //contexto de la aplicacion, nomre de la bd, version
        admin = ControladorBDParques(requireContext(), "empresapatito.db", null, 1)



        val textView: TextView = binding.textGallery
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        // Define el modo de acceso a la bd
        val bd = admin.readableDatabase
        // Instancia del apuntador al registro de búsqueda
        val registro = bd.rawQuery("select * from parques order by ID", null)
        // Variable para la cantidad de registro obtenidos
        val n = registro.count
        // Variable para control de datos en el TextView
        var nr = 0
        // Limpiar el EditText antes de añadir nuevo contenido
        etListado.setText("")
        // Valido que existan registros de la bd
        if (n > 0) {
            // Mover el cursor al inicio de los registros obtenidos
            registro.moveToFirst()
            // Ciclo repetitivo para colocar la información dentro del TextView
            do {
                etListado.append(
                    "\nID:  ${registro.getString(0)}\n" +
                            "\nTamaño del parque: ${registro.getString(1)}\n" +
                            "\nTipo Area: ${registro.getString(2)}\n" +
                            "\nHorario: ${registro.getString(3)}\n" +
                            "\nTipo Parque: ${registro.getString(4)}\n\n"
                )
                nr++
            } while (registro.moveToNext()) // Si existen más registros
        } else {
            // Mensaje informativo que no hay campos
            Toast.makeText(requireContext(), "Sin registro de parques", Toast.LENGTH_SHORT).show()
        }

        bd.close()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
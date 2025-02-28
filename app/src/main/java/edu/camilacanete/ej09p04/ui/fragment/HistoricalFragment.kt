package edu.camilacanete.ej09p04.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import edu.camilacanete.ej09p04.databinding.FragmentRecordImcBinding
import edu.camilacanete.ej09p04.data.model.ImcRecord
import edu.camilacanete.ej09p04.helper.ItemTouchHelperCallback
import edu.camilacanete.ej09p04.ui.adapter.ImcRecordRecyclerAdapter
import edu.camilacanete.ej09p04.ui.viewmodel.IMCViewModel
import edu.camilacanete.ej09p04.ui.viewmodel.IMCViewModelFactory

class HistoricalFragment : Fragment() {

    /*
       Se usa un binding opcional para manejar correctamente el ciclo de vida del fragmento y evitar
       problemas de acceso a vistas después de que la vista sea destruida. Esto previene fugas de memoria
       y errores como NullPointerException al acceder a la vista cuando ya no está disponible.
     */
    private var _binging: FragmentRecordImcBinding? = null
    /*
       Se establece 'binding' con '!!' para forzar la desreferenciación de '_binding'
       y asegura de que el binding no sea null mientras la vista esté activa.
     */
    private val binding get() = _binging!!

    /*
        Se instancia el ViewModel usando activityViewModels para compartir datos entre fragmentos
        El ViewModel es creado utilizando una fábrica personalizada, necesaria cuando se pasa un Application.
     */
    private val imcViewModel: IMCViewModel by activityViewModels{
        IMCViewModelFactory(requireActivity().application)
    }
    private lateinit var adapter: ImcRecordRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binging = FragmentRecordImcBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cargar registros desde el archivo
        imcViewModel.loadRecords()

        // Inicializa el RecyclerView y el adaptador
        adapter = ImcRecordRecyclerAdapter(requireContext(), emptyList()){ record ->
            showDeleteConfirmationDialog(record) // Se pasa una función para manejar la eliminación
        }

        // Configurar RecyclerView para mostrar los registros en una lista vertical
        binding.recyclerviewRecordList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HistoricalFragment.adapter
        }

        // Configurar ItemTouchHelper para habilitar el deslizamiento (swipe)
        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback { swipePosition ->
            val record = imcViewModel.imcRecordList.value?.get(swipePosition)
            record?.let {
                val itemPosition = imcViewModel.imcRecordList.value?.indexOf(record) ?: -1
                if (itemPosition != -1) {
                    // Eliminar el registro del ViewModel
                    imcViewModel.deleteRecord(it.id ?: return@let)
                    adapter.notifyItemRemoved(itemPosition)

                    // Mostrar Snackbar para deshacer
                    Snackbar.make(binding.root, "Registro eliminado", Snackbar.LENGTH_SHORT).setAction("Deshacer") {
                        // Si se hace clic en "Deshacer", se vuelve a agregar el registro
                        imcViewModel.addRecord(record)
                        adapter.notifyItemInserted(itemPosition)
                    }.show()
                }
            }
        })

        // Adjuntar el ItemTouchHelper al RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.recyclerviewRecordList)

        // Observar cambios en la lista de registros del ViewModel
        // Si la lista está vacía se muestra el textView "No hay registros"
        imcViewModel.imcRecordList.observe(viewLifecycleOwner) { recordList ->
            adapter.updateRecords(recordList) // Actualiza el adaptador con los nuevos datos
            binding.textviewNorecords.visibility = if (recordList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    //Método muestra alert para confirmar o no eliminacion del registro
    private fun showDeleteConfirmationDialog (record: ImcRecord){
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Eliminar registro")
            .setMessage("¿Estás seguro de elimnar este registro")
            .setPositiveButton(android.R.string.ok){ _,_ ->
                val position = imcViewModel.imcRecordList.value?.indexOf(record) ?: -1
                if (position != -1) {
                    // Verificar si el id es no nulo antes de pasarlo
                    record.id?.let { id ->
                        imcViewModel.deleteRecord(id) // Pasar solo si id no es nulo
                        adapter.notifyItemRemoved(position)
                        Snackbar.make(binding.root, "Registro eliminado", Snackbar.LENGTH_SHORT).setAction("Deshacer"){
                            imcViewModel.addRecord(record)
                            adapter.notifyItemInserted(position)
                        }.show()
                    } ?: run {
                        // Si id es nulo, muestra un mensaje de error o realiza alguna acción
                        Snackbar.make(binding.root, "Error: id nulo", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel,null)
            .create()

        dialog.show()
    }

    //Se limpia el binding al destruir la vista para evitar fugas de memoria.
    override fun onDestroyView() {
        super.onDestroyView()
        _binging = null // Limpia la referencia al binding cuando la vista es destruida
    }

}
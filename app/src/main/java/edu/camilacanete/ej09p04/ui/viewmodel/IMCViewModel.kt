package edu.camilacanete.ej09p04.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.camilacanete.ej09p04.data.model.ImcRecord
import edu.camilacanete.ej09p04.helper.MyDBOpenHelper

/*
   Clase para el ViewModel de la aplicación, permite centralizar la gestión de los registros IMC
   Permite que los datos se mantengan separados de la UI, asegurando que sobrevivan a cambios de configuración como rotaciones.
 */
class IMCViewModel (application: Application) : AndroidViewModel(application) {
    private val dbHelper = MyDBOpenHelper(application)

    // Lista mutable de registros IMC
    private val _imcRecordList = MutableLiveData<List<ImcRecord>>(dbHelper.getAllRecords())
    val imcRecordList: LiveData<List<ImcRecord>> = _imcRecordList

    //Método para cargar registros en el histórico
    fun loadRecords() {
        val record = dbHelper.getAllRecords()
        _imcRecordList.postValue(record)
    }

    // Método para añadir un registro
    fun addRecord(record: ImcRecord) {
        dbHelper.addRecord(
            date = record.date,
            stateResult = record.stateResult,
            gender = record.gender,
            weight = record.weight,
            height = record.height,
            imcResult = record.imcResult
        )

        _imcRecordList.value = dbHelper.getAllRecords()
    }

    // Método para eliminar un registro
    fun deleteRecord(recordId : Long) {
        dbHelper.deleteRecord(recordId)
        loadRecords()
    }
}
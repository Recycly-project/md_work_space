package com.koaladev.recycly.ui.viewmodel

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.R
import com.koaladev.recycly.data.pref.UserModel
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.WasteRepository
import com.koaladev.recycly.data.response.UploadResponse
import com.koaladev.recycly.data.response.UserResponse
import com.koaladev.recycly.widget.PointsWidget
import kotlinx.coroutines.launch
import java.io.File

class RecyclyViewModel(
    application: Application,
    private val recyclyRepository: RecyclyRepository,
    private val wasteRepository: WasteRepository
): AndroidViewModel(application) {

    private val _points = MutableLiveData<Int>()
    val points: LiveData<Int> get() = _points

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    val uploadResult: LiveData<Result<UploadResponse>> get() = _uploadResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _userData = MutableLiveData<UserResponse>()
    val userData: LiveData<UserResponse> get() = _userData

    fun getSession(): LiveData<UserModel> {
        return recyclyRepository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            recyclyRepository.logout()
        }
    }

    init {
        refreshPoints()
    }
    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun addPoints(newPoints: Int) {
        val currentPoints = _points.value ?: 0
        val updatedPoints = currentPoints + newPoints
        updatePoints(updatedPoints)
    }

    fun minsPoints(newPoints: Int) {
        val currentPoints = _points.value ?: 0
        val updatedPoints = currentPoints - newPoints
        updatePoints(updatedPoints)
    }

    fun refreshPoints() {
        val sharedPref = getApplication<Application>().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedPoints = sharedPref.getInt("POINTS", 0)
        updatePoints(savedPoints)
    }
    
    private fun updatePoints(newPoints: Int) {
        _points.value = newPoints
        
        val sharedPref = getApplication<Application>().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("POINTS", newPoints)
            apply()
        }
    
        Log.d("RecyclyViewModel", "Points updated: $newPoints")
        updateWidget(getApplication())
    }

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = wasteRepository.uploadImage(file)
                _uploadResult.value = result
                if (result.isSuccess) {
                    result.getOrNull()?.points ?.let {
                        updatePoints(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Upload failed", e)
                _uploadResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateWidget(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, PointsWidget::class.java)
        )
        val currentPoints = _points.value ?: 0
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.points_widget)
            views.setTextViewText(R.id.tv_points_value, currentPoints.toString())
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    fun getUserById(id: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = recyclyRepository.getUserById(id, token)
                if (response.status == "success") {
                    _isLoading.value = false
                    _userData.value = response
                    response.data.user.totalPoints.let { newPoints ->
                        updatePoints(newPoints)
                    }
                }
            } catch (e: Exception) {
                Log.e("RecyclyViewModel", "Error fetching user data", e)
            }
        }
    }
}

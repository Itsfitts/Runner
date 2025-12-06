package yangfentuozi.runner.app.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _installTermModUri = MutableStateFlow<Uri?>(null)
    val installTermModUri: StateFlow<Uri?> = _installTermModUri.asStateFlow()
    private val _uninstallTermModId = MutableStateFlow<String?>(null)
    val uninstallTermModId: StateFlow<String?> = _uninstallTermModId.asStateFlow()
    private val _purge = MutableStateFlow<Boolean?>(null)
    val purge: StateFlow<Boolean?> = _purge.asStateFlow()

    private val _isInstalling = MutableStateFlow(false)
    val isInstalling: StateFlow<Boolean> = _isInstalling.asStateFlow()

    fun setInstallTermModUri(uri: Uri?) {
        _installTermModUri.value = uri
    }

    fun setUninstallTermModId(moduleId: String?, purge: Boolean?) {
        _uninstallTermModId.value = moduleId
        _purge.value = purge
    }

    fun setInstalling(installing: Boolean) {
        _isInstalling.value = installing
    }
}


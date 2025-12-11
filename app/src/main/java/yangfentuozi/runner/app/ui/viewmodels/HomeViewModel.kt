package yangfentuozi.runner.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import yangfentuozi.runner.app.Runner
import yangfentuozi.runner.app.ui.screens.main.HideAllDialogs

class HomeViewModel(application: Application) : AndroidViewModel(application), HideAllDialogs {
    private val _refreshTrigger = MutableStateFlow(0)
    val refreshTrigger: StateFlow<Int> = _refreshTrigger.asStateFlow()

    private val _showRemoveTermExtConfirmDialog = MutableStateFlow(false)
    val showRemoveTermExtConfirmDialog: StateFlow<Boolean> = _showRemoveTermExtConfirmDialog.asStateFlow()

    // Shizuku 权限监听器
    private val shizukuPermissionListener = Runner.ShizukuPermissionListener {
        triggerRefresh()
    }

    // Shizuku 状态监听器
    private val shizukuStatusListener = Runner.ShizukuStatusListener {
        triggerRefresh()
    }

    // 服务状态监听器
    private val serviceStatusListener = Runner.ServiceStatusListener {
        triggerRefresh()
    }

    init {
        // 初始化时刷新状态
        viewModelScope.launch(Dispatchers.IO) {
            Runner.refreshStatus()
        }

        // 注册监听器
        Runner.addShizukuPermissionListener(shizukuPermissionListener)
        Runner.addShizukuStatusListener(shizukuStatusListener)
        Runner.addServiceStatusListener(serviceStatusListener)
    }

    fun triggerRefresh() {
        _refreshTrigger.value++
    }

    fun tryBindService() {
        viewModelScope.launch(Dispatchers.IO) {
            Runner.tryBindService()
        }
    }

    fun requestPermission() {
        Runner.requestPermission()
    }

    override fun onCleared() {
        super.onCleared()
        // 移除监听器
        Runner.removeShizukuPermissionListener(shizukuPermissionListener)
        Runner.removeShizukuStatusListener(shizukuStatusListener)
        Runner.removeServiceStatusListener(serviceStatusListener)
    }

    override fun hideAllDialogs() {
    }
}


package yangfentuozi.runner.app.ui.screens.main.module

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import yangfentuozi.runner.R
import yangfentuozi.runner.app.Runner
import yangfentuozi.runner.app.ui.components.ContentWithAutoHideFloatActionButton
import yangfentuozi.runner.app.ui.screens.main.module.components.ModuleItem
import yangfentuozi.runner.app.ui.screens.main.settings.components.CheckboxItem
import yangfentuozi.runner.app.ui.theme.AppSpacing
import yangfentuozi.runner.app.ui.viewmodels.ModuleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(
    viewModel: ModuleViewModel = viewModel(),
    onNavigateToInstallTermMod: ((Uri) -> Unit),
    onNavigateToUninstallTermMod: ((String, Boolean) -> Unit)
) {
    val modules by viewModel.modules.collectAsState()
    val showUninstallDialog by viewModel.showUninstallDialog.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    // 监听生命周期事件
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    viewModel.loadModules()
                }

                Lifecycle.Event.ON_STOP -> {
                    viewModel.hideAllDialogs()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val context = LocalContext.current

    // 文件选择器用于安装 Term 模块
    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == "application/zip") {
                onNavigateToInstallTermMod(uri)
            }
        }
    }

    ContentWithAutoHideFloatActionButton(
        content = {
            if (modules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (!Runner.pingServer()) {
                            stringResource(R.string.service_not_running)
                        } else {
                            stringResource(R.string.no_modules)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = AppSpacing.topBarContentSpacing,
                        bottom = AppSpacing.screenBottomPadding,
                        start = AppSpacing.screenHorizontalPadding,
                        end = AppSpacing.screenHorizontalPadding
                    ),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.cardSpacing)
                ) {
                    items(modules) { module ->
                        ModuleItem(
                            moduleInfo = module,
                            viewModel = viewModel,
                            onUninstall = {
                                viewModel.showUninstallDialog(module.moduleId)
                            }
                        )
                    }
                }
            }
        },
        onClickFAB = {
            if (Runner.pingServer()) {
                pickFileLauncher.launch("application/zip")
            }
        },
        contentFAB = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.install_term_ext)
            )
        }
    )

    if (showUninstallDialog != null) {
        var purge by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { viewModel.hideUninstallDialog() },
            title = { Text(stringResource(R.string.uninstall_term_ext)) },
            text = {
                Column {
                    CheckboxItem(
                        title = stringResource(R.string.purge_uninstall),
                        checked = purge,
                        onCheckedChange = { purge = it },
                        enabled = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onNavigateToUninstallTermMod(showUninstallDialog!!, purge)
                        viewModel.hideUninstallDialog()
                    }
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideUninstallDialog() }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

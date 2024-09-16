package app.revenge.manager.installer.step.installing

import android.content.Context
import app.revenge.manager.R
import app.revenge.manager.domain.manager.InstallMethod
import app.revenge.manager.domain.manager.PreferenceManager
import app.revenge.manager.installer.Installer
import app.revenge.manager.installer.session.SessionInstaller
import app.revenge.manager.installer.shizuku.ShizukuInstaller
import app.revenge.manager.installer.step.Step
import app.revenge.manager.installer.step.StepGroup
import app.revenge.manager.installer.step.StepRunner
import app.revenge.manager.utils.isMiui
import org.koin.core.component.inject
import java.io.File

/**
 * Installs all the modified splits with the users desired [Installer]
 *
 * @see SessionInstaller
 * @see ShizukuInstaller
 *
 * @param lspatchedDir Where all the patched APKs are
 */
class InstallStep(
    private val lspatchedDir: File
): Step() {

    private val preferences: PreferenceManager by inject()
    private val context: Context by inject()

    override val group = StepGroup.INSTALLING
    override val nameRes = R.string.step_installing

    override suspend fun run(runner: StepRunner) {
        runner.logger.i("Installing apks")
        val files = lspatchedDir.listFiles()
            ?.takeIf { it.isNotEmpty() }
            ?: throw Error("Missing APKs from LSPatch step; failure likely")

        val installer: Installer = when (preferences.installMethod) {
            InstallMethod.DEFAULT -> SessionInstaller(context)
            InstallMethod.SHIZUKU -> ShizukuInstaller(context)
        }

        installer.installApks(silent = !isMiui, *files)
    }

}
package me.cirq.obfdetector.analyzer

import me.cirq.obfdetector.Config
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.ApkMeta


object ManifestAnalyzer {

    private val manifest: ApkMeta by lazy {
        val apkFile = Config.get().apkFile.toFile()
        ApkFile(apkFile).apkMeta
    }

    val packageName: String
        get() = manifest.packageName

    val targetSdkVersion: Int
        get() = manifest.targetSdkVersion?.toInt()?:-1

}

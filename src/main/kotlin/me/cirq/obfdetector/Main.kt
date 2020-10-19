package me.cirq.obfdetector

import me.cirq.obfdetector.analyzer.ManifestAnalyzer
import me.cirq.obfdetector.analyzer.PrecomputeAnalyzer
import me.cirq.obfdetector.util.LogUtil


private class Main
private val self = Main::class.java

fun main(args: Array<String>) {
    if(args.isNotEmpty() && args[0] == "LocaL") {  // only used for IDE debugging
        Config.get().init(
                "-s", "C:\\Users\\cirq\\Desktop\\arp-tmp\\android-platforms",

                """C:\Users\cirq\Desktop\arp-tmp\apks\org.kaqui_63.apk"""
        )
    }
    else {
        Config.get().init(*args)
    }


    val pkg = ManifestAnalyzer.packageName
    LogUtil.info(self, "Start soot pre-processing: $pkg")
    PrecomputeAnalyzer.runSootPack()
    PrecomputeAnalyzer.judgeObfuscation()
    LogUtil.info(self, "Precomputing finished")

    LogUtil.info(self, "=== finished ===")
}

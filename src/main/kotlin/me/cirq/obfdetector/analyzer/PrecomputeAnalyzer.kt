package me.cirq.obfdetector.analyzer

import me.cirq.obfdetector.Config
import me.cirq.obfdetector.util.CGUtil
import me.cirq.obfdetector.util.LogUtil
import me.cirq.obfdetector.util.addTransform
import me.cirq.obfdetector.util.apply_options
import soot.PackManager
import soot.Scene
import soot.SceneTransformer
import soot.options.Options
import java.util.*
import kotlin.collections.HashMap


object PrecomputeAnalyzer {

    fun runSootPack() {
        CGUtil.constructCG()

        Scene.v().apply_options {
            Config.get().excludedPkgs?.also{
                LogUtil.info(this, "should exclude")
                set_exclude(it)
                set_no_bodies_for_excluded(true)
            }
            set_whole_program(true)
            set_allow_phantom_refs(true)
            set_process_multiple_dex(true)
            set_force_android_jar(Config.get().versionSdkFile.toString())
            set_soot_classpath(Config.get().versionSdkFile.toString())
            set_process_dir(listOf(Config.get().apkFile.toString()))
            set_src_prec(Options.src_prec_apk)
            set_output_format(Options.output_format_none)
            set_validate(false)
            set_verbose(true)
            set_debug(true)
        }
        Scene.v().loadNecessaryClasses()

        PackManager.v().getPack("wjap").apply {
            addTransform("wjap.obfscan", ObfuscationDetectTransformer())
        }
        PackManager.v().runPacks()
    }

    fun judgeObfuscation() {
        val lengthCounter: MutableMap<Int, Int> = java.util.HashMap()
        var totalMembers = 0
        ObfuscationDetectTransformer.membersMap.forEach { (_, members) ->
            for (member in members) {
                totalMembers++
                val len = member.length
                val newLen = lengthCounter.getOrDefault(len, 0) + 1
                lengthCounter[len] = newLen
            }
        }
        val shortNames = (lengthCounter[1]?:0) + (lengthCounter[2]?:0)
        val ratio = shortNames.toFloat() / totalMembers
        LogUtil.info(this, "Shortname ratio: $ratio")

        if(ratio > Config.get().obfuscationThreshold)
            throw RuntimeException("Obfuscated APk!")
    }

}


class ObfuscationDetectTransformer: SceneTransformer() {

    companion object {
        val membersMap: MutableMap<String, MutableList<String>> = HashMap()
    }

    override fun internalTransform(phaseName: String, options: Map<String, String>) {
        Scene.v().applicationClasses.forEach { cls ->
            val mbs: MutableList<String> = LinkedList()
            cls.name.takeIf { "\$" !in it }?.let{ mbs.add(it) }
            cls.fields.forEach { mbs.add(it.name) }
            cls.methods.filter { it.name !in setOf("<init>", "<clinit>") }
                    .forEach { mbs.add(it.name) }
            val clsname = cls.name
            if (membersMap.containsKey(clsname))
                membersMap[clsname]!! += mbs
            else
                membersMap[clsname] = mbs
        }
    }

}

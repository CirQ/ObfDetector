package me.cirq.obfdetector.util

import me.cirq.obfdetector.Config
import soot.Scene
import soot.jimple.infoflow.InfoflowConfiguration
import soot.jimple.infoflow.InfoflowConfiguration.CallgraphAlgorithm
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.config.SootConfigForAndroid
import soot.jimple.toolkits.callgraph.CallGraph
import soot.options.Options


object CGUtil {

    private object CGHolder {
        lateinit var cg: CallGraph
    }

    fun constructCG() {
        LogUtil.info(this, "Start constructing call graph")
        CGHolder.cg = generateByFlowdroid()
    }

    private fun generateByFlowdroid(): CallGraph {
        val infoflowApplication = SetupApplication(Config.get().versionSdkFile.toString(),
                Config.get().apkFile.toString())
        infoflowApplication.sootConfig = object : SootConfigForAndroid() {
            override fun setSootOptions(options: Options?, config: InfoflowConfiguration?) {
                super.setSootOptions(options, config)
                config!!.callgraphAlgorithm = CallgraphAlgorithm.CHA
                Scene.v().init()
                Scene.v().apply_options {
                    Config.get().excludedPkgs?.also{
                        LogUtil.info(this, "should exclude")
                        set_exclude(it)
                        set_no_bodies_for_excluded(true)
                    }
                    set_force_android_jar(Config.get().versionSdkFile.toString())
                    set_soot_classpath(Config.get().versionSdkFile.toString())
                    set_process_dir(listOf(Config.get().apkFile.toString()))
                    set_src_prec(Options.src_prec_apk)
                    set_output_format(Options.output_format_dex)
                }
            }
        }
        infoflowApplication.constructCallgraph()
        return Scene.v().callGraph
    }

}

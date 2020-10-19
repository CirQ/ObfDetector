package me.cirq.obfdetector.util

import soot.Pack
import soot.Scene
import soot.Transform
import soot.Transformer
import soot.options.Options


fun Scene.init() {
    soot.G.reset()
    Options.v().apply {
        set_allow_phantom_refs(true)
        set_whole_program(true)
        set_prepend_classpath(true)
        set_process_multiple_dex(true)
        set_validate(true)
    }
}

fun Scene.apply_options(block: Options.() -> Unit) {
    Options.v().apply(block)
}

fun Pack.addTransform(phaseName: String, t: Transformer) {
    add(Transform(phaseName, t))
}

package me.cirq.obfdetector.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object LogUtil {

    private val logger: MutableMap<Class<*>,Logger> = HashMap()

    private fun getLogger(obj: Any): Logger {
        val clazz: Class<*> = if (obj is Class<*>) obj else obj::class.java
        if(clazz !in logger)
            logger[clazz] = LoggerFactory.getLogger(clazz)
        return logger[clazz]!!
    }


    @JvmStatic
    fun info(obj: Any, format: String, vararg arguments: Any) {
        getLogger(obj).info(format, *arguments)
    }

    @JvmStatic
    fun error(obj: Any, format: String, vararg arguments: Any) {
        getLogger(obj).error(format, *arguments)
    }

}

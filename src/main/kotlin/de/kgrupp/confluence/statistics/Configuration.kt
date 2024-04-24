package de.kgrupp.confluence.statistics

import java.util.Properties
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val CONFIG_FILE_NAME = "configuration.properties"

class Configuration {
    private val properties = Properties()

    init {
        val file = this::class.java.classLoader.getResourceAsStream(CONFIG_FILE_NAME)
        properties.load(file)
    }

    fun getProperty(key: String): String = properties.getProperty(key)

    fun getBaseUrl(): String = properties.getProperty("baseUrl")

    @OptIn(ExperimentalEncodingApi::class)
    fun getBasicAuth(): String {
        val userName = properties.getProperty("userName")
        val password = properties.getProperty("password")
        return "Basic " + Base64.encode("$userName:$password".toByteArray())
    }
}
package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.rest.model.ConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceSpace
import khttp.get

class ConfluenceRestApi {

    private val configuration: Configuration = Configuration()
    private val objectMapper: ObjectMapper = ObjectMapper()
    init {
        objectMapper.registerModule(KotlinModule.Builder().build())
    }

    fun getSpaces(): List<ConfluenceSpace> {
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/spaces?limit=250",
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        return objectMapper.readValue(response.text, ResultConfluenceSpace::class.java).results
    }
}
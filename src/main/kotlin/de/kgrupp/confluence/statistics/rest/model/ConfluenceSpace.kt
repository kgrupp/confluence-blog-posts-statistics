package de.kgrupp.confluence.statistics.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfluenceSpace(val id: String, val name: String, val key: String)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultConfluenceSpace(var results: List<ConfluenceSpace>, var _links: ResultLinks)
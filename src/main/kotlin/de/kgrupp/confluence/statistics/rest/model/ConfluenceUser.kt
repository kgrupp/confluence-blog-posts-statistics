package de.kgrupp.confluence.statistics.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfluenceUser(val accountId: String, val email: String, val displayName: String)
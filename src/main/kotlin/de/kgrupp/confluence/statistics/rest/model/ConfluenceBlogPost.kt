package de.kgrupp.confluence.statistics.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfluenceBlogPost(
    val id: String,
    val title: String,
    val authorId: String,
    val createdAt: Instant,
    val _links: ConfluenceBlogPostLinks
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfluenceBlogPostLinks(val tinyui: String, val webui: String)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultConfluenceBlogPost(var results: List<ConfluenceBlogPost>, var _links: ResultLinks)

data class ConfluenceBlogPostLikeCount(val count: Int)
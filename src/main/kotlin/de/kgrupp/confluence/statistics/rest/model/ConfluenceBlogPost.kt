package de.kgrupp.confluence.statistics.rest.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConfluenceBlogPost(
    val id: String,
    val title: String,
    val authorId: String,
    val createdAt: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultConfluenceBlogPost(var results: List<ConfluenceBlogPost>, var _links: ResultLinks)

data class ConfluenceBlogPostLikeCount(val count: Int)
package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.rest.model.ConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ConfluenceBlogPostLikeCount
import de.kgrupp.confluence.statistics.rest.model.ConfluenceUser
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ResultLinks
import khttp.get

class ConfluenceRestApi(val configuration: Configuration) {

    private val objectMapper: ObjectMapper = ObjectMapper()
    init {
        objectMapper.registerModule(KotlinModule.Builder().build())
    }

    fun getSpaces(): List<ConfluenceSpace> {
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/spaces",
            params = mapOf("limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        val result = objectMapper.readValue(response.text, ResultConfluenceSpace::class.java)
        return result.results + pageThroughSpaces(result._links)
    }

    private fun pageThroughSpaces(resultLinks: ResultLinks): List<ConfluenceSpace> {
        if (resultLinks.next == null) {
            return emptyList()
        }
        val response = get(
            url = "${configuration.getBaseUrl()}${resultLinks.next}",
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        val result = objectMapper.readValue(response.text, ResultConfluenceSpace::class.java)
        return result.results + pageThroughSpaces(result._links)
    }

    fun getBlotPosts(space: ConfluenceSpace): List<ConfluenceBlogPost> {
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/blogposts",
            params = mapOf("space-id" to space.id, "sort" to "-created-date", "limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        val result = objectMapper.readValue(response.text, ResultConfluenceBlogPost::class.java)
        return result.results + pageThroughBlogPosts(result._links)
    }

    private fun pageThroughBlogPosts(resultLinks: ResultLinks): List<ConfluenceBlogPost> {
        if (resultLinks.next == null) {
            return emptyList()
        }
        val response = get(
            url = "${configuration.getBaseUrl()}${resultLinks.next}",
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        val result = objectMapper.readValue(response.text, ResultConfluenceBlogPost::class.java)
        return result.results + pageThroughBlogPosts(result._links)
    }

    fun getLikeCountForBlogPost(blogPost: ConfluenceBlogPost): ConfluenceBlogPostLikeCount {
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/blogposts/${blogPost.id}/likes/count",
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        return objectMapper.readValue(response.text, ConfluenceBlogPostLikeCount::class.java)
    }

    fun getUser(accountId: String): ConfluenceUser {
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/rest/api/user",
            params = mapOf("accountId" to accountId),
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        return objectMapper.readValue(response.text, ConfluenceUser::class.java)
    }
}
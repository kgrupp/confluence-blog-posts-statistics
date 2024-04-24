package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.rest.model.ConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ConfluenceBlogPostLikeCount
import de.kgrupp.confluence.statistics.rest.model.ConfluenceUser
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceSpace
import khttp.get

class ConfluenceRestApi {

    private val configuration: Configuration = Configuration()
    private val objectMapper: ObjectMapper = ObjectMapper()
    init {
        objectMapper.registerModule(KotlinModule.Builder().build())
    }

    fun getSpaces(): List<ConfluenceSpace> {// TODO page through entries
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/spaces",
            params = mapOf("limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        return objectMapper.readValue(response.text, ResultConfluenceSpace::class.java).results
    }

    fun getBlotPosts(space: ConfluenceSpace): List<ConfluenceBlogPost> {// TODO page through entries
        val response = get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/blogposts",
            params = mapOf("space-id" to space.id, "sort" to "-created-date", "limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth())
        )
        return objectMapper.readValue(response.text, ResultConfluenceBlogPost::class.java).results
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
        println(response.text)
        return objectMapper.readValue(response.text, ConfluenceUser::class.java)
    }
}
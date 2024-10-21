package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.rest.model.ConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ConfluenceGraphQlReactionSummary
import de.kgrupp.confluence.statistics.rest.model.ConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ConfluenceUser
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceBlogPost
import de.kgrupp.confluence.statistics.rest.model.ResultConfluenceSpace
import de.kgrupp.confluence.statistics.rest.model.ResultLinks
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId

class ConfluenceRestApi(val configuration: Configuration) {

    private val objectMapper: ObjectMapper = ObjectMapper()
    private val client = HttpClient(CIO)

    init {
        objectMapper.registerModule(KotlinModule.Builder().build())
        objectMapper.registerModule(JavaTimeModule())
    }

    fun getSpaces(): List<ConfluenceSpace> {
        val response = runBlocking {
            client.get("${configuration.getBaseUrl()}/wiki/api/v2/spaces") {
                headers {
                    append("Authorization", configuration.getBasicAuth())
                    append("Accept", "application/json")
                }
                parameter("limit", "250")
            }
        }
        val body = runBlocking { response.call.response.bodyAsText() }
        val result = objectMapper.readValue(body, ResultConfluenceSpace::class.java)
        return result.results + pageThroughSpaces(result._links)
    }

    private fun pageThroughSpaces(resultLinks: ResultLinks): List<ConfluenceSpace> {
        if (resultLinks.next == null) {
            return emptyList()
        }
        val response =
            runBlocking {
                client.get("${configuration.getBaseUrl()}${resultLinks.next}") {
                    headers {
                        append("Authorization", configuration.getBasicAuth())
                    }
                }
            }
        val result = objectMapper.readValue(runBlocking { response.call.response.bodyAsText() }, ResultConfluenceSpace::class.java)
        return result.results + pageThroughSpaces(result._links)
    }

    fun getBlogPosts(space: ConfluenceSpace, minCreatedDate: LocalDate): List<ConfluenceBlogPost> {
        val response = runBlocking {
            client.get("${configuration.getBaseUrl()}/wiki/api/v2/blogposts") {
                headers {
                    append("Authorization", configuration.getBasicAuth())
                }
                parameter("space-id", space.id)
                parameter("sort", "-created-date")
                parameter("limit", "250")
            }
        }
        val result = objectMapper.readValue(runBlocking { response.call.response.bodyAsText() }, ResultConfluenceBlogPost::class.java)
        val breakCondition: (ConfluenceBlogPost) -> Boolean = {
            it.createdAt.atZone(ZoneId.of("UTC")).toLocalDate() < minCreatedDate
        }
        return (result.results + pageThroughBlogPosts(result._links, breakCondition)).filter {
            breakCondition(it).not()
        }
    }

    private fun pageThroughBlogPosts(
        resultLinks: ResultLinks,
        breakCondition: (ConfluenceBlogPost) -> Boolean
    ): List<ConfluenceBlogPost> {
        if (resultLinks.next == null) {
            return emptyList()
        }
        val response = runBlocking {
            client.get("${configuration.getBaseUrl()}${resultLinks.next}") {
                headers {
                    append("Authorization", configuration.getBasicAuth())
                }
            }
        }
        val result = objectMapper.readValue(runBlocking { response.call.response.bodyAsText() }, ResultConfluenceBlogPost::class.java)
        return if (result.results.any(breakCondition)) {
            result.results
        } else {
            result.results + pageThroughBlogPosts(result._links, breakCondition)
        }
    }

    /**
     * We are using the internal confluence graphql api here to get the reactions count for a blog
     * post.
     */
    fun getLikeCountForBlogPost(blogPost: ConfluenceBlogPost): ConfluenceGraphQlReactionSummary {
        val response = runBlocking {
            client.post("${configuration.getBaseUrl()}/cgraphql?q=ReactionsPlaceholderQuery") {
                headers {
                    append("Authorization", configuration.getBasicAuth())
                    append("Accept", "application/json")
                    append("Content-Type", "application/json")
                }
                setBody(
                    """
                    {
                    		"operationName": "ReactionsPlaceholderQuery",
                    		"query": "query ReactionsPlaceholderQuery(${"$"}contentId: ID!) { content(id: ${"$"}contentId) { nodes { id contentReactionsSummary { reactionsSummaryForEmoji { id count emojiId } } } } }",
                    		"variables": {
                    			"contentId": "${blogPost.id}"
                    		}
                    	}
                    """.trimIndent()
                )
            }
        }
        return objectMapper.readValue(runBlocking { response.call.response.bodyAsText() }, ConfluenceGraphQlReactionSummary::class.java)
    }

    fun getUser(accountId: String): ConfluenceUser {
        val response = runBlocking {
            client.get("${configuration.getBaseUrl()}/wiki/rest/api/user") {
                headers {
                    append("Authorization", configuration.getBasicAuth())
                }
                parameter("accountId", accountId)
            }
        }
        return objectMapper.readValue(runBlocking { response.call.response.bodyAsText() }, ConfluenceUser::class.java)
    }
}

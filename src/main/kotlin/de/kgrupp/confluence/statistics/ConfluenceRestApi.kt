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
import java.time.LocalDate
import java.time.ZoneId
import khttp.get
import khttp.post

class ConfluenceRestApi(val configuration: Configuration) {

  private val objectMapper: ObjectMapper = ObjectMapper()

  init {
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerModule(JavaTimeModule())
  }

  fun getSpaces(): List<ConfluenceSpace> {
    val response =
        get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/spaces",
            params = mapOf("limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth()))
    val result = objectMapper.readValue(response.text, ResultConfluenceSpace::class.java)
    return result.results + pageThroughSpaces(result._links)
  }

  private fun pageThroughSpaces(resultLinks: ResultLinks): List<ConfluenceSpace> {
    if (resultLinks.next == null) {
      return emptyList()
    }
    val response =
        get(
            url = "${configuration.getBaseUrl()}${resultLinks.next}",
            headers = mapOf("Authorization" to configuration.getBasicAuth()))
    val result = objectMapper.readValue(response.text, ResultConfluenceSpace::class.java)
    return result.results + pageThroughSpaces(result._links)
  }

  fun getBlogPosts(space: ConfluenceSpace, minCreatedDate: LocalDate): List<ConfluenceBlogPost> {
    val response =
        get(
            url = "${configuration.getBaseUrl()}/wiki/api/v2/blogposts",
            params = mapOf("space-id" to space.id, "sort" to "-created-date", "limit" to "250"),
            headers = mapOf("Authorization" to configuration.getBasicAuth()))
    val result = objectMapper.readValue(response.text, ResultConfluenceBlogPost::class.java)
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
    val response =
        get(
            url = "${configuration.getBaseUrl()}${resultLinks.next}",
            headers = mapOf("Authorization" to configuration.getBasicAuth()))
    val result = objectMapper.readValue(response.text, ResultConfluenceBlogPost::class.java)
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
    val response =
        post(
            url = "${configuration.getBaseUrl()}/cgraphql?q=ReactionsPlaceholderQuery",
            data =
                """
                    {
                    		"operationName": "ReactionsPlaceholderQuery",
                    		"query": "query ReactionsPlaceholderQuery(${"$"}contentId: ID!) { content(id: ${"$"}contentId) { nodes { id contentReactionsSummary { reactionsSummaryForEmoji { id count emojiId } } } } }",
                    		"variables": {
                    			"contentId": "${blogPost.id}"
                    		}
                    	}
                """
                    .trimIndent(),
            headers =
                mapOf(
                    "Authorization" to configuration.getBasicAuth(),
                    "Accept" to "application/json",
                    "Content-Type" to "application/json"))
    return objectMapper.readValue(response.text, ConfluenceGraphQlReactionSummary::class.java)
  }

  fun getUser(accountId: String): ConfluenceUser {
    val response =
        get(
            url = "${configuration.getBaseUrl()}/wiki/rest/api/user",
            params = mapOf("accountId" to accountId),
            headers = mapOf("Authorization" to configuration.getBasicAuth()))
    return objectMapper.readValue(response.text, ConfluenceUser::class.java)
  }
}

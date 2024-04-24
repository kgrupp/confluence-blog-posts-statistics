package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.model.BlogPost
import de.kgrupp.confluence.statistics.model.User
import de.kgrupp.confluence.statistics.model.UserStatistics

class Statistics {
  private val configuration: Configuration = Configuration()
  private val confluenceRestApi: ConfluenceRestApi = ConfluenceRestApi(configuration)

  fun get(confluenceSpaces: List<String>) {
    val filteredSpaces = confluenceRestApi.getSpaces().filter { confluenceSpaces.contains(it.key) }
    val blogPosts = filteredSpaces.flatMap { confluenceRestApi.getBlotPosts(it) }
    val userMap = HashMap<String, User>()
    val blogPostModels =
        blogPosts.map {
          val likeCount = confluenceRestApi.getLikeCountForBlogPost(it)
          val user =
              userMap.getOrPut(it.authorId) {
                confluenceRestApi.getUser(it.authorId).let {
                  User(id = it.accountId, name = it.displayName, emailAddress = it.email)
                }
              }
          BlogPost(
              id = it.id,
              title = it.title,
              author = user,
              likeCount = likeCount.count,
              createdAt = it.createdAt,
              link = "${configuration.getBaseUrl()}/wiki${it._links.tinyui}")
        }
    val authorStatistics =
        blogPostModels
            .groupBy { it.author }
            .map { (user, blogPosts) ->
              UserStatistics(
                  user = user,
                  totalBlogPosts = blogPosts.size,
                  totalLikes = blogPosts.map { it.likeCount }.reduce { a, b -> a + b },
                  popularBlogPosts = blogPosts.sortedBy { -it.likeCount }.take(3))
            }
            .sortedBy { -it.totalBlogPosts }
    println(authorStatistics.convertToJson())
  }

  fun List<UserStatistics>.convertToJson(): String {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(KotlinModule.Builder().build())
    return objectMapper.writeValueAsString(this)
  }
}

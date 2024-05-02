package de.kgrupp.confluence.statistics

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.kgrupp.confluence.statistics.model.BlogPost
import de.kgrupp.confluence.statistics.model.User
import de.kgrupp.confluence.statistics.model.UserStatistics
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

class Statistics {
  private val configuration: Configuration = Configuration()
  private val confluenceRestApi: ConfluenceRestApi = ConfluenceRestApi(configuration)

  fun get(confluenceSpaces: List<String>, minCreatedDate: LocalDate) {
    val filteredSpaces = confluenceRestApi.getSpaces().filter { confluenceSpaces.contains(it.key) }
    val blogPosts = filteredSpaces.flatMap { confluenceRestApi.getBlogPosts(it) }
    val userMap = HashMap<String, User>()
    val blogPostModels =
        blogPosts
            .map {
              val likeCount =
                  confluenceRestApi
                      .getLikeCountForBlogPost(it).data.content.nodes
                      .flatMap { it.contentReactionsSummary.reactionsSummaryForEmoji }
                      .map { it.count }
                      .sum()
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
                  likeCount = likeCount,
                  createdAt = it.createdAt,
                  link = "${configuration.getBaseUrl()}/wiki${it._links.tinyui}")
            }
            .filter { minCreatedDate.isBefore(it.createdAt.atZone(ZoneId.of("UTC")).toLocalDate()) }
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
            .sortedWith({ a, b ->
              if (a.totalBlogPosts == b.totalBlogPosts) b.totalLikes.compareTo(a.totalLikes)
              else b.totalBlogPosts.compareTo(a.totalBlogPosts)
            })
    println(authorStatistics.convertToJson())
    authorStatistics.writeToFile(File("visualize/src/data/statistics.json"))
  }

  fun List<UserStatistics>.convertToJson(): String {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerModule(JavaTimeModule())
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
  }

  fun List<UserStatistics>.writeToFile(file: File) {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerModule(JavaTimeModule())
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this)
  }
}

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
    val blogPosts = filteredSpaces.flatMap { confluenceRestApi.getBlogPosts(it, minCreatedDate) }
    val userMap = HashMap<String, User>()
    val blogPostModels =
        blogPosts
            .map { blogPost ->
              val likeCount =
                  confluenceRestApi
                      .getLikeCountForBlogPost(blogPost)
                      .data
                      .content
                      .nodes
                      .flatMap { it.contentReactionsSummary.reactionsSummaryForEmoji }
                      .sumOf { it.count }
              val user =
                  userMap.getOrPut(blogPost.authorId) {
                    confluenceRestApi.getUser(blogPost.authorId).let {
                      User(id = it.accountId, name = it.displayName, emailAddress = it.email)
                    }
                  }
              BlogPost(
                  id = blogPost.id,
                  title = blogPost.title,
                  author = user,
                  likeCount = likeCount,
                  createdAt = blogPost.createdAt,
                  link = "${configuration.getBaseUrl()}/wiki${blogPost._links.tinyui}")
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
                  popularBlogPosts = blogPosts.sortedBy { -it.likeCount }.take(5))
            }
            .sortedWith { a, b ->
              if (a.totalBlogPosts == b.totalBlogPosts) b.totalLikes.compareTo(a.totalLikes)
              else b.totalBlogPosts.compareTo(a.totalBlogPosts)
            }
    println(authorStatistics.convertToMarkdown())
    authorStatistics.convertToMarkdown().writeToFile(File("statistics.md"))
    authorStatistics.writeToFile(File("visualize/src/data/statistics.json"))
  }

  private fun List<UserStatistics>.writeToFile(file: File) {
    val objectMapper = ObjectMapper()
    objectMapper.registerModule(KotlinModule.Builder().build())
    objectMapper.registerModule(JavaTimeModule())
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, this)
  }

  private fun String.writeToFile(file: File) {
    file.writeText(this)
  }
}

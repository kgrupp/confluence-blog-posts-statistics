package de.kgrupp.confluence.statistics

import de.kgrupp.confluence.statistics.model.UserStatistics
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun List<UserStatistics>.convertToMarkdown(minCreatedDate: LocalDate): String {
  val builder = StringBuilder()
  val totalBlogPosts = this.sumOf { it.totalBlogPosts }
  val totalBlogPostLikes = this.sumOf { it.totalLikes }
    builder.appendLine("# Statistics")
    builder.append("\uD83D\uDCDC $totalBlogPosts")
    builder.appendLine(" | \uD83D\uDC4D $totalBlogPostLikes")
  builder.appendLine("# Top recent blog posts")
  val oneMonthAgo = LocalDate.now().minusMonths(1)
  builder.appendLine("Created after ${oneMonthAgo.formatToDate()}")
  val topRecentBlogPosts =
      this.asSequence()
          .flatMap { it.popularBlogPosts }
          .filter { it.createdAt.atZone(ZoneId.of("UTC")).toLocalDate().isAfter(oneMonthAgo) }
          .sortedBy { it.createdAt}
          .sortedBy { -it.likeCount }
          .take(15)
          .toList()
  topRecentBlogPosts.forEachIndexed { index, blogPost ->
    builder.append("${index + 1}. [${blogPost.title}](${blogPost.link})")
    builder.appendLine(" | \uD83D\uDCDC ${blogPost.author.name}")
    builder.appendLine(" | \uD83D\uDC4D ${blogPost.likeCount}")
  }

  builder.appendLine("# Global Ranking")
    builder.appendLine("Starting at ${minCreatedDate.formatToDate()}")

  this.forEachIndexed { index, userStatistics ->
    val ranking = getRanking(index)
    builder.append("## ${ranking} ${userStatistics.user.name}")
    builder.append(" | \uD83D\uDCDC ${userStatistics.totalBlogPosts}")
    builder.append(" | \uD83D\uDC4D ${userStatistics.totalLikes}")
      builder.appendLine()
    userStatistics.popularBlogPosts.take(5).forEachIndexed { i, blogPost ->
      builder.appendLine("${i + 1}. [${blogPost.title}](${blogPost.link})")
      builder.appendLine(" | \uD83D\uDC4D ${blogPost.likeCount}\n")
    }
    builder.appendLine()
  }
  return builder.toString()
}

private fun getRanking(index: Int): String {
  val ranking =
      if (index < 3) {
        when (index) {
          0 -> "🥇"
          1 -> "🥈"
          2 -> "🥉"
          else -> ""
        }
      } else {
        "${index + 1}."
      }
  return ranking
}

private fun LocalDate.formatToDate(): String {
  return this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
}
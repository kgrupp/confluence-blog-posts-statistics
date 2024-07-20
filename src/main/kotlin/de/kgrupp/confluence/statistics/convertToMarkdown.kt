package de.kgrupp.confluence.statistics

import de.kgrupp.confluence.statistics.model.UserStatistics

/**
 * Generate a ranking with the number and the name as header. Content contains number of blog posts
 * and likes and all popular blog posts.
 */
fun List<UserStatistics>.convertToMarkdown(): String {
  val builder = StringBuilder()
  this.forEachIndexed { index, userStatistics ->
    val ranking = getRanking(index)
    builder.append("## ${ranking} ${userStatistics.user.name}")
    builder.append(" | \uD83D\uDCDC ${userStatistics.totalBlogPosts}")
    builder.append(" | \uD83D\uDC4D ${userStatistics.totalLikes}\n")
    builder.append("### Popular blog posts\n")
    userStatistics.popularBlogPosts.forEachIndexed { i, blogPost ->
      builder.append("${i + 1}. [${blogPost.title}](${blogPost.link})")
      builder.append(" | \uD83D\uDC4D ${blogPost.likeCount}\n")
    }
    builder.append("\n")
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

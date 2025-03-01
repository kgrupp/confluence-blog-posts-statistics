package de.kgrupp.confluence.statistics

import de.kgrupp.confluence.statistics.model.UserStatistics
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private const val MAX_BLOG_POSTS_PER_AUTHOR_SHOWN = 15

fun List<UserStatistics>.convertToMarkdown(minCreatedDate: LocalDate, recentBlogPostsMinDate: LocalDate): String {
    val builder = StringBuilder()
    val totalAuthors = this.size
    val totalBlogPosts = this.sumOf { it.totalBlogPosts }
    val totalBlogPostLikes = this.sumOf { it.totalLikes }
    builder.appendLine("# Statistics")
    builder.append("\uD83D\uDCDD $totalAuthors")
    builder.append(" | \uD83D\uDCDC $totalBlogPosts")
    builder.append(" | \uD83D\uDC4D $totalBlogPostLikes")
    builder.appendLine()
    builder.appendLine("# Top recent blog posts")
    builder.appendLine("Created after ${recentBlogPostsMinDate.formatToDate()}")
    val topRecentBlogPosts =
        this.asSequence()
            .flatMap { it.popularBlogPosts }
            .filter { it.createdAt.atZone(ZoneId.of("UTC")).toLocalDate().isEqualOrAfter(recentBlogPostsMinDate) }
            .sortedBy { it.createdAt }
            .sortedBy { -it.likeCount }
            .take(15)
            .toList()
    topRecentBlogPosts.forEachIndexed { index, blogPost ->
        builder.append("* [${blogPost.title.replaceBreakingCharacters()}](${blogPost.link})")
        builder.append(" | \uD83D\uDCDD ${blogPost.author.name}")
        if (blogPost.likeCount > 0) {
            builder.append(" | \uD83D\uDC4D ${blogPost.likeCount}")
        }
        builder.appendLine()
    }

    builder.appendLine("# Global Ranking")
    builder.appendLine("Starting at ${minCreatedDate.formatToDate()}")

    this.forEachIndexed { index, userStatistics ->
        val ranking = getRanking(index)
        builder.append("## ${ranking} ${userStatistics.user.name}")
        builder.append(" | \uD83D\uDCDC ${userStatistics.totalBlogPosts}")
        if (userStatistics.totalLikes > 0) {
            builder.append(" | \uD83D\uDC4D ${userStatistics.totalLikes}")
        }
        builder.appendLine()
        userStatistics.popularBlogPosts.take(MAX_BLOG_POSTS_PER_AUTHOR_SHOWN).forEachIndexed { i,
                                                                                               blogPost ->
            builder.append("* [${blogPost.title.replaceBreakingCharacters()}](${blogPost.link})")
            if (blogPost.likeCount > 0) {
                builder.append(" | \uD83D\uDC4D ${blogPost.likeCount}")
            }
            builder.appendLine()
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

private fun String.replaceBreakingCharacters(): String {
    return this.replace("[", "(").replace("]", ")")
}

private fun LocalDate.isEqualOrAfter(date: LocalDate): Boolean {
    return this.isEqual(date) || this.isAfter(date)
}
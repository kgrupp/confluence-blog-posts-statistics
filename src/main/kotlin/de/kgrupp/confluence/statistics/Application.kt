package de.kgrupp.confluence.statistics

import java.time.LocalDate
import java.util.Locale

fun main(args: Array<String>) {
    Locale.setDefault(Locale.ENGLISH)
    val confluenceSpaces = args[0].split(',')
    val minCreatedDate = LocalDate.parse(args[1])
    val maxCreatedDate = if (args.size > 2) LocalDate.parse(args[2]) else null
    val recentBlogPostsMinDate = if (args.size > 3) LocalDate.parse(args[3]) else LocalDate.now().minusMonths(1)
    val statistics = Statistics()
    println(statistics.get(confluenceSpaces, minCreatedDate, maxCreatedDate, recentBlogPostsMinDate))
}

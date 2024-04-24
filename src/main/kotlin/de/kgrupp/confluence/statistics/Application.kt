package de.kgrupp.confluence.statistics

import java.time.LocalDate

fun main(args: Array<String>) {
    val confluenceSpaces = args[0].split(',')
    val minCreatedDate = LocalDate.parse(args[1])
    val statistics = Statistics()
    println(statistics.get(confluenceSpaces, minCreatedDate))
}
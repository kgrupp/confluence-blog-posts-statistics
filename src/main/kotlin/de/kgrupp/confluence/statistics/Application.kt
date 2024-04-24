package de.kgrupp.confluence.statistics

fun main(args: Array<String>) {
    val confluenceSpaces = args[0].split(',')
    val statistics = Statistics()
    println(statistics.get(confluenceSpaces))
}
package de.kgrupp.confluence.statistics

class Statistics {
    private val confluenceRestApi: ConfluenceRestApi = ConfluenceRestApi()

    fun get(confluenceSpaces: List<String>) {
        val filteredSpaces = confluenceRestApi.getSpaces().filter { confluenceSpaces.contains(it.key) }
        println(filteredSpaces)
        TODO("")
    }
}
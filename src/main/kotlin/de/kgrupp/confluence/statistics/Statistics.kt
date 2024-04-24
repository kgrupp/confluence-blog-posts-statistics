package de.kgrupp.confluence.statistics

import de.kgrupp.confluence.statistics.model.BlogPost
import de.kgrupp.confluence.statistics.model.User

class Statistics {
    private val confluenceRestApi: ConfluenceRestApi = ConfluenceRestApi()

    fun get(confluenceSpaces: List<String>) {
        val filteredSpaces = confluenceRestApi.getSpaces().filter { confluenceSpaces.contains(it.key) }
        val blogPosts = filteredSpaces.flatMap {
            confluenceRestApi.getBlotPosts(it)
        }
        val userMap = HashMap<String, User>()
        val blogPostModel = blogPosts.map {
            val likeCount = confluenceRestApi.getLikeCountForBlogPost(it)
            val user = userMap.getOrPut(it.authorId) { confluenceRestApi.getUser(it.authorId).let { User(id = it.accountId, name = it.displayName, emailAddress = it.email) } }
            BlogPost(id = it.id, title = it.title, author = user, likeCount = likeCount.count, createdAt = it.createdAt)
        }
        println(blogPostModel)
    }
}
package de.kgrupp.confluence.statistics.model

data class BlogPost(
    val id: String,
    val title: String,
    val author: User,
    val likeCount: Int,
    val createdAt: String
)
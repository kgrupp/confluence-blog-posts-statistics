package de.kgrupp.confluence.statistics.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class BlogPost(
    val id: String,
    val title: String,
    @JsonIgnore
    val author: User,
    val likeCount: Int,
    val createdAt: String,
    val link: String
)
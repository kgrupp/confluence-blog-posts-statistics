package de.kgrupp.confluence.statistics.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class BlogPost(
    val id: String,
    val title: String,
    @JsonIgnore
    val author: User,
    val likeCount: Int,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "UTC")
    val createdAt: Instant,
    val link: String
)
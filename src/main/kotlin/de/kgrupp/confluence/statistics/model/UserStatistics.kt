package de.kgrupp.confluence.statistics.model

data class UserStatistics(val user: User, val totalBlogPosts: Int, val totalLikes: Int, val popularBlogPosts: List<BlogPost>)
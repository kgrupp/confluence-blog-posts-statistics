package de.kgrupp.confluence.statistics.rest.model

data class ConfluenceGraphQlReactionSummary(val data: Data) {
    data class Data(val content: Content)
    data class Content(val nodes: List<Node>)
    data class Node(val id: String, val contentReactionsSummary: ContentReactionsSummary)
    data class ContentReactionsSummary(val reactionsSummaryForEmoji: List<ReactionsSummaryForEmoji>)
    data class ReactionsSummaryForEmoji(val id: String, val count: Int, val emojiId: String)
}
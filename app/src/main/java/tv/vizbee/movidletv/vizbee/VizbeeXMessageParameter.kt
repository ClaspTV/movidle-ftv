package tv.vizbee.movidletv.vizbee

enum class VizbeeXMessageParameter(val value: String) {
    MESSAGE_TYPE("msgType"),
    CHANNEL_ID("channelId"),
    STATUS("status"),

    MOVIE_NAME("movieName"),
    MOVIES("movies"),
    CLIP_ID("clipId"),
    CLIP_SCORE("clipScore"),

    USER_ID("userId"),
    USER_NAME("userName"),

    SCORE("score")
}
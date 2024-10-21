package tv.vizbee.movidletv.vizbee

enum class VizbeeXMessageType(val value: String) {
    CREATE_GAME("create_game"),
    JOIN_GAME("join_game"),
    START_GAME("start_game"),
    GAME_STATUS("game_status"),
    SCORE_UPDATE("score_update"),
}
package tv.vizbee.movidletv.data.model

import org.json.JSONObject
import tv.vizbee.movidletv.vizbee.VizbeeXMessageParameter

data class Player(var userName: String, val userId: String, var score: String = "0") {
    fun getJson(): JSONObject {
        return JSONObject().apply {
            put(VizbeeXMessageParameter.USER_ID.value, userId)
            put(VizbeeXMessageParameter.USER_NAME.value, userName)
            put(VizbeeXMessageParameter.SCORE.value, score)
        }
    }

    fun getJsonWithoutScore(): JSONObject {
        return JSONObject().apply {
            put(VizbeeXMessageParameter.USER_ID.value, userId)
            put(VizbeeXMessageParameter.USER_NAME.value, userName)
        }
    }
}
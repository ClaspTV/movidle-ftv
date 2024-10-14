package tv.vizbee.movidletv.model

object VideoStorage {
    private val movieList = arrayListOf(
        arrayListOf(
            "https://drive.google.com/uc?export=download&id=1EH5TFyTOkfYrHW7BYZkH5vX76JMSsWKf",
            "https://drive.google.com/uc?export=download&id=1RltUYkfNUIek3ovvaAs9DXhwIFAbBenC",
            "https://drive.google.com/uc?export=download&id=1mdrl24s16Na4KgRQloGsKVJttTnC41D5",
            "https://drive.google.com/uc?export=download&id=1sGw7Ww223I6ePPbBzMw9YS6UO4b6eBOQ",
            "https://drive.google.com/uc?export=download&id=1a7MUPR7dyNjCCaHe0p9xoNI7kiE1oG1d"
        )
    )

    fun getMovie(contentPosition: Int): List<String>? {
        if (contentPosition >= movieList.size) {
            return null
        }
        return movieList[contentPosition]
    }

}
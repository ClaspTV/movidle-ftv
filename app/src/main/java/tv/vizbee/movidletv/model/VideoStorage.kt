package tv.vizbee.movidletv.model

import tv.vizbee.movidletv.model.movie.MovieItem
import tv.vizbee.movidletv.vizbee.VizbeeWrapper

object VideoStorage {
    private val movieList = VizbeeWrapper.movies

    fun getMovie(contentPosition: Int): MovieItem? {
        if (contentPosition >= movieList.size) {
            return null
        }
        return movieList[contentPosition]
    }

    fun getMovieClip(contentPosition: Int, clipPosition: Int): String? {
        val clips = getMovie(contentPosition)?.clips

        if (clipPosition >= (clips?.size ?: 0)) {
            return null
        }
        return clips?.get(clipPosition)?.url
    }
}
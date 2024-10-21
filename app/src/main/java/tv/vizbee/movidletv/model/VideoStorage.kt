package tv.vizbee.movidletv.model

import tv.vizbee.movidletv.model.movie.MovieCLipItem
import tv.vizbee.movidletv.model.movie.MovieItem
import tv.vizbee.movidletv.vizbee.VizbeeXWrapper

object VideoStorage {
    private val movieList = VizbeeXWrapper.movies

    fun getMovie(contentPosition: Int): MovieItem? {
        if (contentPosition >= movieList.size) {
            return null
        }
        return movieList[contentPosition]
    }

    fun getMovieClip(contentPosition: Int, clipPosition: Int): MovieCLipItem? {
        val clips = getMovie(contentPosition)?.clips

        if (clipPosition >= (clips?.size ?: 0)) {
            return null
        }
        return clips?.get(clipPosition)
    }
}
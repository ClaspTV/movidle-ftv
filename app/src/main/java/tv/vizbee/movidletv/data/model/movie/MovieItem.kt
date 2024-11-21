package tv.vizbee.movidletv.data.model.movie

data class MovieItem(
    var name: String? = null,
    var id: String? = null,
    var clips: ArrayList<MovieCLipItem> = arrayListOf()
)
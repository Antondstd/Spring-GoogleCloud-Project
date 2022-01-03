package ru.itmo.cloud_backend.DTO

import ru.itmo.cloud_backend.Models.Activity


class ActivityDTO(
    var id: Long = 0,
    var name: String,
    var description: String,
    var posterUrl: String? = null,
    var miniPosterUrl: String? = null,
    var stages: List<StageDTO>
) {

    //    constructor(activity: Activity) : this() {
////        for (stage in activityDTO.stages)
////            this.stages.add(Stage(stage))
////        this.user = user
//    }
//    constructor(name: String,activity: Activity):this( name = name, activity = activity){
//
//    }
    companion object {
        fun fromActivity(activity: Activity): ActivityDTO {
            val newDTO = ActivityDTO(
                id = activity.id,
                name = activity.name, description = activity.description,
                posterUrl = "https://storage.googleapis.com/${activity.poster_url}",
                miniPosterUrl = "https://storage.googleapis.com/${activity.mini_poster_url}",
                stages = activity.stages.map { StageDTO(it) })
            return newDTO
        }
    }
}
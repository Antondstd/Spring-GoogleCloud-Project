package ru.itmo.cloud_backend.DTO

import ru.itmo.cloud_backend.Models.Stage

class StageDTO(
    var id:Long = 0,
    var name:String,
    var description:String?
) {
    constructor(stage: Stage) : this(
        id = stage.id,
        name = stage.name,
        description = stage.description
    )
}
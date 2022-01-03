package ru.itmo.cloud_backend.Models

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.itmo.cloud_backend.DTO.StageDTO
import javax.persistence.*

@Entity
class Stage(
    var name: String,
    var description: String? = null,

    @JsonIgnore
    @ManyToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "activity_id", nullable = false)
    var activity: Activity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "result_id")
    var id: Long = 0

    @OneToMany(cascade = arrayOf(CascadeType.ALL))
    var records: List<Record> = mutableListOf()

    constructor(stageDTO: StageDTO, activity: Activity) : this(
        name = stageDTO.name,
        description = stageDTO.description,
        activity = activity

    )
}
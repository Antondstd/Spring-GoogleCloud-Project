package ru.itmo.cloud_backend.Models

import com.fasterxml.jackson.annotation.JsonIgnore
import ru.itmo.cloud_backend.DTO.ActivityDTO
import javax.persistence.*

@Entity
class Activity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "activity_id")
    var id: Long = 0,
    var name: String,
    var description: String,
    var poster_url: String,
    var mini_poster_url: String,
) {
    @JsonIgnore
    @ManyToOne(cascade = arrayOf(CascadeType.DETACH))
    @JoinColumn(name = "user_id")
    lateinit var user: User


    @OneToMany(targetEntity = Stage::class,cascade = arrayOf(CascadeType.ALL),mappedBy="activity")
    var stages: MutableList<Stage> = mutableListOf()

    @OneToMany(targetEntity = Result::class,cascade = arrayOf(CascadeType.ALL),mappedBy="activity")
    var results: MutableList<Result> = mutableListOf()

    constructor(activityDTO: ActivityDTO, poster_url: String,mini_poster_url: String, user: User) : this(
        id = 0,
        name = activityDTO.name,
        description = activityDTO.description,
        poster_url = poster_url,
        mini_poster_url = mini_poster_url
    ) {
        this.user = user
    }

}
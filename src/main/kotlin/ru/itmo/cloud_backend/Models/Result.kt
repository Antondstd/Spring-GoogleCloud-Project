package ru.itmo.cloud_backend.Models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime
import javax.persistence.*

@Entity
class Result(
    var startTime:ZonedDateTime = ZonedDateTime.now(),
    var endTime:ZonedDateTime? = null,
    var totalTimeSeconds:Int = 0,
    @ManyToOne(cascade = arrayOf(CascadeType.DETACH))
    @JoinColumn(name = "user_id")
    var user: User,
    @JsonIgnore
    @ManyToOne(targetEntity = Activity::class)
    @JoinColumn(name = "activity_id",nullable = false)
    var activity: Activity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "result_id")
    var id: Long = 0

    @OneToMany(targetEntity = Record::class,cascade = arrayOf(CascadeType.ALL),mappedBy="result")
    var records:MutableList<Record> = mutableListOf()
}
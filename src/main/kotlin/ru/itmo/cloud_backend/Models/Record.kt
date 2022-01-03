package ru.itmo.cloud_backend.Models

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Record(
    var time:Int = 0,
    @JsonIgnore
    @ManyToOne(cascade = arrayOf(CascadeType.ALL))
    @JoinColumn(name = "stage_id")
    var stage: Stage,
    @JsonIgnore
    @ManyToOne(targetEntity = Result::class)
    @JoinColumn(name = "result_id",nullable = false)
     var result: Result
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "record_id")
    var id: Long = 0
}
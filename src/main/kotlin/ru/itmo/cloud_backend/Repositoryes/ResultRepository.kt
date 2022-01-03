package ru.itmo.cloud_backend.Repositoryes

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.cloud_backend.Models.Activity
import ru.itmo.cloud_backend.Models.Result


interface ResultRepository: JpaRepository<Result, Long> {
//    fun findActivitiesByNameIgnoreCaseContaining(name:String,pagination: Pageable): Page<Activity>
    fun findByUserId(id:Long,pageable: Pageable):Page<Result>
}
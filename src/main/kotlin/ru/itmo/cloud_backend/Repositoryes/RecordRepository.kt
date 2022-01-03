package ru.itmo.cloud_backend.Repositoryes

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.cloud_backend.Models.Record

interface RecordRepository: JpaRepository<Record,Long> {
}
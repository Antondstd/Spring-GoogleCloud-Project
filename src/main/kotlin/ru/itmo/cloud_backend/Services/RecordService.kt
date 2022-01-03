package ru.itmo.cloud_backend.Services

import org.springframework.stereotype.Service
import ru.itmo.cloud_backend.Repositoryes.RecordRepository

@Service
class RecordService(private val recordRepository: RecordRepository) : RecordRepository by recordRepository {
}
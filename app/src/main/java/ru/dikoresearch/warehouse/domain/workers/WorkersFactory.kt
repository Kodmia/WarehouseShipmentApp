package ru.dikoresearch.warehouse.domain.workers

import androidx.work.DelegatingWorkerFactory
import ru.dikoresearch.warehouse.domain.repository.WarehouseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkersFactory @Inject constructor(
    warehouseRepository: WarehouseRepository
): DelegatingWorkerFactory() {
    init {
        addFactory(ImageUploadWorkerFactory(warehouseRepository))
    }
}
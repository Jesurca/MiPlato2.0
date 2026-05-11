package com.miplato.app.datos.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AlimentoEntidad::class,
        UsuarioEntidad::class,
        ConsumoDiarioEntidad::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BaseDatosMiPlato : RoomDatabase() {
    abstract fun miPlatoDao(): MiPlatoDao
}

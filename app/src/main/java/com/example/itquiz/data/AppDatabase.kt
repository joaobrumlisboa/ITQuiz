package com.example.itquiz.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Score::class], version = 2, exportSchema = false) // Aumente a versão para 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "score_database"
                )
                    .fallbackToDestructiveMigration() // Adicione isso para permitir a recriação do banco
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

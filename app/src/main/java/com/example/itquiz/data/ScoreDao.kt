package com.example.itquiz.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    fun insertScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY points DESC LIMIT 10")
    fun getAllScores(): List<Score>
}



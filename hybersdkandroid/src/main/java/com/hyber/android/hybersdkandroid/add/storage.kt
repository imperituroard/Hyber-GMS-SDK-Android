package com.hyber.android.hybersdkandroid.add

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.RoomDatabase
import androidx.room.Database
import androidx.room.Room
import kotlin.properties.Delegates


class HyberStorage(context: Context) {

    //any classes initialization
    private var context: Context by Delegates.notNull()

    //main class initialization
    init {
        this.context = context
    }


    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String?,
        @ColumnInfo(name = "last_name") val lastName: String?
    )


    @Dao
    interface UserDao {
        @Query("SELECT * FROM user")
        fun getAll(): List<User>

        @Query("SELECT * FROM user WHERE uid IN (:userIds)")
        fun loadAllByIds(userIds: IntArray): List<User>

        @Query(
            "SELECT * FROM user WHERE first_name LIKE :first AND " +
                    "last_name LIKE :last LIMIT 1"
        )
        fun findByName(first: String, last: String): User

        @Insert
        fun insertAll(vararg users: User)

        @Delete
        fun delete(user: User)
    }

    @Database(entities = arrayOf(User::class), version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun userDao(): UserDao
    }

    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database-name"
    ).build()


}


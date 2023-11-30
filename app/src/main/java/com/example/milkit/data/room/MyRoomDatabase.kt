package com.example.milkit.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.milkit.MyApplication
import com.example.milkit.presentation.cart.CartModel
import com.example.milkit.presentation.cart.StringListConverter


@Database(entities = [(CartModel::class)], version = 3, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getInstance(): MyRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        MyApplication.applicationContext,
                        MyRoomDatabase::class.java,
                        "cartDatabase"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
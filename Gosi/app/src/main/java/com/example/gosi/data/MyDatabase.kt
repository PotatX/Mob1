package com.example.gosi.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.gosi.models.Item

@Database(entities = [Item::class], version = 1)
abstract class MyDatabase : RoomDatabase(){
    abstract fun itemDao() : ItemDao

    companion object {
        private var instance: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase{
            if (instance == null){
                instance = Room
                    .databaseBuilder(context, MyDatabase::class.java, "newNewDatabase")
                    .allowMainThreadQueries()
                    .build()
            }

            return instance as MyDatabase
        }
    }
}


@Dao
interface ItemDao{
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE id == (:id)")
    fun get(id: Int): Item

    @Query("SELECT * FROM item WHERE state == ('InUse')")
    fun loadInUse(): List<Item>

    @Query("SELECT * FROM item WHERE state == ('IsNew')")
    fun loadNew(): List<Item>

    @Query("SELECT * FROM item WHERE state == ('IsDeleted')")
    fun loadWasDeleted(): List<Item>

    @Insert
    fun insert(item: Item)

    @Insert
    fun insertAll(items: List<Item>)

    @Delete
    fun delete(item: Item)

    @Update
    fun update(item: Item)

    @Query("DELETE FROM item")
    fun deleteAll()
}